package com.clebs.celerity.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityTicketsBinding
import com.clebs.celerity.databinding.DialogUploadAlertBinding
import com.clebs.celerity.models.requests.SaveTicketDataRequestBody
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getCurrentDateTime
import com.clebs.celerity.utils.showToast
import org.jetbrains.anko.startActivityForResult

class CreateTicketsActivity : AppCompatActivity() {
    private val PICK_FILE_REQUEST_CODE = 100
    lateinit var mbinding: ActivityTicketsBinding
    lateinit var viewmodel: MainViewModel
    lateinit var repo: MainRepo
    var selectedDeptID: Int = -1
    var selectedRequestTypeID: Int = -1
    var apiCount = 0
    var title: String? = null
    lateinit var pref: Prefs
    var desc: String? = null
    lateinit var loadingDialog: LoadingDialog
    private var selectedFileUri: Uri? = null
    private lateinit var uploadFileLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_tickets)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        repo = MainRepo(apiService)
        pref = Prefs(this)
        viewmodel =
            ViewModelProvider(this, MyViewModelFactory(repo))[MainViewModel::class.java]
        loadingDialog = LoadingDialog(this)

        observers()
        viewmodel.GetUserDepartmentList()
        showDialog()
        setInputListener(mbinding.edtTitle)
        setInputListener(mbinding.edtDes)

        mbinding.saveTickets.setOnClickListener {
            if (chkNull())
                showToast("Please complete form first!!", this)
            else
                saveTicket()
        }


        uploadFileLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->

                uri?.let { selectedFileUri ->

                }
            }

        mbinding.icUpload.setOnClickListener {

        }

        mbinding.imageViewBack.setOnClickListener {
            onBackPressed()
        }

        mbinding.cancel.setOnClickListener {
            onBackPressed()
        }


        val spinnerNamesWithPlaceholder = listOf<String>()
        val spinnerIdsWithPlaceholder = listOf<Int>()
        setSpinners(mbinding.tvRequests, spinnerNamesWithPlaceholder, spinnerIdsWithPlaceholder)

        mbinding.tvdepart.setOnClickListener {
            mbinding.rvList.visibility = View.VISIBLE
        }
    }

    private fun chkNull(): Boolean {
        return selectedDeptID == -1 || selectedRequestTypeID == -1 || title == null || desc == null
    }

    private fun saveTicket() {
        var currDt = getCurrentDateTime()
        val request = SaveTicketDataRequestBody(
            AssignedToUserIDs = listOf(),
            BadgeComment = "undefined",
            BadgeReturnedStatusId = 0,
            DaTestDate = currDt,
            DaTestTime = currDt,
            Description = desc!!,
            DriverId = pref.userID.toInt(),
            EstCompletionDate = currDt,
            KeepDeptInLoop = true,
            NoofPeople = 0,
            ParentCompanyID = 0,
            PriorityId = 0,
            RequestTypeId = selectedRequestTypeID,
            TicketDepartmentId = selectedDeptID,
            TicketId = 0,
            TicketUTRNo = "undefined",
            Title = title!!,
            UserStatusId = 0,
            UserTicketRegNo = "undefined",
            VmId = 0,
            WorkingOrder = 0
        )

        /*"TicketId": 0,
        "Title": "title",
        "Description": "Description",
        "TicketDepartmentId": 3,
        "PriorityId": 0,
        "DriverId": 0,
        "VmId": 0,
        "RequestTypeId": 16,
        "UserTicketRegNo": "string",
        "KeepDeptInLoop": true,
        "TicketUTRNo": "string",
        "DaTestDate": "2024-04-01T12:30:27.826Z",
        "DaTestTime": "2024-04-01T12:30:27.826Z",
        "NoofPeople": 0,
        "EstCompletionDate": "2024-04-01T12:30:27.826Z",
        "UserStatusId": 0,
        "WorkingOrder": 0,
        "ParentCompanyID": 0,
        "BadgeReturnedStatusId": 0,
        "BadgeComment": "string",
        "AssignedToUserIDs": [
        0
        ]*/
        showDialog()
        viewmodel.SaveTicketData(
            pref.userID.toInt(),
            request
        )
    }

    private fun setInputListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s?.toString()
                when (editText.id) {
                    R.id.edt_title -> title = value
                    R.id.edt_des -> desc = value.toString()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observers() {
        viewmodel.liveDataSaveTicketResponse.observe(this) {
            hideDialog()
            if (it != null) {
                showUploadDialog()

            }
        }

        viewmodel.liveDataTicketDepartmentsResponse.observe(this) { depts ->
            hideDialog()
            if (depts != null) {

                val departmentIds = depts.map { tickets ->
                    tickets.DepartmentId
                }
                val departmentNames = depts.map { it.DepartmentName }
                setSpinners(mbinding.tvDepart, departmentNames, departmentIds)
            }
        }
        viewmodel.liveDataGetTicketRequestType.observe(this) { requests ->
            hideDialog()
            if (requests != null) {
                val requestIDs = requests.map { it.RequestId }
                val requestNames = requests.map { it.RequestName }
                setSpinners(mbinding.tvRequests, requestNames, requestIDs)
            } else {
                val spinnerNamesWithPlaceholder = listOf<String>()
                val spinnerIdsWithPlaceholder = listOf<Int>()
                setSpinners(
                    mbinding.tvRequests,
                    spinnerNamesWithPlaceholder,
                    spinnerIdsWithPlaceholder
                )
            }
        }
    }

    private fun setSpinners(spinner: Spinner, items: List<String>, ids: List<Int>) {

        val dummyItem = "Select Item"
        val itemsList = mutableListOf(dummyItem)
        itemsList.addAll(items)
        val adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //adapter.addAll(itemsList)

        spinner.adapter = adapter

        spinner.setSelection(0)


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                parent?.let { nonNullParent ->
                    if (position != 0) { // Skip the dummy item
                        val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                        selectedItem.let {
                            when (spinner) {
                                mbinding.tvDepart -> {
                                    selectedDeptID = ids[position - 1]
                                    showDialog()
                                    viewmodel.GetTicketRequestType(selectedDeptID)
                                }

                                mbinding.tvRequests -> {
                                    selectedRequestTypeID = ids[position - 1]
                                }
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    public fun hideDialog() {
        apiCount--
        if (apiCount <= 0) {
            loadingDialog.cancel()
            apiCount = 0
        }
    }

    public fun showDialog() {
        if (apiCount == 0) {
            loadingDialog.show()
        }
        apiCount++
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                    data?.data?.let { selectedUri ->

                        selectedFileUri = selectedUri
                    }
                } else if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_CANCELED) {

                    showToast("File selection canceled", this)
                }*/
    }

    fun showUploadDialog() {
        val uploadDialog = AlertDialog.Builder(this).create()
        val uploadDialogBinding = DialogUploadAlertBinding.inflate(LayoutInflater.from(this))
        uploadDialog.setView(uploadDialogBinding.root)
        uploadDialog.setCanceledOnTouchOutside(false)
        uploadDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        uploadDialog.show()
        uploadDialogBinding.upload.setOnClickListener {
            uploadDialog.dismiss()
            uploadDialog.cancel()
            uploadFileLauncher.launch("*/*")
        }

        uploadDialogBinding.cancel.setOnClickListener {
            uploadDialog.dismiss()
        }


    }
}
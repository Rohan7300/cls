package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityTicketsBinding
import com.clebs.celerity.models.requests.SaveTicketDataRequestBody
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getCurrentDateTime
import com.clebs.celerity.utils.showToast

class CreateTicketsActivity : AppCompatActivity() {
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

        mbinding.saveTickets.setOnClickListener {
            if (chkNull())
                showToast("Please complete form first!!", this)
            else
                saveTicket()
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
            BadgeComment ="undefined",
            BadgeReturnedStatusId =0,
            DaTestDate = currDt,
            DaTestTime =currDt,
            Description =desc!!,
            DriverId =pref.userID.toInt(),
            EstCompletionDate =currDt,
            KeepDeptInLoop =true,
            NoofPeople =0,
            ParentCompanyID =0,
            PriorityId =0,
            RequestTypeId =selectedRequestTypeID,
            TicketDepartmentId =selectedDeptID,
            TicketId =0,
            TicketUTRNo ="undefined",
            Title =title!!,
            UserStatusId =0,
            UserTicketRegNo ="undefined",
            VmId =0,
            WorkingOrder =0
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
        viewmodel.liveDataSaveTicketResponse.observe(this){
            if(it!=null){

            }
        }

        viewmodel.liveDataTicketDepartmentsResponse.observe(this) { depts ->
            hideDialog()
            if (depts != null) {
                val departmentIds = depts.map { it.DepartmentId }
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

}
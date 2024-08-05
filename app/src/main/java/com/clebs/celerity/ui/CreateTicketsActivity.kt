package com.clebs.celerity.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
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
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getCurrentDateTime
import com.clebs.celerity.utils.noInternetCheck
import com.clebs.celerity.utils.showToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody


class CreateTicketsActivity : AppCompatActivity() {
    private val PICK_FILE_REQUEST_CODE = 100
    lateinit var mbinding: ActivityTicketsBinding
    lateinit var viewmodel: MainViewModel
    lateinit var repo: MainRepo
    var selectedDeptID: Int = -1
    var selectedRequestTypeID: Int = -1
    var ticketID: String? = "0"
    var apiCount = 0
    var title: String? = null
    lateinit var pref: Prefs
    var desc: String? = null
    var uploadWithAttachement: Boolean = false
    var ticketRegNo: String = "undefined"
    lateinit var loadingDialog: LoadingDialog
    private var selectedFileUri: Uri? = null
    lateinit var filePart: MultipartBody.Part

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }.toTypedArray()


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_tickets)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        repo = MainRepo(apiService)
        pref = Prefs(this)
        viewmodel =
            ViewModelProvider(this, MyViewModelFactory(repo))[MainViewModel::class.java]
        loadingDialog = LoadingDialog(this)
        DependencyProvider.comingFromViewTickets = false
        observers()
        viewmodel.GetUserDepartmentList()
        showDialog()
        setInputListener(mbinding.edtTitle)
        setInputListener(mbinding.edtDes)
        noInternetCheck(this, mbinding.nointernetLL, this)

        mbinding.saveTickets.setOnClickListener {
            if (chkNull() == 1) {
                showUploadDialog()
            }
        }

        if (DependencyProvider.blockCreateTicket) {
            mbinding.cancel.visibility = View.GONE
            mbinding.imageViewBack.visibility = View.GONE
        } else {
            mbinding.cancel.visibility = View.VISIBLE
            mbinding.imageViewBack.visibility = View.VISIBLE
        }

        mbinding.imageViewBack.setOnClickListener {
            finish()
            //onBackPressed()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (DependencyProvider.blockCreateTicket)
                    showToast(
                        "Please submit the Deduction Agreement first!!",
                        this@CreateTicketsActivity
                    )
            }
        })
        mbinding.tvRegistration.doAfterTextChanged {
            if (!it.isNullOrEmpty()) {
                ticketRegNo = it.toString()
            }
        }

        mbinding.cancel.setOnClickListener {
            finish()
            //onBackPressed()
        }

        val spinnerNamesWithPlaceholder = listOf<String>()
        val spinnerIdsWithPlaceholder = listOf<Int>()

        setSpinnerNew(
            spinnerNamesWithPlaceholder,
            spinnerIdsWithPlaceholder,
            "Select Request Type",
            mbinding.spinnerRequestAT
        )
        setSpinnerNew(
            spinnerNamesWithPlaceholder,
            spinnerIdsWithPlaceholder,
            "Select Department",
            mbinding.selectDepartmentET
        )
    }

    private fun chkNull(): Int {
        if (selectedDeptID == -1 || selectedRequestTypeID == -1 || title.isNullOrBlank() || desc.isNullOrBlank() || mbinding.edtDes.text.isNullOrBlank()) {
            if (selectedDeptID == -1)
                showToast("Department not Selected!!", this)
            else if (selectedRequestTypeID == -1)
                showToast("Please add request type!!", this)
            else if (title.isNullOrBlank())
                showToast("Please add ticket title!!", this)
            else if (mbinding.edtDes.text.isNullOrBlank())
                showToast("Please add ticket description!!", this)
            else
                showToast("Please complete the form first!!", this)
            return -1
        }
        return 1
    }

    private fun saveTicket() {
        val currDt = getCurrentDateTime()
        val request = SaveTicketDataRequestBody(
            AssignedToUserIDs = listOf(),
            BadgeComment = "undefined",
            BadgeReturnedStatusId = 0,
            DaTestDate = currDt,
            DaTestTime = currDt,
            Description = desc!!,
            DriverId = pref.clebUserId.toInt(),
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
            UserTicketRegNo = ticketRegNo,
            VmId = 0,
            WorkingOrder = 0
        )
        showDialog()
        viewmodel.SaveTicketData(
            pref.clebUserId.toInt(),
            0,
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
                ticketID = it.TicketId
                if (uploadWithAttachement) {
                    viewmodel.UploadTicketAttachmentDoc(
                        pref.clebUserId.toInt(),
                        ticketId = ticketID!!.toInt(),
                        file = filePart
                    )
                } else {
                    finish()
                    //onBackPressed()
                }
            }
        }

        viewmodel.liveDataUploadTicketAttachmentDoc.observe(this) {
            hideDialog()
            if (it != null) {
                finish()
                //onBackPressed()
            } else {
                showToast("Failed to Upload Attachment!!", this)
                finish()
                //onBackPressed()
            }
        }

        viewmodel.liveDataTicketDepartmentsResponse.observe(this) { depts ->
            hideDialog()
            if (depts != null) {

                val departmentIds = depts.map { tickets ->
                    tickets.DepartmentId
                }
                val departmentNames = depts.map { it.DepartmentName }

                setSpinnerNew(
                    departmentNames, departmentIds,
                    "Select Request Type",
                    mbinding.selectDepartmentET
                )
            }
        }
        viewmodel.liveDataGetTicketRequestType.observe(this) { requests ->
            hideDialog()

            if (requests != null) {
                val requestIDs = requests.map { it.RequestId }
                val requestNames = requests.map { it.RequestName }
                setSpinnerNew(
                    requestNames,
                    requestIDs,
                    "Select Request Type",
                    mbinding.spinnerRequestAT
                )
            } else {
                val spinnerNamesWithPlaceholder = listOf<String>()
                val spinnerIdsWithPlaceholder = listOf<Int>()

                setSpinnerNew(
                    spinnerNamesWithPlaceholder,
                    spinnerIdsWithPlaceholder, "Select Request Type", mbinding.spinnerRequestAT
                )
            }
        }
    }


    fun hideDialog() {
        apiCount--
        if (apiCount <= 0) {
            loadingDialog.cancel()
            apiCount = 0
        }
    }

    fun showDialog() {
        if (apiCount == 0) {
            loadingDialog.show()
        }
        apiCount++
    }

    private fun getMimeType(uri: Uri): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                Log.d("XX", "$result $ticketID")
                data?.data?.let {
                    selectedFileUri = it
                    if (ticketID == null || selectedFileUri == null) {
                        showToast("Something went wrong!!", this)
                    } else {
                        val mimeType = getMimeType(selectedFileUri!!)?.toMediaTypeOrNull()
                        val tmpFile = createTempFile("temp", null, cacheDir).apply {
                            deleteOnExit()
                        }

                        val inputStream = contentResolver.openInputStream(selectedFileUri!!)
                        val outputStream = tmpFile.outputStream()

                        inputStream?.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }

                        val fileExtension = getMimeType(selectedFileUri!!)?.let { mimeType ->
                            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                        }

                        val requestBody = tmpFile.asRequestBody(mimeType)
                        filePart = MultipartBody.Part.createFormData(
                            "UploadTicketDoc",
                            selectedFileUri!!.lastPathSegment + "." + (fileExtension ?: "jpg"),
                            requestBody
                        )

                        showDialog()
                        uploadWithAttachement = true
                        saveTicket()
                    }

                }
            } else {
                //saveTicket()
                finish()
                showToast("Attachment not selected!!", this)
                //onBackPressed()
            }
        }

    private fun showUploadDialog() {
        val uploadDialog = AlertDialog.Builder(this).create()
        val uploadDialogBinding = DialogUploadAlertBinding.inflate(LayoutInflater.from(this))
        uploadDialog.setView(uploadDialogBinding.root)
        uploadDialog.setCanceledOnTouchOutside(false)
        uploadDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        uploadDialog.show()

        uploadDialogBinding.upload.setOnClickListener {
            uploadDialog.dismiss()
            uploadDialog.cancel()
            if (allPermissionsGranted()) {
                upload()
            } else {
                requestPermissions()
            }
        }


        uploadDialogBinding.cancel.setOnClickListener {
            uploadDialog.dismiss()
            uploadDialog.cancel()
            uploadWithAttachement = false
            saveTicket()
        }
    }

    fun upload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)

    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast("Permission denied", this)
            } else {
                upload()
            }
        }

    fun Int.dpToPx(): Int {
        val scale = resources.displayMetrics.density
        return (this * scale + 0.5f).toInt()
    }

    private fun setSpinnerNew(
        items: List<String>,
        ids: List<Int>, dummyItem: String,
        spinner: AutoCompleteTextView
    ) {
        val itemsList = mutableListOf<String>()
        itemsList.addAll(items)
        val adapter =
            ArrayAdapter(this, R.layout.dropdown_menu_popup_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.setAdapter(adapter)
        spinner.setOnItemClickListener { parent, view, position, id ->
            run {
                parent?.let { nonNullParent ->
                    val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                    selectedItem.let {
                        when (spinner) {
                            mbinding.selectDepartmentET -> {
                                selectedDeptID = ids[position]
                                showDialog()
                                selectedRequestTypeID = -1
                                mbinding.spinnerRequestAT.setText("")
                                viewmodel.GetTicketRequestType(selectedDeptID)
                            }

                            mbinding.spinnerRequestAT -> {
                                selectedRequestTypeID = ids[position]
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (DependencyProvider.blockCreateTicket) {
            showToast(
                "Please submit the Deduction Agreement first!!",
                this@CreateTicketsActivity
            )
        }
    }

}
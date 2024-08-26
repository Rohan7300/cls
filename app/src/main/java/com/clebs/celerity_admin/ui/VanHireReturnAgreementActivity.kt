package com.clebs.celerity_admin.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.AddFilesAdapter
import com.clebs.celerity_admin.databinding.ActivityVanHireReturnAgreementBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.ReturnVehicleToDepoRequest
import com.clebs.celerity_admin.models.VehicleInfoXXXX
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DependencyClass
import com.clebs.celerity_admin.utils.DependencyClass.addBlueMileage
import com.clebs.celerity_admin.utils.DependencyClass.crrMileage
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleFuelId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleLocId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleOilLevelListId
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.SignatureDialog
import com.clebs.celerity_admin.utils.SignatureDialogListener
import com.clebs.celerity_admin.utils.bitmapToBase64
import com.clebs.celerity_admin.utils.getFileUri
import com.clebs.celerity_admin.utils.getMimeType
import com.clebs.celerity_admin.utils.showDatePickerDialog
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.viewModels.MainViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class VanHireReturnAgreementActivity : AppCompatActivity() {
    companion object {
        var path = Path()
        var brush = Paint()
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

    lateinit var binding: ActivityVanHireReturnAgreementBinding
    private var selectedFileUri: MutableList<Uri>? = mutableListOf()
    lateinit var prefs: Prefs
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mainViewModel: MainViewModel
    private lateinit var vehicleInfoXXXX: VehicleInfoXXXX
    private var REQUEST_STORAGE_PERMISSION_CODE = 101
    val adapter = AddFilesAdapter(mutableListOf())
    private lateinit var filePart: MultipartBody.Part
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVanHireReturnAgreementBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(this)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        window.statusBarColor = resources.getColor(R.color.commentbg, null)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        setContentView(binding.root)
        clickListeners()
        prefs = Prefs.getInstance(this)
        vehicleInfoXXXX = prefs.getCurrentVehicleInfo()!!
        vehicleInfoXXXX.let {
            binding.atvDOB.setText(it.DOB)
            binding.atvLicenseNumber.setText(it.LisenceNumber)
            binding.aTvLicenseStartDate.setText(it.LisanceStartDate)
            binding.aTvLicenseEndDate.setText(it.LisanceEnddate)
        }

        observers()
        binding.tilConvictions.visibility = View.GONE
        binding.layoutUploadVehicleAccidentImages.visibility = View.GONE
        binding.vanType.text = DependencyClass.crrSelectedVehicleType
        binding.vehicleAccidentalImageLayout.fileListRV.adapter = adapter
        binding.vehicleAccidentalImageLayout.fileListRV.layoutManager = LinearLayoutManager(this)
    }

    private fun observers() {
        mainViewModel.LDDownloadVehicleHireAgreementPDF.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                downloadPDF("VehicleVanHireAgreement", it.byteStream())
            }
        }
        mainViewModel.LDDownloadVehicleSignOutHireAgreementPDF.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                downloadPDF("VehicleSignOutHireAgreement", it.byteStream())
            }
        }
    }

    private fun clickListeners() {
        binding.atvDOB.setOnClickListener {
            showDatePickerDialog(this, binding.atvDOB)
        }
        binding.atvDOB.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.atvDOB.performClick()
            }
        }
        binding.aTvLicenseStartDate.setOnClickListener {
            showDatePickerDialog(this, binding.aTvLicenseStartDate)
        }
        binding.aTvLicenseStartDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.aTvLicenseStartDate.performClick()
        }
        binding.aTvLicenseEndDate.setOnClickListener {
            showDatePickerDialog(this, binding.aTvLicenseEndDate)
        }
        binding.aTvLicenseEndDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.aTvLicenseEndDate.performClick()
        }
        binding.checkboxConvictions.setOnClickListener {
            if (binding.checkboxConvictions.isChecked) {
                binding.tilConvictions.visibility = View.VISIBLE
            } else {
                binding.tilConvictions.visibility = View.GONE
            }
        }
        binding.checkboxUploadVehicleDefectImages.setOnClickListener {
            if (binding.checkboxUploadVehicleDefectImages.isChecked)
                binding.layoutUploadVehicleAccidentImages.visibility = View.VISIBLE
            else
                binding.layoutUploadVehicleAccidentImages.visibility = View.GONE
        }
        binding.checkBoxProceed.setOnClickListener {
            if (binding.checkBoxProceed.isChecked)
                showSignatureDialog()
        }
        binding.vehicleAccidentalImageLayout.browseBtn.setOnClickListener {
            browseFiles()
        }
        binding.vanHireAgreementBtn.setOnClickListener {
            loadingDialog.show()
            mainViewModel.DownloadVehicleHireAgreementPDF()
        }
        binding.vehicleSignOutBtn.setOnClickListener {
            loadingDialog.show()
            mainViewModel.DownloadVehicleSignOutHireAgreementPDF()
        }
    }

    private fun browseFiles() {
        if (allPermissionsGranted()) {
            upload()
        } else {
            requestPermissions()
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    selectedFileUri!!.add(it)
                    adapter.data = selectedFileUri as MutableList<Uri>
                    adapter.notifyItemInserted(selectedFileUri!!.size)
                    val mimeType = getMimeType(it)?.toMediaTypeOrNull()
                    val tmpFile = createTempFile("temp", null, cacheDir).apply {
                        deleteOnExit()
                    }

                    val inputStream = contentResolver.openInputStream(it)
                    val outputStream = tmpFile.outputStream()

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }

                    val fileExtension = getMimeType(it)?.let { mimeType ->
                        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                    }

                    val requestBody = tmpFile.asRequestBody(mimeType)
                    filePart = MultipartBody.Part.createFormData(
                        "UploadTicketDoc",
                        it.lastPathSegment + "." + (fileExtension ?: "jpg"),
                        requestBody
                    )

                    /*                        showDialog()
                                            uploadWithAttachement = true
                                            saveTicket()*/
                }
            } else {
                finish()
                showToast("Attachment not selected!!", this)
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
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast("Permission denied", this)
            } else {
                upload()
            }
        }

    private fun showSignatureDialog() {
        val dialog = SignatureDialog()
        dialog.setSignatureListener(object : SignatureDialogListener {
            override fun onSignatureSaved(bitmap: Bitmap) {
                if (checkAll()) {
                    val bse64 = "data:image/png;base64," + bitmapToBase64(bitmap)
                  /*  mainViewModel.ReturnVehicleToDepo(
                        ReturnVehicleToDepoRequest(
                            Signature1 = bse64,
                            DriverId = vehicleInfoXXXX.DaId,
                            AccidentsChk = binding.checkBoxTickToConfirmAccidents.isChecked,
                            AddBlueMileage = addBlueMileage.toString(),
                            ChangeVehCount = 1,
                            ConOrAcciComment = binding.atvConAccident.text.toString(),
                            ConvictionsChk = binding.checkboxConvictions.isChecked,
                            DOB = vehicleInfoXXXX.DOB,
                            IsDAVehReturned =,
                            IsVehAllocDaLeft =,
                            IsVehAllocInOurGarage =,
                            IsVehAllocIsDamaged =,
                            IsVehReturnToDepo =true,
                            IsVehReturned =,
                            IsVehReturntoSupplier =,
                            IsVehRoadWorthy =,
                            IsVehicleMultiAlloc =,
                            LicenseEndDate =binding.aTvLicenseEndDate.text.toString(),
                            LicenseNo =binding.atvLicenseNumber.text.toString(),
                            LicenseStartDate =binding.aTvLicenseStartDate.text.toString(),
                            NewVmId =0,
                            OldVmId =selectedVehicleId,
                            ParentCompanyId =,
                            RequestTypeIds =,
                            SecondUserAllocPosition =,
                            ThirdUserAllocPosition =,
                            VehAllocComments =,
                            VehAllocGarageStartDate =,
                            VehAllocStatusId =,
                            VehCurrentFuelLevelId =selectedVehicleFuelId,
                            VehCurrentMileage =crrMileage.toString(),
                            VehCurrentOILLevelId =selectedVehicleOilLevelListId,
                            VehSelectedLocationId =selectedVehicleLocId,
                            VehType = ,
                            supervisorId = prefs.clebUserId.toInt()
                        )
                    )*/
                }
            }
        })
        dialog.show(supportFragmentManager, "sign")
    }

    private fun checkAll(): Boolean {
        return true
    }

    private fun downloadPDF(fileName: String, fileContent: InputStream) {
        if (checkForStoragePermission()) {
            try {
                val currentDate = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                    Date()
                )
                val uniqueId = UUID.randomUUID().toString()
                val uniqueFileName = "$fileName-$currentDate-$uniqueId.pdf"
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    uniqueFileName
                )
                FileOutputStream(file).use { outputStream ->
                    fileContent.use { input ->
                        input.copyTo(outputStream)
                    }
                }
                val uri = getFileUri(file, this)

                /*showNotification(
                    "PDF Loaded",
                    "Your PDF is ready to view.",
                    uri
                )*/
                showToast("Your PDF is ready to view.", this)

                openPDF(file)
            } catch (e: Exception) {
                e.printStackTrace()
                showToast("Failed to download PDF", this)
            }
        } else {
            showToast("Storage Permission Required", this)
        }
    }

    private fun openPDF(file: File) {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "${this.packageName}.fileprovider", file)
        } else {
            Uri.fromFile(file)
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        try {
            this.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            showToast("No PDF viewer found", this)
        }
    }

    private fun checkForStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_STORAGE_PERMISSION_CODE
                )
            } else {
                return true
            }
        } else {
            return true
        }
        return false
    }

}
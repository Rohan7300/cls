package com.clebs.celerity.ui

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.ViewModel.OSyncVMProvider
import com.clebs.celerity.ViewModel.OSyncViewModel
import com.clebs.celerity.database.OfflineSyncEntity
import com.clebs.celerity.databinding.ActivityAddInspectionBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.BackgroundUploadDialog
import com.clebs.celerity.utils.BackgroundUploadDialogListener
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.ClsCapture
import com.clebs.celerity.utils.ClsCaptureTwo
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.currentUri
import com.clebs.celerity.utils.DependencyProvider.insLevel
import com.clebs.celerity.utils.DependencyProvider.isComingBackFromCLSCapture
import com.clebs.celerity.utils.DependencyProvider.offlineSyncRepo
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.checkIfInspectionFailed
import com.clebs.celerity.utils.getCurrentDateTime
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.startUploadWithWorkManager
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class AddInspection : AppCompatActivity(), BackgroundUploadDialogListener {
    lateinit var binding: ActivityAddInspectionBinding
    lateinit var prefs: Prefs
    private lateinit var backgroundUploadDialog: BackgroundUploadDialog
    lateinit var loadingDialog: LoadingDialog
    var b64ImageList = mutableListOf<String>()
    var i = 0
    lateinit var fragmentManager: FragmentManager
    var isadBluerequire: Boolean = false
    var DaVehicleAddBlueImage = String()
    private var allImagesUploaded: Boolean = false
    lateinit var viewModel: MainViewModel
    lateinit var oSyncViewModel: OSyncViewModel
    lateinit var osData: OfflineSyncEntity
    private var imageCapture: ImageCapture? = null

    companion object {

        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                }
            }.toTypedArray()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_inspection)
        setContentView(binding.root)
        prefs = Prefs.getInstance(this)
        loadingDialog = LoadingDialog(this)
        backgroundUploadDialog = BackgroundUploadDialog()
        backgroundUploadDialog.setListener(this)

        initPreviewView()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val todayDate = dateFormat.format(Date())
        var currentDateTime = getCurrentDateTime()

        val osRepo = offlineSyncRepo(this)
        oSyncViewModel = ViewModelProvider(
            this,
            OSyncVMProvider(osRepo, prefs.clebUserId.toInt(), todayDate)
        )[OSyncViewModel::class.java]


        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        osData = DependencyProvider.osData

        oSyncViewModel.osData.observe(this) {
            osData = it
            DependencyProvider.osData = it
            i = 0
            b64ImageList.clear()
            Log.d("OSData ", "$osData")
            if (!osData.isIni) {
                osData.clebID = prefs.clebUserId.toInt()
                osData.dawDate = todayDate
                osData.vehicleID = prefs.scannedVmRegNo
                osData.isIni = true
            }
            if (checkIfInspectionFailed(osData)) {
                Log.d("OSDataFailed1 ", "$osData")
                i = 0
            } else {
                if (osData.dashboardImage != null)
                    b64ImageList.add(osData.dashboardImage!!)
                if (osData.frontImage != null)
                    b64ImageList.add(osData.frontImage!!)
                if (osData.rearSideImage != null)
                    b64ImageList.add(osData.rearSideImage!!)
                if (osData.nearSideImage != null)
                    b64ImageList.add(osData.nearSideImage!!)
                if (osData.offSideImage != null)
                    b64ImageList.add(osData.offSideImage!!)
                if (osData.addblueImage != null)
                    b64ImageList.add(osData.addblueImage!!)
                if (osData.oillevelImage != null)
                    b64ImageList.add(osData.oillevelImage!!)
                if (osData.dashboardImage != null &&
                    osData.frontImage != null &&
                    osData.rearSideImage != null &&
                    osData.nearSideImage != null &&
                    osData.offSideImage != null &&
                    osData.addblueImage != null &&
                    osData.oillevelImage != null
                ) {
                    uploadStatus(7)
                    generateInspectionID()
                    showToast("Inspection Completed", this)
                    onSaveClick()

                }

                i = b64ImageList.size


                Log.d("OSData I= ", "$i")
            }
            uploadStatus(i)
        }

        observers()

        viewModel.GetVehicleImageUploadInfo(
            prefs.clebUserId.toInt(),
            prefs.vmId.toInt(),
            currentDateTime
        )
        loadingDialog.show()
        clientUniqueID()


        binding.ivUploadImage.setOnClickListener {
            if (allPermissionsGranted())
                openClsCapture()
            //uploadImage()
            else {
                requestpermissions()
            }
        }
        binding.tvUploadMainTV.setOnClickListener {
            if (allPermissionsGranted())
                openClsCapture()
            //uploadImage()
            else
                requestpermissions()
        }
        binding.fullClick.setOnClickListener {
            if (allPermissionsGranted())
                openClsCapture()
            //uploadImage()
            else {
                requestpermissions()
            }
        }
        binding.newUploadBtn.setOnClickListener {
            if (allPermissionsGranted())
                openClsCapture()
            //uploadImage()
            else {
                requestpermissions()
            }
        }
        binding.imageViewBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("destinationFragment", "CompleteTask")
            intent.putExtra("actionToperform", "undef")
            intent.putExtra("actionID", "0")
            intent.putExtra("tokenUrl", "undef")
            intent.putExtra("notificationId", "0")
            startActivity(intent)
        }
        binding.tvNext.setOnClickListener {
            generateInspectionID()
            // backgroundUploadDialog.show(this.supportFragmentManager, BackgroundUploadDialog.TAG)\

            onSaveClick()

        }
    }

    private fun initPreviewView() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.inspectionPreviewView.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            PreviewView.ImplementationMode.COMPATIBLE
            val cameraSelector =
                CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.d(ContentValues.TAG, "Use case binding failed ${e.message}")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun observers() {
        viewModel.vehicleImageUploadInfoLiveData.observe(this) {
            loadingDialog.dismiss()
            i = 0
            if (it != null) {
                Log.d("OSData2 ", "$osData")
                if (it.DaVehImgDashBoardFileName != null &&
                    it.DaVehImgFrontFileName != null &&
                    it.DaVehImgRearFileName != null &&
                    it.DaVehImgNearSideFileName != null &&
                    it.DaVehImgOffSideFileName != null &&
                    it.DaVehicleAddBlueImage != null &&
                    it.DaVehImgOilLevelFileName != null &&
                    !checkIfInspectionFailed(osData)
                ) {
                    osData.isaddBlueImageRequired = false
                    osData.isDashboardImageRequired = false
                    osData.isRearImageRequired = false
                    osData.isOffsideImageRequired = false
                    osData.isnearImageRequired = false
                    osData.isfaceMaskImageRequired = false
                    osData.isoilLevelImageRequired = false
                    osData.isFrontImageRequired = false
                    generateInspectionID()
                    allImagesUploaded = true
                    showToast("Inspection Completed", this)
                    onSaveClick()
                } else {
                    if (it.IsAdBlueRequired == true)
                        osData.isaddBlueImageRequired = it.DaVehicleAddBlueImage == null
                    else
                        osData.isaddBlueImageRequired = false

                    osData.isDashboardImageRequired = it.DaVehImgDashBoardFileName == null

                    osData.isRearImageRequired = it.DaVehImgRearFileName == null

                    osData.isOffsideImageRequired = it.DaVehImgOffSideFileName == null

                    osData.isnearImageRequired = it.DaVehImgNearSideFileName == null

                    osData.isFrontImageRequired = it.DaVehImgFrontFileName == null

                    osData.isoilLevelImageRequired = it.DaVehImgOilLevelFileName == null

                    oSyncViewModel.insertData(osData)
                    uploadStatus(1)
                }
            }
        }

        viewModel.livedataSavevehicleinspectioninfo.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                if (it.Message == "200") {
                    prefs.saveBoolean("Inspection", true)
                    prefs.updateInspectionStatus(true)
                    showToast("Inspection id Generated", this)
                }
            }
        }

        viewModel.liveDatauploadVehicleImages.observe(this) {
            loadingDialog.show()
            if (it != null) {
                viewModel.GetVehicleImageUploadInfo(
                    prefs.clebUserId.toInt(),
                    prefs.vmId,
                    getCurrentDateTime()
                )
            }
        }
    }

    private fun uploadStatus(i: Int) {
        val uploadStatus = "($i/7)"
        binding.uploadStatus.text = uploadStatus
        with(binding) {
            listOf(
                dashboardStatusIV,
                frontStatusIV,
                nearSideStatusIV,
                rearSideStatusIV,
                offsideStatusIV,
                addBlueIV,
                oilLevelIV
            ).forEach {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@AddInspection,
                        R.drawable.fileins
                    )
                )
            }
        }
        setCheckIndicator()
        setUploadLabel()
    }


    private fun sendImage(imageUri: Uri, requestCode: Int) {
        var x = b64ImageList.size
        if (osData.dashboardImage == null && osData.isDashboardImageRequired) {
            osData.dashboardImage = imageUri.toString()
            oSyncViewModel.insertData(osData)
        } else if (osData.frontImage == null && osData.isFrontImageRequired) {
            osData.frontImage = imageUri.toString()
            oSyncViewModel.insertData(osData)
        } else if (osData.nearSideImage == null && osData.isnearImageRequired) {
            osData.nearSideImage = imageUri.toString()
            oSyncViewModel.insertData(osData)
        } else if (osData.rearSideImage == null && osData.isRearImageRequired) {
            osData.rearSideImage = imageUri.toString()
            oSyncViewModel.insertData(osData)
        } else if (osData.offSideImage == null && osData.isOffsideImageRequired) {
            osData.offSideImage = imageUri.toString()
            oSyncViewModel.insertData(osData)
        } else if (osData.addblueImage == null && osData.isaddBlueImageRequired) {
            osData.addblueImage = imageUri.toString()
            oSyncViewModel.insertData(osData)
        } else if (osData.oillevelImage == null && osData.isoilLevelImageRequired) {
            osData.oillevelImage = imageUri.toString()
            oSyncViewModel.insertData(osData)
        } else {



        }

        uploadStatus(x)
        /*        if (x == 7) {
                    binding.tvNext.isEnabled = true
                    oSyncViewModel.insertData(osData)
                    binding.tvNext.setTextColor(ContextCompat.getColor(this, R.color.white))
                }*/
    }

    private fun clientUniqueID(): String {
        val x = Prefs.getInstance(App.instance).clebUserId.toString()
        val y = Prefs.getInstance(App.instance).scannedVmRegNo

        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))
        var regexPattern = Regex("${x.take(3)}${y.take(3)}${formattedDate}")
//        prefs.inspectionID = regexPattern.toString()
        return regexPattern.toString()
    }

    override fun onSaveClick() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        osData.isInspectionDoneToday = true
        oSyncViewModel.insertData(osData)
        intent.putExtra("destinationFragment", "CompleteTask")
        intent.putExtra("actionToperform", "undef")
        intent.putExtra("actionID", "0")
        intent.putExtra("tokenUrl", "undef")
        intent.putExtra("notificationId", "0")
        startActivity(intent)
    }

    private fun generateInspectionID() {
        prefs.updateInspectionStatus(true)

        val currentDate =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).format(
                Date()
            )

        val currentLocation = Prefs.getInstance(App.instance).currLocationId
        val workingLocation = Prefs.getInstance(App.instance).workLocationId
        val locationID: Int = if (workingLocation != 0) {
            workingLocation
        } else {
            currentLocation
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestpermissions() {

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
                openClsCapture()
                //uploadImage()
            }
        }

    override fun onResume() {
        super.onResume()
        initPreviewView()
        if (isComingBackFromCLSCapture) {
            if (currentUri != null)
            // printBitmapSize(currentUri!!)
                sendImage(currentUri!!, 0)
            isComingBackFromCLSCapture = false
        }
    }

    private fun openClsCapture() {
        Log.e("sdkdfjfdjfdjhfd", "openClsCapture: " + b64ImageList.size.toString())


        if (osData.dashboardImage == null && osData.isDashboardImageRequired) {
            val intent = Intent(this, ClsCaptureTwo::class.java)
            insLevel = 0
            startActivity(intent)
        } else if (osData.frontImage == null && osData.isFrontImageRequired) {
            currentUri = null
            val intent = Intent(this, ClsCapture::class.java)
            insLevel = 1
            intent.putExtra("source_activity", "")
            startActivity(intent)
        } else if (osData.nearSideImage == null && osData.isnearImageRequired) {
            currentUri = null
            val intent = Intent(this, ClsCapture::class.java)
            insLevel = 2
            intent.putExtra("source_activity", "")
            startActivity(intent)
        } else if (osData.rearSideImage == null && osData.isRearImageRequired) {
            currentUri = null
            val intent = Intent(this, ClsCapture::class.java)
            insLevel = 3
            intent.putExtra("source_activity", "")
            startActivity(intent)
        } else if (osData.offSideImage == null && osData.isOffsideImageRequired) {
            currentUri = null
            val intent = Intent(this, ClsCapture::class.java)
            insLevel = 4
            intent.putExtra("source_activity", "")
            startActivity(intent)
        } else if (osData.addblueImage == null && osData.isaddBlueImageRequired) {
            val intent = Intent(this, ClsCaptureTwo::class.java)
            insLevel = 5
            startActivity(intent)
        } else if (osData.oillevelImage == null && osData.isoilLevelImageRequired) {
            val intent = Intent(this, ClsCaptureTwo::class.java)
            insLevel = 6
            startActivity(intent)
        } else {
            currentUri = null
            val intent = Intent(this, ClsCapture::class.java)
            insLevel = 6
            intent.putExtra("source_activity", "")
            startActivity(intent)
        }

        /*
                b64ImageList.size
                if (b64ImageList.size == 0) {
                    val intent = Intent(this, ClsCaptureTwo::class.java)
                    insLevel = b64ImageList.size
                    startActivity(intent)

                } else if (b64ImageList.size == 5 && isadBluerequire.equals(true) && DaVehicleAddBlueImage.isNotEmpty()) {
                    val intent = Intent(this, ClsCaptureTwo::class.java)
                    insLevel = b64ImageList.size
                    startActivity(intent)
                } else if (b64ImageList.size == 6) {
                    val intent = Intent(this, ClsCaptureTwo::class.java)
                    insLevel = b64ImageList.size
                    startActivity(intent)
                } else {
                    currentUri = null
                    val intent = Intent(this, ClsCapture::class.java)
                    insLevel = b64ImageList.size
                    intent.putExtra("source_activity", "")
                    startActivity(intent)
                }*/
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun setUploadLabel() {
        if (osData.dashboardImage == null && osData.isDashboardImageRequired) {
            binding.tvUploadType.text = "Dashboard Image"
            binding.uploadBtnText.text = "Upload Dashboard Image"
        } else if (osData.frontImage == null && osData.isFrontImageRequired) {
            binding.tvUploadType.text = "Front Image"
            binding.uploadBtnText.text = "Upload Front Image"
        } else if (osData.nearSideImage == null && osData.isnearImageRequired) {
            binding.tvUploadType.text = "Near Side Image"
            binding.uploadBtnText.text = "Upload Near Side Image"
        } else if (osData.rearSideImage == null && osData.isRearImageRequired) {
            binding.tvUploadType.text = "Rear Side Image"
            binding.uploadBtnText.text = "Upload Rear Side Image"
        } else if (osData.offSideImage == null && osData.isOffsideImageRequired) {
            binding.tvUploadType.text = "Offside Image"
            binding.uploadBtnText.text = "Upload Offside Image"
        } else if (osData.addblueImage == null && osData.isaddBlueImageRequired) {
            binding.tvUploadType.text = "Add Blue Level Image"
            binding.uploadBtnText.text = "Upload Add Blue Level Image"
        } else if (osData.oillevelImage == null && osData.isoilLevelImageRequired) {
            binding.tvUploadType.text = "Oil Level Image"
            binding.uploadBtnText.text = "Upload Oil Level Image"
        } else {
            if (allImagesUploaded) {
                binding.ivUploadImage.visibility = View.VISIBLE

                binding.ivUploadImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        this, R.drawable.ic_yes2
                    )
                )
                binding.tvUploadMainTV.text = "Inspection Completed"
                binding.uploadBtnText.text = "Inspection Completed"
                binding.uploadBtnText.setTextColor(ContextCompat.getColor(this, R.color.orange))
                binding.tvUploadType.text = "You can exit and continue on remaining steps."
            } else {
                binding.ivUploadImage.visibility = View.GONE
                binding.tvUploadMainTV.visibility = View.GONE
                binding.tvUploadType.text =
                    "You can save and exit while images are being uploaded."
                osData.isdashboardUploadedFailed = false
                osData.isfrontImageFailed = false
                osData.isnearSideFailed = false
                osData.isrearSideFailed = false
                osData.isoffSideFailed = false
                osData.isaddblueImageFailed = false
                osData.isoillevelImageFailed = false
                startUploadWithWorkManager(0, prefs, this)
            }
            binding.tvUploadMainTV.isEnabled = false
            binding.ivUploadImage.isEnabled = false
            binding.uploadBtnText.text = "Inspection Completed"
            binding.uploadBtnText.setTextColor(ContextCompat.getColor(this, R.color.orange))
            binding.newUploadBtn.background.setTint(
                ContextCompat.getColor(
                    this,
                    R.color.very_light_orange
                )
            )

            val drawable = ContextCompat.getDrawable(this, R.drawable.check_new)

            binding.uploadBtnIV.setImageDrawable(drawable)

            binding.newUploadBtn.isEnabled = false
            binding.fullClick.isEnabled = false
        }
    }

    private fun setCheckIndicator() {
        var uploadStatus = 0

        val imageViews = listOf(
            Pair(
                osData.dashboardImage to osData.isDashboardImageRequired,
                binding.dashboardStatusIV
            ),
            Pair(osData.frontImage to osData.isFrontImageRequired, binding.frontStatusIV),
            Pair(osData.nearSideImage to osData.isnearImageRequired, binding.nearSideStatusIV),
            Pair(osData.rearSideImage to osData.isRearImageRequired, binding.rearSideStatusIV),
            Pair(osData.offSideImage to osData.isOffsideImageRequired, binding.offsideStatusIV),
            Pair(osData.addblueImage to osData.isaddBlueImageRequired, binding.addBlueIV),
            Pair(osData.oillevelImage to osData.isoilLevelImageRequired, binding.oilLevelIV)
        )

        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_yes2)
        imageViews.forEach { (image, imageView) ->
            if (image.first != null || !image.second) {
                uploadStatus += 1
                binding.uploadStatus.text = "$uploadStatus/7"
                if (uploadStatus == 7) {
                    binding.tvNext.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.tvNext.isEnabled = true
                }

                imageView.setImageDrawable(drawable)
            }
        }
    }

}
package com.clebs.celerity.ui

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.ViewModel.OSyncVMProvider
import com.clebs.celerity.ViewModel.OSyncViewModel
import com.clebs.celerity.database.OfflineSyncEntity
import com.clebs.celerity.databinding.ActivityAddInspection2Binding
import com.clebs.celerity.databinding.ActivityAddInspectionBinding
import com.clebs.celerity.databinding.ActivityHomeBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.BackgroundUploadDialog
import com.clebs.celerity.utils.BackgroundUploadDialogListener
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.models.requests.SaveVehicleInspectionInfo
import com.clebs.celerity.utils.ClsCapture
import com.clebs.celerity.utils.ClsCaptureTwo
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.currentUri
import com.clebs.celerity.utils.DependencyProvider.insLevel
import com.clebs.celerity.utils.DependencyProvider.isComingBackFromCLSCapture
import com.clebs.celerity.utils.DependencyProvider.offlineSyncRepo
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.SaveVehicleInspection
import com.clebs.celerity.utils.checkIfInspectionFailed
import com.clebs.celerity.utils.checkIfInspectionFailed2
import com.clebs.celerity.utils.getCurrentDateTime
import com.clebs.celerity.utils.showErrorDialog
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.startUploadWithWorkManager
import com.clebs.celerity.utils.toast
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport.Session.User
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.CQSDKInitializer.Companion.userFlowParams
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone


class AddInspectionActivity2 : AppCompatActivity(), BackgroundUploadDialogListener {
    lateinit var binding: ActivityAddInspection2Binding
    lateinit var prefs: Prefs
    private lateinit var backgroundUploadDialog: BackgroundUploadDialog
    lateinit var loadingDialog: LoadingDialog
    private var b64ImageList = mutableListOf<String>()
    var i = 0
    lateinit var fragmentManager: FragmentManager
    private var startonetime: Boolean? = true
    private var allImagesUploaded: Boolean = false
    lateinit var viewModel: MainViewModel
    lateinit var oSyncViewModel: OSyncViewModel
    lateinit var osData: OfflineSyncEntity
    private var imageCapture: ImageCapture? = null
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var regexPattern: Regex
    private lateinit var inspectionID: String
    private var inspectionTAG = "INSPECTION"

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_inspection2)
        setContentView(binding.root)
        prefs = Prefs.getInstance(this)
        loadingDialog = LoadingDialog(this)
        backgroundUploadDialog = BackgroundUploadDialog()
        backgroundUploadDialog.setListener(this)
        cqSDKInitializer = CQSDKInitializer(this)
        cqSDKInitializer.triggerOfflineSync()

        initPreviewView()

        startonetime = prefs.Isfirst!!

        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val todayDate = dateFormat.format(Date())
        val currentDateTime = getCurrentDateTime()

        val osRepo = offlineSyncRepo(this)
        oSyncViewModel = ViewModelProvider(
            this,
            OSyncVMProvider(osRepo, prefs.clebUserId.toInt(), todayDate)
        )[OSyncViewModel::class.java]


        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]

        clientUniqueID()
        BubbleShowCaseBuilder(this)//Activity instance
            .title("Vehicle Inspection") //Any title for the bubble view
            .description("Please make sure you have stable internet connection when you trying to do vehicle inspection for the very first time") //More detailed description
            .arrowPosition(BubbleShowCase.ArrowPosition.TOP)

            //You can force the position of the arrow to change the location of the bubble.
            .backgroundColor((this.getColor(R.color.very_light_orange)))
            //Bubble background color
            .textColor(this.getColor(R.color.black)) //Bubble Text color
            .titleTextSize(16) //Title text size in SP (default value 16sp)
            .descriptionTextSize(12) //Subtitle text size in SP (default value 14sp)
            .image(ContextCompat.getDrawable(this, R.drawable.scan2)!!) //Bubble main image
            .closeActionImage(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.cross
                )!!
            ) //Custom close action image

            .listener(
                (object : BubbleShowCaseListener { //Listener for user actions
                    override fun onTargetClick(bubbleShowCase: BubbleShowCase) {
                        //Called when the user clicks the target
                        bubbleShowCase.dismiss()
                    }

                    override fun onCloseActionImageClick(bubbleShowCase: BubbleShowCase) {
                        //Called when the user clicks the close button
                        bubbleShowCase.dismiss()
                    }

                    override fun onBubbleClick(bubbleShowCase: BubbleShowCase) {
                        //Called when the user clicks on the bubble
                        bubbleShowCase.dismiss()
                    }

                    override fun onBackgroundDimClick(bubbleShowCase: BubbleShowCase) {
                        bubbleShowCase.dismiss()
                        //Called when the user clicks on the background dim
                    }
                })
            ).targetView(binding.llscan).showOnce("20")
            .highlightMode(BubbleShowCase.HighlightMode.VIEW_SURFACE)
            .backgroundColor(resources.getColor(R.color.very_light_orange)) //View to point out
            .show().finishSequence()

        oSyncViewModel.osData.observe(this) {
            osData = it
            i = 0
            b64ImageList.clear()
            try {
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


                    i = b64ImageList.size


                    Log.d("OSData I= ", "$i")
                }
            } catch (_: Exception) {

            }

            //  uploadStatus()
        }

        observers()

        viewModel.GetVehicleImageUploadInfo(
            prefs.clebUserId.toInt(),
            prefs.vmId,
            currentDateTime
        )
        loadingDialog.show()

        uploadStatus()

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
                if (it.DaVehImgOilLevelFileName != null
                    && !checkIfInspectionFailed2(osData)
                ) {
                    prefs.oilLevelRequired = false
                    osData.isaddBlueImageRequired = false
                    osData.isDashboardImageRequired = false
                    osData.isRearImageRequired = false
                    osData.isOffsideImageRequired = false
                    osData.isnearImageRequired = false
                    osData.isfaceMaskImageRequired = false
                    osData.isoilLevelImageRequired = false
                    osData.isFrontImageRequired = false
                    if (it.IsAdBlueRequired == true) {
                        if (it.DaVehicleAddBlueImage != null) {
                            generateInspectionID()
                            allImagesUploaded = true
                            if (prefs.isInspectionDoneToday()) {
                                onSaveClick()
                                showToast("Inspection Completed", this)
                            }

                        }
                        prefs.addBlueRequired = it.DaVehicleAddBlueImage == null
                    } else {
                        generateInspectionID()
                        allImagesUploaded = true
                        if (prefs.isInspectionDoneToday()) {
                            onSaveClick()

                        }
                    }
                } else {

                    prefs.addBlueRequired =
                        it.IsAdBlueRequired == true && it.DaVehicleAddBlueImage == null && prefs.addBlueUri == null

                    prefs.oilLevelRequired =
                        it.DaVehImgOilLevelFileName == null && prefs.oilLevelUri == null
                    uploadStatus()
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
                    prefs.baseVmID.toInt(),
                    getCurrentDateTime()
                )
            }
        }
    }
    private fun uploadStatus() {
        val uploadStatus = "($i/3)"
        binding.uploadStatus.text = uploadStatus
        with(binding) {
            listOf(
                addBlueIV,
                oilLevelIV
            ).forEach {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@AddInspectionActivity2,
                        R.drawable.fileins
                    )
                )
            }
        }
        setCheckIndicator()
        setUploadLabel()

    }


    private fun sendImage(imageUri: Uri) {
        var x = b64ImageList.size
        if (prefs.addBlueUri == null && prefs.addBlueRequired) {
            prefs.addBlueUri = imageUri.toString()
            prefs.addBlueRequired = false
            osData.addblueImage = imageUri.toString()
            oSyncViewModel.insertData(osData)
        } else if (prefs.oilLevelUri == null && prefs.oilLevelRequired) {
            prefs.oilLevelUri = imageUri.toString()
            prefs.oilLevelRequired = false
            osData.oillevelImage = imageUri.toString()
            oSyncViewModel.insertData(osData)
        } else {
            binding.tvNext.isEnabled = true
            binding.tvNext.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        uploadStatus()
    }

    private fun clientUniqueID(): String {
        val x = Prefs.getInstance(App.instance).clebUserId.toString()
        val y = Prefs.getInstance(App.instance).scannedVmRegNo

        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))
        var regexPattern = Regex("${x.take(3)}${y.take(3)}${formattedDate}")
        prefs.inspectionID = regexPattern.toString()
        inspectionID = regexPattern.toString()
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
            }
        }

    override fun onResume() {
        super.onResume()
        initPreviewView()
        if (isComingBackFromCLSCapture) {
            if (currentUri != null)
                sendImage(currentUri!!)
            isComingBackFromCLSCapture = false
        }

        startonetime = prefs.Isfirst
        val message =
            intent.getStringExtra(PublicConstants.quoteCreationFlowStatusMsgKeyInIntent)
                ?: "Could not identify status message"
        val tempCode =
            intent.getIntExtra(PublicConstants.quoteCreationFlowStatusCodeKeyInIntent, -1)

        if (tempCode == 200) {
            Log.d("hdhsdshdsdjshhsds", "200 $message")
            prefs.saveBoolean("Inspection", true)
            prefs.Isfirst = false
            generateInspectionID()
            prefs.updateInspectionStatus(true)
            SaveVehicleInspection(viewModel)

            uploadStatus()
            showToast("Vehicle Inspection is successfully completed ", this)
        } else {

            //showToast("Vehicle Inspection Failed!! ", this)
            Log.d("hdhsdshdsdjshhsds", "else $tempCode $message")
        }
    }

    private fun openClsCapture() {
        if (!prefs.isInspectionDoneToday()) {
            loadingDialog.show()


            startInspection()
        } else if (prefs.addBlueUri == null && prefs.addBlueRequired) {
            val intent = Intent(this, ClsCaptureTwo::class.java)
            insLevel = 5
            startActivity(intent)
        } else if (prefs.oilLevelUri == null && prefs.oilLevelRequired) {
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
    }


    private fun startInspection() {
        loadingDialog.show()

        if (cqSDKInitializer.isCQSDKInitialized()) {
            Log.e(inspectionTAG, "onCreateView: CQ SDK Initialized")

            try {
                startInspectionMain()

//                if (!startonetime!!)
//                    showToast("Offline Mode", this)
//                else
//                    showToast("Online Mode", this)

//                cqSDKInitializer.checkUserFlowBasedQuoteCreationFeasibility(
//                    userFlowParams,
//                    result = { isStarted, msg ->
//                        if (isStarted) {
////                            if (msg.equals("Online quote can not be created without internet")){
////                                showToast("Please Turn on the internet",this)
////                                prefs.Isfirst =true
////                            }
//                            startInspectionMain()
//                            Log.d("CQSDKXX", "isStarted " + msg)
//                        } else {
//
//
//                            Log.d("CQSDKXX", "Not isStarted" + msg)
//                        }
//
//                        if (!isStarted) {
//                            Log.e("startedinspection", "onCreateView: $msg$isStarted")
//                        }
//
//                    })


            } catch (e: Exception) {
                Log.d(inspectionTAG, "exc::  ${e.localizedMessage}   \n${e.message}")
                loadingDialog.dismiss()
            }
        } else {
            Log.d(inspectionTAG, "Not initialized")
        }

    }

    private fun startInspectionMain() {
        cqSDKInitializer.startInspection(activity = this,
            clientAttrs = ClientAttrs(
                userName = " ",
                dealer = " ",
                dealerIdentifier = " ",
                client_unique_id = inspectionID
            ),
            inputDetails = InputDetails(
                vehicleDetails = VehicleDetails(
                    regNumber = prefs.scannedVmRegNo.replace(" ", ""),
                    make = "Van", //if sent, user can't edit
                    model = "Any Model", //if sent, user can't edit
                    bodyStyle = "Van"  // if sent, user can't edit - Van, Boxvan, Sedan, SUV, Hatch, Pickup [case sensitive]
                ),
                customerDetails = CustomerDetails(
                    name = "", //if sent, user can't edit
                    email = "", //if sent, user can't edit
                    dialCode = "", //if sent, user can't edit
                    phoneNumber = "", //if sent, user can't edit
                )
            ),
            userFlowParams = UserFlowParams(
                isOffline = !startonetime!!,
                skipInputPage = true,
            ),


            result = { isStarted, msg, code ->
                loadingDialog.dismiss()
                Log.e(inspectionTAG, "onCreateView: startone $startonetime ")
                Log.e(inspectionTAG, "startInspection: $msg$code")
                Log.e(inspectionTAG, "regNo: ${prefs.scannedVmRegNo}")
                if (isStarted) {
                    prefs.Isfirst = false
                    startonetime = prefs.Isfirst
                    Log.d(inspectionTAG, "isStarted " + msg)
                } else {
                    prefs.Isfirst = true
                    startonetime = prefs.Isfirst
                    if (msg == "Online quote can not be created without internet") {
                        showToast("Please Turn on the internet", this)

                    } else if (msg == "Sufficient data not available to create an offline quote") {
                        showToast("Please Turn on the internet resources are downloading", this)
                    }

                    Log.d(inspectionTAG, "Not isStarted $msg")
                }
                if (msg == "Success") {
                    Log.d(inspectionTAG, "Success $msg")
                } else {
//                    prefs.Isfirst =true
//                    if (msg.equals("Online quote can not be created without internet")){
//                        showToast("Please Turn on the internet",this)
//                    }
//                    prefs.Isfirst =true
                    Log.d(inspectionTAG, "Not Success" + msg)
                }
                if (!isStarted) {
                    Log.e(inspectionTAG, "MSG: $msg\n IsStarted: $isStarted")
                }
            })
//                cqSDKInitializer.checkUserFlowBasedQuoteCreationFeasibility(
//                    UserFlowParams(isOffline = !startonetime!!,
//                        skipInputPage = true), result = {Boolean,String} )


//        prefs.Isfirst = false
        Log.d(inspectionTAG, "StartOneTime $startonetime")
    }

    private fun setUploadLabel() {
        if (!prefs.isInspectionDoneToday()) {
            prefs.updateInspectionStatus(false)
            binding.tvUploadType.text = "Start Full Inspection"
            binding.uploadBtnText.text = "Full Vehicle Inspection"
        } else if (prefs.addBlueUri == null && prefs.addBlueRequired) {
            binding.tvUploadType.text = "Add Blue Level Image"
            binding.uploadBtnText.text = "Upload Add Blue Level Image"
        } else if (prefs.oilLevelUri == null && prefs.oilLevelRequired) {
            binding.tvUploadType.text = "Oil Level Image"
            binding.uploadBtnText.text = "Upload Oil Level Image"
        } else {

            binding.ivUploadImage.setImageDrawable(
                ContextCompat.getDrawable(
                    this, R.drawable.ic_yes2
                )
            )
            binding.tvUploadMainTV.text = "Inspection Completed"
            binding.uploadBtnText.text = "Inspection Completed"
            binding.uploadBtnText.setTextColor(ContextCompat.getColor(this, R.color.orange))
            binding.tvUploadType.text = "You can exit and continue on remaining steps."

            binding.tvUploadType.text =
                "You can save and exit while images are being uploaded."
            /*
         if(osData!=null){

             osData.isdashboardUploadedFailed = false
             osData.isfrontImageFailed = false
             osData.isnearSideFailed = false
             osData.isrearSideFailed = false
             osData.isoffSideFailed = false
             osData.isaddblueImageFailed = false
             osData.isoillevelImageFailed = false
            }

             */

            startUploadWithWorkManager(0, prefs, this)
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
            Pair(null to !prefs.isInspectionDoneToday(), binding.fullvehicleInspection),
            Pair(prefs.addBlueUri to prefs.addBlueRequired, binding.addBlueIV),
            Pair(prefs.oilLevelUri to prefs.oilLevelRequired, binding.oilLevelIV)
        )

        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_yes2)
        imageViews.forEach { (image, imageView) ->
            if (image.first != null || !image.second) {
                uploadStatus += 1
                binding.uploadStatus.text = "$uploadStatus/3"
                if (uploadStatus == 3) {
                    binding.tvNext.setTextColor(ContextCompat.getColor(this, R.color.white))
                    binding.tvNext.isEnabled = true
                }

                imageView.setImageDrawable(drawable)
            }
        }
    }


}
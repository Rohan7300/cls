package com.clebs.celerity.ui

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.clebs.celerity.databinding.ActivityAddInspectionBinding
import com.clebs.celerity.databinding.ActivityAddInspectionTwoBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.BackgroundUploadDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.checkIfInspectionFailed
import com.clebs.celerity.utils.getCurrentDateTime
import com.clebs.celerity.utils.showErrorDialog
import com.clebs.celerity.utils.showToast
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.TimeZone

class AddInspectionTwoActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddInspectionTwoBinding
    lateinit var prefs: Prefs
    private lateinit var cqSDKInitializer: CQSDKInitializer
    var uploadMax: Int = 5
    private var startonetime: Boolean? = false
    private var isfirst: Boolean? = false
    private var imageCapture: ImageCapture? = null
    lateinit var loadingDialog: LoadingDialog
    var b64ImageList = mutableListOf<String>()
    var i = 0
    private lateinit var inspectionID: String
    lateinit var fragmentManager: FragmentManager
    var isadBluerequire: Boolean = false
    var DaVehicleAddBlueImage = String()
    var DaVehicleAddOilImage = String()
    private val imagePartsList = mutableListOf<MultipartBody.Part>()
    private var allImagesUploaded: Boolean = false
    lateinit var viewModel: MainViewModel

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_inspection_two)
        setContentView(binding.root)
        prefs = Prefs.getInstance(this)
        loadingDialog = LoadingDialog(this)
        initPreviewView()
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val todayDate = dateFormat.format(Date())
        var currentDateTime = getCurrentDateTime()
        viewModel.GetVehicleImageUploadInfo(
            prefs.clebUserId.toInt(),
            prefs.vmId.toInt(),
            currentDateTime
        )
        loadingDialog.show()
        clientUniqueID()
        cqSDKInitializer = CQSDKInitializer(this)
        startonetime = isfirst!!
        Prefs.getInstance(App.instance).isFirst = true

        isfirst = Prefs.getInstance(App.instance).isFirst
        viewModel.GetVehicleImageUploadInfo(
            prefs.clebUserId.toInt(),
            prefs.vmId.toInt(),
            currentDateTime
        )
        observers()
        binding.startinspection.setOnClickListener {

            startInspection()
//            if (allPermissionsGranted())
////                openClsCapture()
//            //uploadImage()
//            else {
////                requestpermissions()
//            }
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

    private fun clientUniqueID(): String {
        val x = Prefs.getInstance(App.instance).clebUserId.toString()
        val y = Prefs.getInstance(App.instance).scannedVmRegNo

        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))
        var regexPattern = Regex("${x.take(3)}${y.take(3)}${formattedDate}")
        prefs.inspectionID = regexPattern.toString()
        return regexPattern.toString()
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
//                openClsCapture()
                //uploadImage()
            }
        }

    private fun startInspection() {
//        if (isAllImageUploaded) {
//            mbinding.tvNext.visibility = View.VISIBLE
//        }

//      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
        loadingDialog.show()

        if (cqSDKInitializer.isCQSDKInitialized()) {

            var vmReg = Prefs.getInstance(App.instance).scannedVmRegNo ?: ""
            Log.e(
                "totyototyotoytroitroi",
                "startInspection: " + inspectionID + "VmReg ${Prefs.getInstance(App.instance).vmRegNo}"
            )
            if (vmReg.isEmpty()) {
                vmReg = Prefs.getInstance(App.instance).vmRegNo
            }
            Log.e("sdkskdkdkskdkskd", "onCreateView: ")

            try {
                cqSDKInitializer.startInspection(activity = this,
                    clientAttrs = ClientAttrs(
                        userName = " ",
                        dealer = " ",
                        dealerIdentifier = " ",
                        client_unique_id = inspectionID
                        //drivers ID +vechile iD + TOdays date dd// mm //yy::tt,mm
                    ),
                    inputDetails = InputDetails(
                        vehicleDetails = VehicleDetails(
                            regNumber = vmReg.replace(
                                " ",
                                ""
                            ), //if sent, user can't edit
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
                        isOffline = startonetime, // true, Offline quote will be created | false, online quote will be created | null, online

                        skipInputPage = true, // true, Inspection will be started with camera page | false, Inspection will be started

                    ),
                    result = { isStarted, msg, code ->
//                        Log.e("inspectionIDsssssssss", "startInspection: " + inspectionID)
//                        Log.e("messsagesss", "startInspection: " + msg + code)
                        if (isStarted) {
                            Prefs.getInstance(App.instance).inspectionID = inspectionID
                        } else {
//
                        }
                        if (msg == "Success") {
                            loadingDialog.cancel()
                        }
                        if (!isStarted) {
                            loadingDialog.cancel()
                            Log.e("startedinspection", "onCreateView: " + msg + isStarted)
                        }
                    })
            } catch (_: Exception) {

                showErrorDialog(fragmentManager, "CTF-02", "Please try again later!!")
            }
        }
    }

    private fun observers() {
        viewModel.vehicleImageUploadInfoLiveData.observe(this) {
            loadingDialog.dismiss()
            i = 0
            if (it != null) {


//                if (it.IsAdBlueRequired != null) {
//                    isadBluerequire = it.IsAdBlueRequired!!
//                }
//                DaVehicleAddBlueImage = it.DaVehicleAddBlueImage.toString()
//                if (it.IsAdBlueRequired == true && it.DaVehicleAddBlueImage.equals(null)) {
//                    binding.addbluell.visibility = View.VISIBLE
//                } else {
//                    binding.addbluell.visibility = View.GONE
//                }
//
//                if (it.DaVehImgOilLevelFileName.isNullOrEmpty()) {
//                    binding.oilLevelll.visibility = View.VISIBLE
//                } else {
//                    binding.oilLevelll.visibility = View.GONE
//                }

                /*                    if (it.DaVehImgDashBoardFileName != null || osData.dashboardImage != null)
                                        i += 1
                                    if (it.DaVehImgFrontFileName != null || osData.frontImage != null)
                                        i += 1
                                    if (it.DaVehImgRearFileName != null || osData.rearSideImage != null)
                                        i += 1
                                    if (it.DaVehImgNearSideFileName != null || osData.nearSideImage != null)
                                        i += 1
                                    if (it.DaVehImgOffSideFileName != null || osData.offSideImage != null)
                                        i += 1*/
                if (it.DaVehImgDashBoardFileName != null &&
                    it.DaVehImgFrontFileName != null &&
                    it.DaVehImgRearFileName != null &&
                    it.DaVehImgNearSideFileName != null &&
                    it.DaVehImgOffSideFileName != null &&
                    it.DaVehicleAddBlueImage != null &&
                    it.DaVehImgOilLevelFileName != null

                ) {
//                        generateInspectionID()
//                        allImagesUploaded = true
                    showToast("Inspection Completed", this)
//                        onSaveClick()
                    //uploadStatus(i)
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

}
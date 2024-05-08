package com.clebs.celerity.ui


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityAddInspectionBinding
import com.clebs.celerity.models.requests.SaveVehicleInspectionInfo
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.BackgroundUploadDialog
import com.clebs.celerity.utils.BackgroundUploadDialogListener
import com.clebs.celerity.utils.ClsCapture
import com.clebs.celerity.utils.Constants.Companion.LABELS_PATH
import com.clebs.celerity.utils.Constants.Companion.MODEL_PATH
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.toRequestBody


import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddInspection : AppCompatActivity(), BackgroundUploadDialogListener {
    lateinit var binding: ActivityAddInspectionBinding
    lateinit var prefs: Prefs
    private lateinit var backgroundUploadDialog: BackgroundUploadDialog
    lateinit var loadingDialog: LoadingDialog
    var i = 0
    lateinit var fragmentManager: FragmentManager
    private val isFrontCamera = false

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null


    private lateinit var cameraExecutor: ExecutorService
    private val imagePartsList = mutableListOf<MultipartBody.Part>()
    private var allImagesUploaded: Boolean = false
    lateinit var viewModel: MainViewModel


    companion object {
        private val REQUIRED_PERMISSIONSs =
            mutableListOf(
                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    add(Manifest.permission.READ_MEDIA_VIDEO)
//                    add(Manifest.permission.READ_MEDIA_IMAGES)
//                    add(Manifest.permission.READ_MEDIA_AUDIO)
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

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]

        observers()
        viewModel.GetVehicleImageUploadInfo(prefs.clebUserId.toInt())
        loadingDialog.show()
        clientUniqueID()
        binding.ivUploadImage.setOnClickListener {
//            uploadImage()
            if (allPermissionsGranted()) {
//                binding.llupload.visibility = View.GONE
//                binding.constcamera.visibility = View.VISIBLE
                startUploadtwo()
            } else {
                requestpermissions()
//                binding.llupload.visibility = View.VISIBLE
//                binding.constcamera.visibility = View.GONE
            }
        }
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.tvUploadMainTV.setOnClickListener {
//            uploadImage()
            if (allPermissionsGranted()) {
//                binding.llupload.visibility = View.GONE
//                binding.constcamera.visibility = View.VISIBLE
                startUploadtwo()
            } else {
                requestpermissions()
//                binding.llupload.visibility = View.VISIBLE
//                binding.constcamera.visibility = View.GONE
            }
        }
        binding.imageViewBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("destinationFragment", "CompleteTask")
            startActivity(intent)
        }



        binding.tvNext.setOnClickListener {
            startUpload()

            backgroundUploadDialog.show(this.supportFragmentManager, BackgroundUploadDialog.TAG)

            //    viewModel.uploadVehicleImages(prefs.clebUserId.toInt(), imagePartsList)
        }


    }

    private fun uploadImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 0)
    }

    fun startUploadtwo() {
        val intent = Intent(this, ClsCapture::class.java)
        startActivity(intent)
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener({
//            cameraProvider  = cameraProviderFuture.get()
//            cameraPreview()
//        }, ContextCompat.getMainExecutor(this))


    }


    fun cameraPreview() {
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

//        val rotation = binding.camera.display.rotation

        val cameraSelector = CameraSelector
            .Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

//        preview = Preview.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setTargetRotation(rotation)
//            .build()
//        val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1f, 1f)
//            .createPoint(.5f, .5f)
//        try {
//            val autoFocusAction = FocusMeteringAction.Builder(
//                autoFocusPoint,
//                FocusMeteringAction.FLAG_AF
//            ).apply {
//                //start auto-focusing after 2 seconds
//                setAutoCancelDuration(6, TimeUnit.SECONDS)
//            }.build()
//            camera?.cameraControl?.startFocusAndMetering(autoFocusAction)
//        } catch (e: CameraInfoUnavailableException) {
//            Log.d("ERROR", "cannot access camera", e)
//        }
//            preview.afterMeasured {
//            val autoFocusPoint = SurfaceOrientedMeteringPointFactory(1f, 1f)
//                .createPoint(.5f, .5f)
//            try {
//                val autoFocusAction = FocusMeteringAction.Builder(
//                    autoFocusPoint,
//                    FocusMeteringAction.FLAG_AF
//                ).apply {
//                    //start auto-focusing after 2 seconds
//                    setAutoCancelDuration(2, TimeUnit.SECONDS)
//                }.build()
//                camera?.cameraControl?.startFocusAndMetering(autoFocusAction)
//            } catch (e: CameraInfoUnavailableException) {
//                Log.d("ERROR", "cannot access camera", e)
//            }
//        }

//        imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(AspectRatio.RATIO_4_3)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .setTargetRotation(binding.camera.display.rotation)
//            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
//
//            .build()

        imageAnalyzer?.setAnalyzer(cameraExecutor) { imageProxy ->
            val bitmapBuffer =
                Bitmap.createBitmap(
                    imageProxy.width,
                    imageProxy.height,
                    Bitmap.Config.ARGB_8888
                )


//
            imageProxy.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }
            imageProxy.close()

            val matrix = Matrix().apply {
                postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())

                if (isFrontCamera) {
                    postScale(
                        -1f,
                        1f,
                        imageProxy.width.toFloat(),
                        imageProxy.height.toFloat()
                    )
                }
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
                matrix, true
            )

//            detector.detect(rotatedBitmap)

        }

        cameraProvider.unbindAll()

        try {
            camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageAnalyzer
            )

//            preview?.setSurfaceProvider(binding.camera.surfaceProvider)


        } catch (exc: Exception) {
            Log.e("nothing", "Use case binding failed", exc)
        }


    }


    private fun observers() {
        viewModel.vehicleImageUploadInfoLiveData.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                i = 0
                if (it.DaVehImgDashBoardFileName != null)
                    i += 1
                if (it.DaVehImgFrontFileName != null)
                    i += 1
                if (it.DaVehImgRearFileName != null)
                    i += 1
                if (it.DaVehImgNearSideFileName != null)
                    i += 1
                if (it.DaVehImgOffSideFileName != null)
                    i += 1
                if (it.DaVehImgDashBoardFileName != null && it.DaVehImgFrontFileName != null && it.DaVehImgRearFileName != null && it.DaVehImgNearSideFileName != null && it.DaVehImgOffSideFileName != null && i >= 5) {
                    generateInspectionID()
                    allImagesUploaded = true
                }

                uploadStatus(i)
            } else {
                uploadStatus(0)
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

                viewModel.GetVehicleImageUploadInfo(prefs.clebUserId.toInt())
            }
        }
    }

    private fun uploadStatus(i: Int) {
        val uploadStatus = "($i/5)"
        binding.uploadStatus.text = uploadStatus
        with(binding) {
            listOf(
                dashboardStatusIV,
                frontStatusIV,
                nearSideStatusIV,
                rearSideStatusIV,
                offsideStatusIV
            ).forEach {
                it.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@AddInspection,
                        R.drawable.fileins
                    )
                )
            }
        }
        when (i) {
            0 -> {
                binding.tvUploadType.text = "Dashboard Image"
            }

            1 -> {
                binding.tvUploadType.text = "Front Image"
                binding.dashboardStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
            }

            2 -> {
                binding.tvUploadType.text = "Near Side Image"
                binding.dashboardStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
                binding.frontStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
            }

            3 -> {
                binding.tvUploadType.text = "Rear Side Image"
                binding.dashboardStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
                binding.frontStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
                binding.nearSideStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
            }

            4 -> {
                binding.tvUploadType.text = "Offside Image"
                binding.dashboardStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
                binding.frontStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
                binding.nearSideStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
                binding.rearSideStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
            }

            else -> {
                if (allImagesUploaded) {
                    binding.ivUploadImage.visibility = View.VISIBLE
                    binding.uploadProgress.visibility = View.GONE
                    binding.ivUploadImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            this, R.drawable.ic_yes2
                        )
                    )
                    binding.tvUploadMainTV.text = "Inspection Completed"
                    binding.tvUploadType.text = "You can exit and continue on remaining steps."
                } else {
                    binding.ivUploadImage.visibility = View.GONE
                    binding.uploadProgress.visibility = View.VISIBLE
                    //binding.tvUploadMainTV.text = "Save and Upload"
                    binding.tvUploadMainTV.visibility = View.GONE
                    binding.tvUploadType.text =
                        "You can save and exit while images are being uploaded."
                }

                binding.dashboardStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
                binding.frontStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
                binding.nearSideStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
                binding.rearSideStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )
                binding.offsideStatusIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.check_new
                    )
                )

                binding.tvUploadMainTV.isEnabled = false
                binding.ivUploadImage.isEnabled = false
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            sendImage(imageBitmap, requestCode)
        }
    }

    private fun sendImage(imageBitmap: Bitmap, requestCode: Int) {
        val uniqueFileName = "image_${UUID.randomUUID()}.jpg"
        val requestBody = imageBitmap.toRequestBody()
        val partName = when (imagePartsList.size) {
            0 -> "uploadVehicleDashBoardImage"
            1 -> "uploadVehicleFrontImage"
            2 -> "uploadVehicleNearSideImage"
            3 -> "uploadVehicleRearImage"
            4 -> "uploadVehicleOffSideImage"
            else -> "Invalid"
        }
        val imagePart =
            MultipartBody.Part.createFormData(partName, uniqueFileName, requestBody)

        imagePartsList.add(imagePart)
        val x = imagePartsList.size
        uploadStatus(x)
        if (imagePartsList.size == 5) {
            binding.tvNext.isEnabled = true
            binding.tvNext.setTextColor(ContextCompat.getColor(this, R.color.white))
        }
    }

    private fun startUpload() {
        showToast("Image Upload Started", this)
        viewModel.uploadVehicleImage(prefs.clebUserId.toInt(), imagePartsList[0], 1)
        viewModel.uploadVehicleImage(prefs.clebUserId.toInt(), imagePartsList[1], 2)
        viewModel.uploadVehicleImage(prefs.clebUserId.toInt(), imagePartsList[2], 3)
        viewModel.uploadVehicleImage(prefs.clebUserId.toInt(), imagePartsList[3], 4)
        viewModel.uploadVehicleImage(prefs.clebUserId.toInt(), imagePartsList[4], 6)
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

    override fun onSaveClick() {
        generateInspectionID()
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("destinationFragment", "CompleteTask")
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
        viewModel.SaveVehicleInspectionInfo(
            SaveVehicleInspectionInfo(
                prefs.clebUserId.toInt(),
                currentDate,
                prefs.inspectionID,
                locationID,
                prefs.VmID.toString().toInt()
            )
        )
    }

    private fun allPermissionsGranted() = AddInspection.REQUIRED_PERMISSIONSs.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestpermissions() {

        activityResultLauncher.launch(REQUIRED_PERMISSIONSs)

    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONSs && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast("Permission denied", this)
            } else {

            }
        }


}
package com.clebs.celerity.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ais.plate_req_api.model.NumberPlateResponse
import com.ais.plate_req_api.webService.RetrofitHelper
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentDailyWorkBinding
import com.clebs.celerity.network.ApiPlateRecognizer
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.ui.HomeActivity.Companion.showLog
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.ScanErrorDialogListener
import com.clebs.celerity.utils.getFileFromUri
import com.clebs.celerity.utils.navigateTo
import com.clebs.celerity.utils.showScanErrorDialog
import com.clebs.celerity.utils.showToast
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.kotlinpermissions.KotlinPermissions
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.tapadoo.alerter.Alerter
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DailyWorkFragment : Fragment(), ScanErrorDialogListener {
    lateinit var mbinding: FragmentDailyWorkBinding
    private lateinit var mainViewModel: MainViewModel
    private val API_TOKEN = "9d04d01d5ba1997289fa28f6f544b16ab9e5a8b6"
    private lateinit var loadingDialog: LoadingDialog
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private var isFrontCamera = false


    var vrn: String = ""
    private var imageBitmap: Bitmap? = null
    var countryCode: String = ""
    var txt: String = ""
    var vehicleType: String = ""
    private var counter = 0
    var score: String = ""
    var bounding: String = ""
    private lateinit var fragmentManager: FragmentManager

    companion object {

        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
//                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    add(Manifest.permission.READ_MEDIA_VIDEO)
//                    add(Manifest.permission.READ_MEDIA_IMAGES)
//                    add(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }.toTypedArray()

        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentManager = (activity as HomeActivity).fragmentManager
        //   cameraExecutor = Executors.newSingleThreadExecutor()


    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentDailyWorkBinding.inflate(inflater, container, false)
        }
        HomeActivity.Boolean = true
        loadingDialog = (activity as HomeActivity).loadingDialog
        cameraExecutor = Executors.newSingleThreadExecutor()
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)
        mainViewModel.setLastVisitedScreenId(requireContext(), R.id.dailyWorkFragment)
//


//        Log.e(
//            TAG,
//            "onCreateViewhint: " + showhint + Prefs.getInstance(App.instance).get("showhint")
//        )


        showToolTip()



        mbinding.rectangle4.setOnClickListener {
            if (allPermissionsGranted()) {
                mbinding.rectange.visibility = View.VISIBLE
                mbinding.ivTakePhoto.visibility = View.VISIBLE
                mbinding.rectangle4.visibility = View.GONE
                startCamera()
                initListeners()
            } else {
                requestpermissions()
            }

//            checkPermissions()
            //findNavController().navigate(R.id.vechileMileageFragment)
        }



        return mbinding.root
    }

    fun showToolTip() {


        BubbleShowCaseBuilder(requireActivity()) //Activity instance
            .title("Capture") //Any title for the bubble view
            .description("Click here to capture Vehicle Registration number.") //More detailed description
            .arrowPosition(BubbleShowCase.ArrowPosition.TOP)
            //You can force the position of the arrow to change the location of the bubble.
            .backgroundColor((requireContext().getColor(R.color.very_light_orange)))
            //Bubble background color
            .textColor(requireContext().getColor(R.color.black)) //Bubble Text color
            .titleTextSize(16) //Title text size in SP (default value 16sp)
            .descriptionTextSize(12) //Subtitle text size in SP (default value 14sp)
            .image(requireContext().resources.getDrawable(R.drawable.baseline_document_scanner_24)!!) //Bubble main image
            .closeActionImage(requireContext().resources.getDrawable(R.drawable.cross)!!) //Custom close action image

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
            )
            .targetView(mbinding.scanLayout)
            .showOnce("2")
            .highlightMode(BubbleShowCase.HighlightMode.VIEW_SURFACE) //View to point out
            .show()


    }

    fun checkPermissions() {

//            runWithPermissions(
//              Manifest.permission.CAMERA,
////              Manifest.permission.READ_MEDIA_IMAGES,
////               Manifest.permission.READ_MEDIA_VIDEO,
////              Manifest.permission.READ_MEDIA_AUDIO
//
//            ) {
//               startCamera()
//                initListeners()
//            }
//        } else {
//            runWithPermissions(
//              Manifest.permission.CAMERA,
////                Manifest.permission.READ_EXTERNAL_STORAGE,
////                Manifest.permission.WRITE_EXTERNAL_STORAGE
//
//            ) {
//               startCamera()
//                initListeners()
//            }
//        }


        // Request camera permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            KotlinPermissions.with(requireActivity()) // Where this is an FragmentActivity instance
                .permissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO,

                    ).onAccepted {
                    mbinding.rectange.visibility = View.VISIBLE
                    mbinding.ivTakePhoto.visibility = View.VISIBLE
                    mbinding.rectangle4.visibility = View.GONE


                    startCamera()
                    initListeners()
                }.onDenied {
                    mbinding.rectange.visibility = View.GONE
                    mbinding.rectangle4.visibility = View.VISIBLE

                    mbinding.ivTakePhoto.visibility = View.GONE
                    Log.d(TAG, "User denied permissions")
                }.onForeverDenied {
                    Log.d(TAG, "User forever denied permissions")
                }.ask()
        } else {

            KotlinPermissions.with(requireActivity()) // Where this is an FragmentActivity instance
                .permissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,

                    ).onAccepted {
                    mbinding.rectange.visibility = View.VISIBLE
                    mbinding.ivTakePhoto.visibility = View.VISIBLE
                    mbinding.rectangle4.visibility = View.GONE


                    startCamera()
                    initListeners()
                }.onDenied {
                    mbinding.rectange.visibility = View.GONE
                    mbinding.rectangle4.visibility = View.VISIBLE

                    mbinding.ivTakePhoto.visibility = View.GONE
                    Log.d(TAG, "User denied permissions")
                }.onForeverDenied {
                    Log.d(TAG, "User forever denied permissions")
                }.ask()
        }


    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast("Permission denied", requireContext())
            } else {
                mbinding.rectange.visibility = View.VISIBLE
                mbinding.ivTakePhoto.visibility = View.VISIBLE
                mbinding.rectangle4.visibility = View.GONE
                startCamera()
                initListeners()
            }
        }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestpermissions() {

        activityResultLauncher.launch(REQUIRED_PERMISSIONS)

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initListeners() = try {
        mbinding.ivTakePhoto.setOnClickListener {
            takePhoto()
        }

    } catch (e: Exception) {
        Log.d(TAG, "Photo capture failed: ${e.message}")
    }

    @SuppressLint("NewApi")
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(mbinding.rectange.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            PreviewView.ImplementationMode.COMPATIBLE
            val cameraSelector =
                CameraSelector.DEFAULT_BACK_CAMERA
            try {

                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (e: Exception) {
                Log.d(TAG, "Use case binding failed ${e.message}")
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    open fun detectTxt() {

        val image = FirebaseVisionImage.fromBitmap(imageBitmap!!)
        val detector: FirebaseVisionTextRecognizer =
            FirebaseVision.getInstance().onDeviceTextRecognizer
        detector.processImage(image)
            .addOnSuccessListener { firebaseVisionText ->
                processTxt(firebaseVisionText)
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occur during the text recognition process
            }
    }

    open fun processTxt(text: FirebaseVisionText) {

        val blocks: List<FirebaseVisionText.TextBlock> = text.textBlocks


        if (blocks.size == 0) {
            mbinding.pb.visibility = View.GONE
            //showToast("No Text ", requireContext())
            showScanErrorDialog(
                this,
                fragmentManager,
                "DWF-03",
                "Vehicle doesn't exists. Please scan again or contact your supervisor."
            )
            return
        }

        for (block in text.textBlocks) {
            val lineText = block.lines.get(0).text
            txt = lineText.replace(" ", "")
            Log.e(TAG, "processTxtscanning: $txt")
            if (txt.isNotEmpty()) {
                getVichleinformation()
            }

        }
    }

    @SuppressLint("NewApi")
    private fun takePhoto() = try {
//        mbinding.pb.visibility = View.VISIBLE
        loadingDialog.show()
        mbinding.rectange.visibility = View.GONE
        mbinding.ivTakePhoto.visibility = View.GONE
        mbinding.rectangle4.visibility = View.VISIBLE
//        mbinding.imageView5.visibility = View.GONE


        val imageCapture = imageCapture ?: throw IOException("Camera not connected")
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        val outputOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            ImageCapture.OutputFileOptions
                .Builder(
                    requireContext().contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ).build()
        else
            ImageCapture.OutputFileOptions
                .Builder(
                    requireContext().contentResolver,
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    contentValues
                ).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.d(TAG, "Photo capture failed")
                    println("Photo capture failed")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "Photo capture succeeded processing photo")

                    showToast("Photo capture succeeded processing photo", requireContext())
                    imageBitmap = getImageBitmapFromUri(requireContext(), output.savedUri!!)

                    uploadImageToServerAndGetResults(output.savedUri)

//                    if (imageBitmap != null) {
//
//                        detectTxt()
//                    }

//                    mbinding.rectangle4.setImageBitmap(imageBitmap)
                }
            }
        )
    } catch (e: Exception) {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
        Log.d(TAG, "Photo capture failed: ${e.message}")
        println("Photo capture failed: ${e.message}")
    }

    fun getImageBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }


    fun uploadImageToServerAndGetResults(savedUri: Uri?) {
        if (savedUri != null) {
            Alerter.create(requireActivity())
                .setTitle("Analysing")
                .setIcon(R.drawable.logo_new)
                .setText("Photo is been analysed")
                .setBackgroundColorInt(resources.getColor(R.color.medium_orange))
                .show()
            val apiService: ApiPlateRecognizer =
                RetrofitHelper.getInstance().create(ApiPlateRecognizer::class.java)
            GlobalScope.launch(Dispatchers.IO) {
                val imgFile = requireContext().getFileFromUri(savedUri)
                val compressedImageFile = Compressor.compress(requireContext(), imgFile)
                val imageFilePart = MultipartBody.Part.createFormData(
                    "upload",
                    compressedImageFile.name,
                    RequestBody.create(
                        "image/*".toMediaTypeOrNull(),
                        compressedImageFile
                    )
                )
                val response =
                    apiService.getNumberPlateDetails(
                        token = "TOKEN $API_TOKEN",
                        imagePart = imageFilePart
                    )
                if (response.isSuccessful && response.body() != null) {
                    if ((response.body()?.results?.size ?: 0) > 0) {
                        withContext(Dispatchers.Main) {

                            // Set variables for vehicle data.
                            vrn = response.body()?.results?.get(0)?.plate.toString().uppercase()
                            countryCode = response.body()?.results?.get(0)?.region?.code.toString()
                                .uppercase()
                            vehicleType = response.body()?.results?.get(0)?.vehicle?.type.toString()
                                .uppercase()

                            score = response.body()?.results?.get(0)?.score.toString()

                            bounding = response.body()?.results?.get(0)?.box.toString()

                            Log.d(TAG, response.body()?.results.toString())
                            getVichleinformation()

                        }
                    } else {
                        showScanErrorDialog(
                            this@DailyWorkFragment,
                            fragmentManager,
                            "DWF-03",
                            " This Vehicle ${if (vrn.isNotEmpty()) (vrn) else ""} doesn't exists. Please scan again or contact your supervisor."
                        )
//                        if (loadingDialog.isShowing){
//                            loadingDialog.dismiss()
//                        }

//                        mbinding.pb.visibility=View.GONE
                        withContext(Dispatchers.Main) {
                            Log.d(TAG, "No VRN found in image.")

                        }
                        if (loadingDialog.isShowing){
                            loadingDialog.dismiss()
                        }
                    }
                }
                else{
                    if (loadingDialog.isShowing){
                        loadingDialog.dismiss()
                    }
                }
            }
        }
    }


    fun getVichleinformation() {
        Prefs.getInstance(App.instance).vmRegNo = vrn
        (activity as HomeActivity).getVehicleLocationInfo()
        mainViewModel.getVichelinformationResponse(
            Prefs.getInstance(App.instance).userID.toString().toDouble(), 0.toDouble(), vrn
        ).observe(requireActivity(), Observer {
            if (it != null) {

                Prefs.getInstance(App.instance)
                    .save("vehicleLastMillage", it.vehicleLastMillage.toString())
                mbinding.rectange.visibility = View.GONE
                mbinding.ivTakePhoto.visibility = View.GONE
                mbinding.rectangle4.visibility = View.VISIBLE
//                mbinding.imageView5.visibility = View.VISIBLE
                if (txt.isNotEmpty()) {
                    Prefs.getInstance(App.instance).save("vrn", txt)
                }

                mbinding.pb.visibility = View.GONE
                showLog(
                    "TAG------->",
                    "mymileage" + it.vehicleLastMillage.toString() + Prefs.getInstance(App.instance)
                        .get("vehicleLastMillage")
                )
                //showAlert()
                navigateTo(R.id.vechileMileageFragment, requireContext(), findNavController())
                if (loadingDialog.isShowing) {
                    loadingDialog.dismiss()
                }
            } else {
                showScanErrorDialog(
                    this,
                    fragmentManager,
                    "",
                    "This Vehicle ${if (vrn.isNotEmpty()) "[$vrn]" else ""} doesn't exists. Please scan again or contact your supervisor."
                )
                /*     showToast(
                         "Vehicle doesn't exists. Please scan again or contact your supervisor.",
                         requireContext()
                     )*/
                mbinding.rectange.visibility = View.GONE
                mbinding.ivTakePhoto.visibility = View.GONE
                mbinding.rectangle4.visibility = View.VISIBLE
//                mbinding.imageView5.visibility = View.VISIBLE
                if (loadingDialog.isShowing) {
                    loadingDialog.dismiss()
                }
            }

        })


    }

    fun showAlert() {
        val factory = LayoutInflater.from(requireActivity())
        val view: View = factory.inflate(R.layout.acknowledgement, null)
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        val checkBox: CheckBox = view.findViewById<CheckBox>(R.id.checkbox)
        val image: ImageView = view.findViewById(R.id.img_Acknowledege)
        image.setOnClickListener {

            if (checkBox.isChecked) {
                // findNavController().navigate(R.id.vechileMileageFragment)
                navigateTo(R.id.vechileMileageFragment, requireContext(), findNavController())
//             deleteDialog.window!!.setWindowAnimations(R.style.ExplodeAnimation)
//                val explosionField = ExplosionField.attach2Window(requireActivity())
//                explosionField.explode(view)
//                dismissAlertDialogWithAnimation(deleteDialog)
                deleteDialog.dismiss()


            } else {
                showToast("Please check the acknowledgement check", requireContext())
            }
        }

        deleteDialog.setView(view)
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                true
            } else {
                false
            }
        }
//        deleteDialog.window!!.getAttributes().windowAnimations = R.style.ExplodeAnimation;
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }

    //    private fun dismissAlertDialogWithAnimation(alertDialog: AlertDialog) {
//        val explodeAnimation = AnimationUtils.loadAnimation(context, R.anim.explode)
//        alertDialog.window?.decorView?.startAnimation(explodeAnimation)
//
//        Handler().postDelayed({
//            alertDialog.dismiss()
//        }, 300) // Delay the dismissal to match the animation duration
//    }
    override fun onTryAgainClicked() {
//        checkPermissions()
        requestpermissions()
    }


}
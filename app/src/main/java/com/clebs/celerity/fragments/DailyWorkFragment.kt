package com.clebs.celerity.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentDailyWorkBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.ui.HomeActivity.Companion.showLog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showErrorDialog
import com.clebs.celerity.utils.showToast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer
import com.kotlinpermissions.KotlinPermissions
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

open class DailyWorkFragment : Fragment() {
    lateinit var mbinding: FragmentDailyWorkBinding
    private lateinit var mainViewModel: MainViewModel
    private val API_TOKEN = "9d04d01d5ba1997289fa28f6f544b16ab9e5a8b6"

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private var isFrontCamera = false

    var vrn: String = ""
    private var imageBitmap: Bitmap? = null
    var countryCode: String = ""
    var txt: String = ""
    var vehicleType: String = ""
    var score: String = ""
    var bounding: String = ""
    private lateinit var fragmentManager: FragmentManager

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

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)
        mainViewModel.setLastVisitedScreenId(requireContext(), R.id.dailyWorkFragment)




        mbinding.rectangle4.setOnClickListener {
            checkPermissions()
            //findNavController().navigate(R.id.vechileMileageFragment)
        }



        return mbinding.root
    }

    private fun checkPermissions() {
        // Request camera permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            KotlinPermissions.with(requireActivity()) // Where this is an FragmentActivity instance
                .permissions(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                ).onAccepted {
                    mbinding.rectange.visibility = View.VISIBLE
                    mbinding.ivTakePhoto.visibility = View.VISIBLE
                    mbinding.rectangle4.visibility = View.GONE
                    mbinding.imageView5.visibility = View.GONE

                    startCamera()
                    initListeners()
                }.onDenied {
                    mbinding.rectange.visibility = View.GONE
                    mbinding.rectangle4.visibility = View.VISIBLE
                    mbinding.imageView5.visibility = View.VISIBLE
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
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).onAccepted {
                    mbinding.rectange.visibility = View.VISIBLE
                    mbinding.ivTakePhoto.visibility = View.VISIBLE
                    mbinding.rectangle4.visibility = View.GONE
                    mbinding.imageView5.visibility = View.GONE

                    startCamera()
                    initListeners()
                }.onDenied {
                    mbinding.rectange.visibility = View.GONE
                    mbinding.rectangle4.visibility = View.VISIBLE
                    mbinding.imageView5.visibility = View.VISIBLE
                    mbinding.ivTakePhoto.visibility = View.GONE
                    Log.d(TAG, "User denied permissions")
                }.onForeverDenied {
                    Log.d(TAG, "User forever denied permissions")
                }.ask()
        }
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
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(mbinding.rectange.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector =
                if (isFrontCamera)
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else
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
            showToast("No Text ", requireContext())
            return
        }

        for (block in text.textBlocks) {

            txt = block.getText()
            if (txt.isNotEmpty()) {
                getVichleinformation()
            }
            Log.e(TAG, "processTxt: $txt")
        }
    }

    @SuppressLint("NewApi")
    private fun takePhoto() = try {
        mbinding.pb.visibility = View.VISIBLE
        mbinding.rectange.visibility = View.GONE
        mbinding.ivTakePhoto.visibility = View.GONE
        mbinding.rectangle4.visibility = View.VISIBLE
        mbinding.imageView5.visibility = View.GONE


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
                    if (imageBitmap != null) {

                        detectTxt()
                    }


//                    mbinding.rectangle4.setImageBitmap(imageBitmap)

                }
            }
        )
    } catch (e: Exception) {
        mbinding.pb.visibility = View.GONE
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


    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }


    private fun getVichleinformation() {
        Prefs.getInstance(App.instance).vmRegNo = txt
        mainViewModel.getVichelinformationResponse(
            Prefs.getInstance(App.instance).userID.toString().toDouble(), 0.toDouble(), txt
        ).observe(requireActivity(), Observer {
            if (it != null) {
                Prefs.getInstance(App.instance)

                    .save("vehicleLastMillage", it.vehicleLastMillage.toString())
                mbinding.rectange.visibility = View.GONE
                mbinding.ivTakePhoto.visibility = View.GONE
                mbinding.rectangle4.visibility = View.VISIBLE
                mbinding.imageView5.visibility = View.VISIBLE
                if (txt.isNotEmpty()) {
                    Prefs.getInstance(App.instance).save("vrn", txt)
                }

                mbinding.pb.visibility = View.GONE
                showLog(
                    "TAG------->",
                    "mymileage" + it.vehicleLastMillage.toString() + Prefs.getInstance(App.instance)
                        .get("vehicleLastMillage")
                )
                showAlert()
            } else {
            showErrorDialog(fragmentManager,"DWF-01","Vehicle doesn't exists. Please scan again or contact your supervisor.")
           /*     showToast(
                    "Vehicle doesn't exists. Please scan again or contact your supervisor.",
                    requireContext()
                )*/
                mbinding.rectange.visibility = View.GONE
                mbinding.ivTakePhoto.visibility = View.GONE
                mbinding.rectangle4.visibility = View.VISIBLE
                mbinding.imageView5.visibility = View.VISIBLE
                mbinding.pb.visibility = View.GONE
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
                findNavController().navigate(R.id.vechileMileageFragment)
                deleteDialog.dismiss()

            } else {
                showToast("Please check the acknowledgment check", requireContext())
            }
        }

        deleteDialog.setView(view)
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }


}
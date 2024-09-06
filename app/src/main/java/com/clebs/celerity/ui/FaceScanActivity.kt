package com.clebs.celerity.ui


import ObjectDetectorHelper
import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.OSyncVMProvider
import com.clebs.celerity.ViewModel.OSyncViewModel
import com.clebs.celerity.databinding.ActivityFaceScanBinding
import com.clebs.celerity.fragments.DailyWorkFragment
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.currentUri
import com.clebs.celerity.utils.DependencyProvider.isComingBackFromFaceScan
import com.clebs.celerity.utils.DependencyProvider.osData
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast
import org.jetbrains.anko.runOnUiThread
import org.tensorflow.lite.task.vision.detector.Detection
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.text.Typography.registered


class FaceScanActivity : AppCompatActivity(),ObjectDetectorHelper.DetectorListener {
    lateinit var binding: ActivityFaceScanBinding
    private var imageCapture: ImageCapture? = null

    private lateinit var outputDirectory: File
    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    lateinit var oSyncViewModel: OSyncViewModel
    private lateinit var bitmapBuffer: Bitmap
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_face_scan)
        imageCapture = ImageCapture.Builder().build()
        objectDetectorHelper = ObjectDetectorHelper(
            context = this,
            objectDetectorListener = this
        )
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val todayDate = dateFormat.format(Date())
        val osRepo = DependencyProvider.offlineSyncRepo(this)
        oSyncViewModel = ViewModelProvider(
            this,
            OSyncVMProvider(osRepo, Prefs.getInstance(this).clebUserId.toInt(), todayDate)
        )[OSyncViewModel::class.java]

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.faceScanPreviewView.post {
            setUpCamera()
            binding.capture.setOnClickListener {
                try {
                    val imageCapture = imageCapture ?: throw IOException("Camera not connected")
                    val name = SimpleDateFormat(DailyWorkFragment.FILENAME_FORMAT, Locale.US)
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
                                contentResolver,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            ).build()
                    else{
                        outputDirectory = getOutputDirectory()
                        val file = createFile(
                            outputDirectory,
                            "yyyy-MM-dd-HH-mm-ss-SSS",
                            ".jpg"
                        )
                        ImageCapture.OutputFileOptions
                            .Builder(file).build()
                    }


                    imageCapture.takePicture(
                        outputOptions, ContextCompat.getMainExecutor(this),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(exc: ImageCaptureException) {
                                Log.d(ContentValues.TAG, "Photo capture failed")
                            }

                            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                showToast(
                                    "Photo capture succeeded processing photo",
                                    this@FaceScanActivity
                                )
                                currentUri = output.savedUri
                                if(osData.isIni){
                                    osData.faceMaskImage = currentUri.toString()
                                    osData.isImagesUploadedToday = true
                                    oSyncViewModel.insertData(osData)
                                }
                                print("CurrentURi $currentUri")
                                isComingBackFromFaceScan = true
                                finish()
                            }
                        }
                    )
                } catch (e: Exception) {
                    Log.d(ContentValues.TAG, "Photo capture failed: ${e.message}")
                    println("Photo capture failed: ${e.message}")
                }
            }
        }


    }

    override fun onError(error: String) {

    }

    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {
        applicationContext?.runOnUiThread {

            //if result has detection equals car


            val resultss = results ?: LinkedList<Detection>()
            //if result has detection equals car
            val containsCar = resultss.any { detection ->
                detection.categories[0].label.equals("person")


            }
            val score = resultss.any { detection ->

                String.format("%.2f", detection.categories[0].score) >= 0.60.toString()
            }

            val containstruck = resultss.any { detection ->

                detection.categories[0].label.equals("face")
            }
            if (results != null && binding != null) {
                if (containsCar || containstruck && score) {


                    binding.capture.visibility = View.VISIBLE

                } else {
                    binding.capture.visibility = View.GONE

                }
            }

            binding.overlay.setResults(
                results ?: LinkedList<Detection>(),
                imageHeight,
                imageWidth
            )

            binding.overlay.invalidate()
        }
    }
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this
        )
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(this)
        )
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageE    rror")
    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview =
            Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(binding.faceScanPreviewView.display.rotation)
                .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_DEFAULT)
                .setTargetRotation(binding.faceScanPreviewView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()
                // The analyzer can then be assigned to the instance
                .also {
                    it.setAnalyzer(cameraExecutor) { image ->
                        if (!::bitmapBuffer.isInitialized) {
                            // The image rotation and RGB image buffer are initialized only once
                            // the analyzer has started running
                            bitmapBuffer = Bitmap.createBitmap(
                                image.width,
                                image.height,
                                Bitmap.Config.ARGB_8888
                            )
                        }

                        detectObjects(image)
                    }
                }

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            this, cameraSelector, preview, imageCapture
        )
        try {
            // A variable number of use-cases can be passed here -
            // camera provides access to CameraControl & CameraInfo
            camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(binding.faceScanPreviewView.surfaceProvider)
        } catch (exc: Exception) {

        }
    }
    private fun detectObjects(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection
        objectDetectorHelper.detect(bitmapBuffer, imageRotation)
    }
    private fun getOutputDirectory(): File {
        val mediaDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            externalMediaDirs.firstOrNull()?.let {
                File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
            }
        } else {
            getExternalFilesDir(null)?.let {
                File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
            }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }
    fun createFile(baseFolder: File, format: String, extension: String) =
        File(
            baseFolder, SimpleDateFormat(format, Locale.US)
                .format(System.currentTimeMillis()) + extension
        )
}

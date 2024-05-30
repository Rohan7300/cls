package com.clebs.celerity.utils.fragments

import ObjectDetectorHelper
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentCameraBinding
import com.clebs.celerity.fragments.DailyWorkFragment
import com.clebs.celerity.ui.AddInspection
import com.clebs.celerity.utils.ClsCapture
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.insLevel

import com.clebs.celerity.utils.bitmapToBase64
import com.clebs.celerity.utils.invisible
import com.clebs.celerity.utils.scaleBitmapToWidth
import com.clebs.celerity.utils.showToast
import kotlinx.coroutines.runBlocking

import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import org.tensorflow.lite.task.vision.detector.Detection
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class CameraFragment : Fragment(), ObjectDetectorHelper.DetectorListener {

    private val TAG = "ObjectDetection"

    private var _fragmentCameraBinding: FragmentCameraBinding? = null

    private lateinit var fragmentCameraBinding: FragmentCameraBinding
    private var imageCapture: ImageCapture? = null


    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private lateinit var bitmapBuffer: Bitmap
    var bitmapBuffer2: Bitmap? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    /** Blocking camera operations are performed using this executor */
    private lateinit var cameraExecutor: ExecutorService

    override fun onResume() {
        super.onResume()
        // Make sure that all permissions are still present, since the
        // user could have removed them while the app was in paused state.
        if (!PermissionsFragment.hasPermissions(requireContext())) {
            Navigation.findNavController(requireActivity(), R.id.fragment_container)
                .navigate(R.id.permissions_fragment)
        }
    }

    override fun onDestroyView() {
        _fragmentCameraBinding = null
        super.onDestroyView()

        // Shut down our background executor
        cameraExecutor.shutdown()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentCameraBinding = FragmentCameraBinding.inflate(inflater, container, false)
        fragmentCameraBinding = _fragmentCameraBinding!!
        imageCapture = ImageCapture.Builder().build()
      when(insLevel){
          0->{
              fragmentCameraBinding.focusTxt.text= "Focus Camera on Vehicle Dashboard"
              fragmentCameraBinding.dashboardStatusIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ins_dashboard))
          }
          1->{
              fragmentCameraBinding.focusTxt.text= "Focus Camera on Vehicle Front"
              fragmentCameraBinding.dashboardStatusIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ins_front))
          }
          2->{
              fragmentCameraBinding.focusTxt.text= "Focus Camera on Vehicle Near Side"
              fragmentCameraBinding.dashboardStatusIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_near_two))
          }
          3->{
              fragmentCameraBinding.focusTxt.text= "Focus Camera on Vehicle Rear Side"
              fragmentCameraBinding.dashboardStatusIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_rear))
          }
          4->{
                  fragmentCameraBinding.focusTxt.text= "Focus Camera on Vehicle Offside"
                  fragmentCameraBinding.dashboardStatusIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ic_offtwo))
          }
          5->{
              fragmentCameraBinding.focusTxt.text= "Focus Camera on Add Blue Level Meter"
              fragmentCameraBinding.dashboardStatusIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ins_addblue))
          }
          6->{
              fragmentCameraBinding.focusTxt.text= "Focus Camera on Oil Level Meter"
              fragmentCameraBinding.dashboardStatusIV.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ins_oillevel))
          }
      }
        return fragmentCameraBinding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        objectDetectorHelper = ObjectDetectorHelper(
            context = requireContext(),
            objectDetectorListener = this
        )

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        // Wait for the views to be properly laid out
        fragmentCameraBinding.viewFinder.post {
            // Set up the camera and its use cases
            setUpCamera()
        }


        fragmentCameraBinding.imgCamera.setOnClickListener {
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
                        Log.d(ContentValues.TAG, "Photo capture failed")
                        println("Photo capture failed")
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        Log.d(ContentValues.TAG, "Photo capture succeeded processing photo")

                        showToast("Photo capture succeeded processing photo", requireContext())
                        bitmapBuffer2 =
                            getImageBitmapFromUri(requireContext(), output.savedUri!!)


                        if (bitmapBuffer2 != null) {
                            Log.e(TAG, "onImageSaved-==============: " + bitmapBuffer2)
                            //currentimagebase64=     bitmapToBase64(bitmapBuffer2!!)
                            var scaleBitmap = scaleBitmapToWidth(bitmapBuffer2!!,1024)
                            (activity as ClsCapture).passBitmap(output.savedUri!!)
                        }

//                    if (imageBitmap != null) {
//
//                        detectTxt()
//                    }

//                    mbinding.rectangle4.setImageBitmap(imageBitmap)
                    }
                }
            )
        }


    }

//    private fun initBottomSheetControls() {
//        // When clicked, lower detection score threshold floor
//        fragmentCameraBinding.bottomSheetLayout.thresholdMinus.setOnClickListener {
//            if (objectDetectorHelper.threshold >= 0.1) {
//                objectDetectorHelper.threshold -= 0.1f
//                updateControlsUi()
//            }
//        }
//
//        // When clicked, raise detection score threshold floor
//        fragmentCameraBinding.bottomSheetLayout.thresholdPlus.setOnClickListener {
//            if (objectDetectorHelper.threshold <= 0.8) {
//                objectDetectorHelper.threshold += 0.1f
//                updateControlsUi()
//            }
//        }
//
//        // When clicked, reduce the number of objects that can be detected at a time
//        fragmentCameraBinding.bottomSheetLayout.maxResultsMinus.setOnClickListener {
//            if (objectDetectorHelper.maxResults > 1) {
//                objectDetectorHelper.maxResults--
//                updateControlsUi()
//            }
//        }
//
//        // When clicked, increase the number of objects that can be detected at a time
//        fragmentCameraBinding.bottomSheetLayout.maxResultsPlus.setOnClickListener {
//            if (objectDetectorHelper.maxResults < 5) {
//                objectDetectorHelper.maxResults++
//                updateControlsUi()
//            }
//        }
//
//        // When clicked, decrease the number of threads used for detection
//        fragmentCameraBinding.bottomSheetLayout.threadsMinus.setOnClickListener {
//            if (objectDetectorHelper.numThreads > 1) {
//                objectDetectorHelper.numThreads--
//                updateControlsUi()
//            }
//        }
//
//        // When clicked, increase the number of threads used for detection
//        fragmentCameraBinding.bottomSheetLayout.threadsPlus.setOnClickListener {
//            if (objectDetectorHelper.numThreads < 4) {
//                objectDetectorHelper.numThreads++
//                updateControlsUi()
//            }
//        }
//
//        // When clicked, change the underlying hardware used for inference. Current options are CPU
//        // GPU, and NNAPI
//        fragmentCameraBinding.bottomSheetLayout.spinnerDelegate.setSelection(0, false)
//        fragmentCameraBinding.bottomSheetLayout.spinnerDelegate.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                    objectDetectorHelper.currentDelegate = p2
//                    updateControlsUi()
//                }
//
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//                    /* no op */
//                }
//            }
//
//        // When clicked, change the underlying model used for object detection
//        fragmentCameraBinding.bottomSheetLayout.spinnerModel.setSelection(0, false)
//        fragmentCameraBinding.bottomSheetLayout.spinnerModel.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                    objectDetectorHelper.currentModel = p2
//                    updateControlsUi()
//                }
//
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//                    /* no op */
//                }
//            }
//    }

    // Update the values displayed in the bottom sheet. Reset detector.
//    private fun updateControlsUi() {
//        fragmentCameraBinding.bottomSheetLayout.maxResultsValue.text =
//            objectDetectorHelper.maxResults.toString()
//        fragmentCameraBinding.bottomSheetLayout.thresholdValue.text =
//            String.format("%.2f", objectDetectorHelper.threshold)
//        fragmentCameraBinding.bottomSheetLayout.threadsValue.text =
//            objectDetectorHelper.numThreads.toString()
//
//        // Needs to be cleared instead of reinitialized because the GPU
//        // delegate needs to be initialized on the thread using it when applicable
//        objectDetectorHelper.clearObjectDetector()
//        fragmentCameraBinding.overlay.clear()
//    }

    // Initialize CameraX, and prepare to bind the camera use cases
    private fun setUpCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(
            {
                // CameraProvider
                cameraProvider = cameraProviderFuture.get()

                // Build and bind the camera use cases
                bindCameraUseCases()
            },
            ContextCompat.getMainExecutor(requireContext())
        )
    }

    // Declare and bind preview, capture and analysis use cases
    @SuppressLint("UnsafeOptInUsageError")
    private fun bindCameraUseCases() {

        // CameraProvider
        val cameraProvider =
            cameraProvider ?: throw IllegalStateException("Camera initialization failed.")

        // CameraSelector - makes assumption that we're only using the back camera
        val cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview. Only using the 4:3 ratio because this is the closest to our models
        preview =
            Preview.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .build()

        // ImageAnalysis. Using RGBA 8888 to match how our models work
        imageAnalyzer =
            ImageAnalysis.Builder()
                .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                .setTargetRotation(fragmentCameraBinding.viewFinder.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(OUTPUT_IMAGE_FORMAT_RGBA_8888)
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
            preview?.setSurfaceProvider(fragmentCameraBinding.viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    private fun detectObjects(image: ImageProxy) {
        // Copy out RGB bits to the shared bitmap buffer
        image.use { bitmapBuffer.copyPixelsFromBuffer(image.planes[0].buffer) }

        val imageRotation = image.imageInfo.rotationDegrees
        // Pass Bitmap and rotation to the object detector helper for processing and detection
        objectDetectorHelper.detect(bitmapBuffer, imageRotation)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        imageAnalyzer?.targetRotation = fragmentCameraBinding.viewFinder.display.rotation
    }

    // Update UI after objects have been detected. Extracts original image height/width
    // to scale and place bounding boxes properly through OverlayView
    override fun onResults(
        results: MutableList<Detection>?,
        inferenceTime: Long,
        imageHeight: Int,
        imageWidth: Int
    ) {


        activity?.runOnUiThread {

            //if result has detection equals car


            val resultss = results ?: LinkedList<Detection>()
            //if result has detection equals car
            val containsCar = resultss.any { detection ->
                detection.categories[0].label.equals("car")


            }
            val score = resultss.any { detection ->

                String.format("%.2f", detection.categories[0].score) >= 0.60.toString()
            }

            val containstruck = resultss.any { detection ->

                detection.categories[0].label.equals("truck")
            }
            if (results != null && _fragmentCameraBinding != null) {
                if (containsCar || containstruck && score) {

                    Log.e(TAG, "onResults===========bitmap: " + bitmapBuffer2)
                    fragmentCameraBinding.imgCamera.visibility = View.VISIBLE

                    fragmentCameraBinding.alert.visibility = View.GONE
                } else {
                    fragmentCameraBinding.imgCamera.visibility = View.GONE
                    fragmentCameraBinding.alert.visibility = View.VISIBLE
                }
            }

            fragmentCameraBinding.overlay.setResults(
                results ?: LinkedList<Detection>(),
                imageHeight,
                imageWidth
            )

            fragmentCameraBinding.overlay.invalidate()
//            val detectionList: MutableList<Detection> = results ?: ArrayList()
//
//            val containsCar = detectionList.any { detection ->
//                detection.equals("car")
//            }
//
//            if (containsCar) {
//                _fragmentCameraBinding?.relativeLayout3!!.visibility=View.VISIBLE
//                // "car" is present in the list
//                // Perform your desired actions here
//            } else {
//                _fragmentCameraBinding?.relativeLayout3!!.visibility=View.GONE
//                // "car" is not present in the list
//            }
//            fragmentCameraBinding.bottomSheetLayout.inferenceTimeVal.text =
//                            String.format("%d ms", inferenceTime)

            // Pass necessary information to OverlayView for drawing on the canvas


//            fragmentCameraBinding.overlay.setResults(
//                results ?: LinkedList<Detection>(),
//                imageHeight,
//                imageWidth
//            )

            // Force a redraw

        }
    }

    override fun onError(error: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
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

    override fun onDestroy() {
        super.onDestroy()
        objectDetectorHelper.clearObjectDetector()
    }

}

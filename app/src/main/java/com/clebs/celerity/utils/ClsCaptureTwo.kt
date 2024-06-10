package com.clebs.celerity.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityClsCaptureTwoBinding
import com.clebs.celerity.databinding.FragmentDailyWorkBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.fragments.DailyWorkFragment
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ClsCaptureTwo : AppCompatActivity() {
    lateinit var mbinding: ActivityClsCaptureTwoBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var loadingDialog: LoadingDialog
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    companion object {

        private val REQUIRED_PERMISSIONS =
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

        const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_cls_capture_two)
        setContentView(mbinding.root)
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (DependencyProvider.insLevel.equals(0)) {
            mbinding.dashboardStatusIV.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ins_dashboard
                )
            )
        } else if (DependencyProvider.insLevel.equals(5)) {
            mbinding.dashboardStatusIV.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ins_addblue
                )
            )
        } else if (DependencyProvider.insLevel.equals(6)) {
            mbinding.dashboardStatusIV.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ins_oillevel
                )
            )
        }
        else{
            mbinding.dashboardStatusIV.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ins_dashboard
                )
            )
        }
        startCamera()
        if (allPermissionsGranted()) {
            startCamera()
            initListeners()
        } else {
            requestpermissions()
        }

    }

    @SuppressLint("NewApi")
    fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(mbinding.inspectionPreviewView.surfaceProvider)
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
                Log.d(ContentValues.TAG, "Use case binding failed ${e.message}")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @SuppressLint("NewApi")
    private fun takePhoto() = try {
//        mbinding.pb.visibility = View.VISIBLE

//        mbinding.imageView5.visibility = View.GONE


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
                    this.contentResolver,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ).build()
        else
            ImageCapture.OutputFileOptions
                .Builder(
                    this.contentResolver,
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    contentValues
                ).build()
        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.d(ContentValues.TAG, "Photo capture failed")
                    println("Photo capture failed clscap2 exc: $exc \n ${exc.localizedMessage}")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Log.d(ContentValues.TAG, "Photo capture succeeded processing photo")

                    showToast("Photo capture succeeded processing photo", this@ClsCaptureTwo)
                    val uri = output.savedUri
                    if (uri != null) {
                        passBitmap(uri)
                    }


//                    imageBitmap = getImageBitmapFromUri(requireContext(), output.savedUri!!)
//
//                    uploadImageToServerAndGetResults(output.savedUri)

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
        Log.d(ContentValues.TAG, "Photo capture failed: ${e.message}")
        println("Photo capture failed: ${e.message}")
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in ClsCaptureTwo.REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast("Permission denied", this)
            } else {

                startCamera()
                initListeners()
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

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initListeners() = try {
        mbinding.ivTakePhoto.setOnClickListener {
            takePhoto()
        }

    } catch (e: Exception) {
        Log.d(ContentValues.TAG, "Photo capture failed: ${e.message}")
    }

    fun passBitmap(crrURI: Uri) {
        DependencyProvider.isComingBackFromCLSCapture = true
        DependencyProvider.currentUri = crrURI
        Log.e("skdhhsjdfhfdh", "passBitmap: " + DependencyProvider.currentUri)
        finish()
    }
}
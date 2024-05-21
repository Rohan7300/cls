package com.clebs.celerity.ui

import android.content.ContentValues
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.clebs.celerity.R
import com.clebs.celerity.databinding.ActivityFaceScanBinding
import com.clebs.celerity.fragments.DailyWorkFragment
import com.clebs.celerity.utils.DependencyProvider.currentUri
import com.clebs.celerity.utils.DependencyProvider.isComingBackFromFaceScan
import com.clebs.celerity.utils.showToast
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class FaceScanActivity : AppCompatActivity() {
    lateinit var binding: ActivityFaceScanBinding
    private var imageCapture: ImageCapture? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_face_scan)
        initCamera()
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
                else
                    ImageCapture.OutputFileOptions
                        .Builder(
                            contentResolver,
                            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                            contentValues
                        ).build()
                imageCapture.takePicture(
                    outputOptions, ContextCompat.getMainExecutor(this),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(exc: ImageCaptureException) {
                            Log.d(ContentValues.TAG, "Photo capture failed")
                        }
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            showToast("Photo capture succeeded processing photo", this@FaceScanActivity)
                            currentUri = output.savedUri
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

    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.faceScanPreviewView.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            PreviewView.ImplementationMode.COMPATIBLE
            val cameraSelector =
                CameraSelector.DEFAULT_FRONT_CAMERA
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
}
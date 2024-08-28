
package com.clebs.celerity_admin.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.databinding.ActivityImageCaptureBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageCaptureActivity : AppCompatActivity() {
    lateinit var binding: ActivityImageCaptureBinding
    private lateinit var cameraExecutor: ExecutorService
    lateinit var loadingDialog: LoadingDialog
    private var imageCapture: ImageCapture? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageCapture = ImageCapture.Builder().build()
        loadingDialog = LoadingDialog(this)
        cameraExecutor = Executors.newSingleThreadExecutor()
        initCamera()
        binding.captureIV.setOnClickListener {
            loadingDialog.show()
            takePhoto()
        }
    }

    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
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


    @SuppressLint("NewApi")
    private fun takePhoto() = try {
        val imageCapture = imageCapture ?: throw IOException("Camera not connected")
        val name = SimpleDateFormat(FileNameFormat, Locale.US)
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
                    loadingDialog.dismiss()
                    Log.d(ContentValues.TAG, "Photo capture failed")
                    println("Photo capture failed ita ex: $exc")
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    loadingDialog.dismiss()
                    val resultIntent = Intent()
                    resultIntent.putExtra("outputUri", output.savedUri.toString())
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        )
    } catch (e: Exception) {
        println("Photo capture failed: ${e.message}")
    }

    companion object {
        private const val FileNameFormat = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
}
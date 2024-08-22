package com.clebs.celerity_admin.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.AdapterViewFlipper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity_admin.adapters.AddFilesAdapter
import com.clebs.celerity_admin.databinding.ActivityVanHireReturnAgreementBinding
import com.clebs.celerity_admin.utils.SignatureDialog
import com.clebs.celerity_admin.utils.SignatureDialogListener
import com.clebs.celerity_admin.utils.getMimeType
import com.clebs.celerity_admin.utils.showDatePickerDialog
import com.clebs.celerity_admin.utils.showToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class VanHireReturnAgreementActivity : AppCompatActivity() {
    companion object {
        var path = Path()
        var brush = Paint()
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }.toTypedArray()
    }

    lateinit var binding: ActivityVanHireReturnAgreementBinding
    private var selectedFileUri: MutableList<Uri>? = mutableListOf()
    val adapter = AddFilesAdapter( mutableListOf())
    private lateinit var filePart: MultipartBody.Part
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVanHireReturnAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        clickListeners()
        binding.tilConvictions.visibility = View.GONE
        binding.layoutUploadVehicleAccidentImages.visibility = View.GONE

        binding.vehicleAccidentalImageLayout.fileListRV.adapter = adapter
        binding.vehicleAccidentalImageLayout.fileListRV.layoutManager = LinearLayoutManager(this)
    }

    private fun clickListeners() {
        binding.atvDOB.setOnClickListener {
            showDatePickerDialog(this, binding.atvDOB)
        }
        binding.atvDOB.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.atvDOB.performClick()
            }
        }
        binding.aTvLicenseStartDate.setOnClickListener {
            showDatePickerDialog(this, binding.aTvLicenseStartDate)
        }
        binding.aTvLicenseStartDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.aTvLicenseStartDate.performClick()
        }
        binding.aTvLicenseEndDate.setOnClickListener {
            showDatePickerDialog(this, binding.aTvLicenseEndDate)
        }
        binding.aTvLicenseEndDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding.aTvLicenseEndDate.performClick()
        }
        binding.checkboxConvictions.setOnClickListener {
            if (binding.checkboxConvictions.isChecked) {
                binding.tilConvictions.visibility = View.VISIBLE
            } else {
                binding.tilConvictions.visibility = View.GONE
            }
        }
        binding.checkboxUploadVehicleDefectImages.setOnClickListener {
            if (binding.checkboxUploadVehicleDefectImages.isChecked)
                binding.layoutUploadVehicleAccidentImages.visibility = View.VISIBLE
            else
                binding.layoutUploadVehicleAccidentImages.visibility = View.GONE
        }
        binding.checkBoxProceed.setOnClickListener {
            if (binding.checkBoxProceed.isChecked)
                showSignatureDialog()
        }
        binding.vehicleAccidentalImageLayout.browseBtn.setOnClickListener {
            browseFiles()
        }
    }

    private fun browseFiles() {
        if (allPermissionsGranted()) {
            upload()
        } else {
            requestPermissions()
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    selectedFileUri!!.add(it)
                    adapter.data = selectedFileUri
                    adapter.notifyItemInserted(selectedFileUri.size)
                    val mimeType = getMimeType(it)?.toMediaTypeOrNull()
                    val tmpFile = createTempFile("temp", null, cacheDir).apply {
                        deleteOnExit()
                    }

                    val inputStream = contentResolver.openInputStream(it)
                    val outputStream = tmpFile.outputStream()

                    inputStream?.use { input ->
                        outputStream.use { output ->
                            input.copyTo(output)
                        }
                    }

                    val fileExtension = getMimeType(it)?.let { mimeType ->
                        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                    }

                    val requestBody = tmpFile.asRequestBody(mimeType)
                    filePart = MultipartBody.Part.createFormData(
                        "UploadTicketDoc",
                        it.lastPathSegment + "." + (fileExtension ?: "jpg"),
                        requestBody
                    )

                    /*                        showDialog()
                                            uploadWithAttachement = true
                                            saveTicket()*/
                }
            } else {
                finish()
                showToast("Attachment not selected!!", this)
            }
        }

    fun upload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        resultLauncher.launch(intent)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast("Permission denied", this)
            } else {
                upload()
            }
        }

    private fun showSignatureDialog() {
        val dialog = SignatureDialog()
        dialog.setSignatureListener(object : SignatureDialogListener {
            override fun onSignatureSaved(bitmap: Bitmap) {

            }
        })
        dialog.show(supportFragmentManager, "sign")
    }
}
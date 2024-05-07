package com.clebs.celerity.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.webkit.MimeTypeMap
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.ExpiringDocAdapter
import com.clebs.celerity.adapters.ExpiringDocUploadListener
import com.clebs.celerity.databinding.ActivityExpiringDocumentsBinding
import com.clebs.celerity.databinding.UploadexpiringdocdialogBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getMimeType
import com.clebs.celerity.utils.showToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class ExpiringDocumentsActivity : AppCompatActivity(), ExpiringDocUploadListener {
    lateinit var viewmodel: MainViewModel
    private lateinit var repo: MainRepo
    lateinit var pref: Prefs
    lateinit var binding: ActivityExpiringDocumentsBinding
    lateinit var loadingDialog: LoadingDialog
    private var selectedFileUri: Uri? = null
    lateinit var filePart: MultipartBody.Part
    var documentID:Int = -1

    companion object{
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_expiring_documents)
        setContentView(binding.root)

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        repo = MainRepo(apiService)
        pref = Prefs(this)
        loadingDialog = LoadingDialog(this)
        viewmodel = ViewModelProvider(this, MyViewModelFactory(repo))[MainViewModel::class.java]
        val adapter = ExpiringDocAdapter(this@ExpiringDocumentsActivity)
        binding.expringDocRV.adapter = adapter
        binding.expringDocRV.layoutManager = LinearLayoutManager(this)
        loadingDialog.show()
        viewmodel.GetDAExpiringDocuments(pref.clebUserId.toInt())
        viewmodel.liveDataGetDAExpiringDocuments.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                adapter.saveData(it)
            }
        }
        binding.expringDocSave.isEnabled = false
        binding.expringDocSave.setOnClickListener {

        }
        binding.expringDocCancel.setOnClickListener {
            onBackPressed()
        }

    }
    override fun uploadIntent(documentTypeID: Int) {
        documentID = documentTypeID
        if (allPermissionsGranted()) {
            upload()
        } else {
            requestPermissions()
        }
    }

    fun upload() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"
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
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast("Permission denied", this)
            } else {
                upload()
            }
        }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    selectedFileUri = it
                    if (selectedFileUri == null) {
                        showToast("Something went wrong!!", this)
                    } else {
                        val mimeType = getMimeType(selectedFileUri!!)?.toMediaTypeOrNull()
                        val tmpFile = createTempFile("temp", null, cacheDir).apply {
                            deleteOnExit()
                        }

                        val inputStream = contentResolver.openInputStream(selectedFileUri!!)
                        val outputStream = tmpFile.outputStream()

                        inputStream?.use { input ->
                            outputStream.use { output ->
                                input.copyTo(output)
                            }
                        }

                        val fileExtension = getMimeType(selectedFileUri!!)?.let { mimeType ->
                            MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                        }

                        val requestBody = tmpFile.asRequestBody(mimeType)
                        filePart = MultipartBody.Part.createFormData(
                            "uploadUserDocument",
                            selectedFileUri!!.lastPathSegment + "." + (fileExtension ?: "jpg"),
                            requestBody
                        )

                        //loadingDialog.show()
                        if(documentID!=-1)
                        showUploadDialog()
                        else
                            showToast("Failed to fetch Document ID",this)
                    }

                }
            } else {
                showToast("Attachment not selected!!", this)
            }
        }

    private fun showUploadDialog() {
        val uploadDialog = AlertDialog.Builder(this).create()
        val uploadDialogBinding = UploadexpiringdocdialogBinding.inflate(LayoutInflater.from(this))
        uploadDialog.setView(uploadDialogBinding.root)
        uploadDialog.setCanceledOnTouchOutside(false)
        uploadDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        uploadDialog.show()

        uploadDialogBinding.upload.setOnClickListener {
            uploadDialog.dismiss()
            uploadDialog.cancel()
            uploadDoc()
        }


        uploadDialogBinding.cancel.setOnClickListener {
            uploadDialog.dismiss()
            uploadDialog.cancel()
        }
    }

    fun uploadDoc(){
        loadingDialog.show()
        viewmodel.UploadExpiringDocs(pref.clebUserId.toInt(),documentID,filePart)
        viewmodel.liveDataUploadExpiringDocs.observe(this){
            if(it!=null){
                loadingDialog.show()
                viewmodel.GetDAExpiringDocuments(pref.clebUserId.toInt())
                showToast("Document Uploaded succesfully!!",this)
            }else{
                showToast("Something went wron!!",this)
            }
        }
    }
}
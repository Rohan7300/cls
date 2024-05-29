package com.clebs.celerity.ui

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.DetailCommentAdapter
import com.clebs.celerity.databinding.ActivityAddCommentBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class AddCommentActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddCommentBinding
    lateinit var viewModel: MainViewModel
    lateinit var loadingDialog: LoadingDialog
    private var ticketID: Int? = null
    lateinit var prefs: Prefs
    private var commentID: Int? = null
    private var uploadCommentAttachment: Boolean = false
    private var selectedFileUri: Uri? = null
    private var filePart: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_comment)
        setContentView(binding.root)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        viewModel = ViewModelProvider(
            this,
            MyViewModelFactory(MainRepo(apiService))
        )[MainViewModel::class.java]
        prefs = Prefs.getInstance(this)
        ticketID = intent.getIntExtra("ticketID", -1)
        loadingDialog = LoadingDialog(this)
        observer()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                try {
                    finish()
                    DependencyProvider.comingFromViewTickets = true
                } catch (_: Exception) {
                }
            }
        })
        binding.addImage.setOnClickListener {
            uploadCommentAttachment = true
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }

        binding.imageViewBack.setOnClickListener {
            finish()
            DependencyProvider.comingFromViewTickets = true
        }

        loadingDialog.show()
        viewModel.GetTicketCommentList(prefs.clebUserId.toInt(), ticketID!!)
        binding.submitAddComment.setOnClickListener {
            if (!binding.commentET.text.isNullOrEmpty()) {
                loadingDialog.show()
                viewModel.SaveTicketComment(
                    prefs.clebUserId.toInt(),
                    ticketID!!,
                    binding.commentET.text.toString()
                )
            } else {
                showToast("Please add comment first", this)
            }
        }

    }

    private fun observer() {
        viewModel.liveDataSaveTicketComment.observe(this) {
            loadingDialog.cancel()
            if (it != null) {
                binding.commentET.setText("")
                loadingDialog.show()
                if (uploadCommentAttachment) {
                    commentID = it.CommentId.toInt()
                    uploadAttachment()
                }else{
                    viewModel.GetTicketCommentList(prefs.clebUserId.toInt(), ticketID!!)
                }
            }
        }

        viewModel.liveDataUploadTicketCommentAttachmentDoc.observe(this){
            viewModel.GetTicketCommentList(prefs.clebUserId.toInt(), ticketID!!)
            if(it!=null){
                binding.addImage.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.gallery))
                ticketID = null
                selectedFileUri = null
                commentID = null
                uploadCommentAttachment = false
            }
        }

        val commentAdapter = DetailCommentAdapter(arrayListOf(), this)
        binding.commentRv.adapter = commentAdapter
        binding.commentRv.layoutManager = LinearLayoutManager(this)

        viewModel.liveDataGetTicketCommentList.observe(this) {
            loadingDialog.cancel()
            if (it != null) {
                if (it.Docs.isNotEmpty()) {
                    binding.commentRv.visibility = View.VISIBLE
                    binding.noCommentLayout.visibility = View.GONE
                }
                val reversedList = it.Docs
                commentAdapter.arrayList.clear()
                commentAdapter.arrayList.addAll(reversedList)
                commentAdapter.notifyDataSetChanged()
            } else {
                binding.commentRv.visibility = View.GONE
                binding.noCommentLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun uploadAttachment() {
        if (filePart != null && commentID != null) {
            viewModel.UploadTicketCommentAttachmentDoc(
                prefs.clebUserId.toInt(),
                commentID!!,
                filePart!!
            )
        } else {
            showToast("Failed to upload attachment.", this)
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                Log.d("XX", "$result")
                data?.data?.let {
                    selectedFileUri = it
                    if (selectedFileUri == null) {
                        showToast("Something went wrong!!", this)
                    } else {
                        val inputStreamX = contentResolver.openInputStream(selectedFileUri!!)
                        val bitmap = BitmapFactory.decodeStream(inputStreamX)
                        binding.addImage.setImageBitmap(bitmap)
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
                            "uploadTicketCommentDoc",
                            selectedFileUri!!.lastPathSegment + "." + (fileExtension ?: "jpg"),
                            requestBody
                        )
                    }
                }
            } else {
                Log.d("Error", "")
            }
        }

    private fun getMimeType(uri: Uri): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}
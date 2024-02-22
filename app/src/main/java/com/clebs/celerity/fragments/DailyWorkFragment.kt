package com.clebs.celerity.fragments


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ais.plate_req_api.webService.ApiServiceTwo
import com.ais.plate_req_api.webService.RetrofitHelper
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
import com.clebs.celerity.utils.getFileFromUri
import com.clebs.celerity.utils.navigateTo
import com.kotlinpermissions.KotlinPermissions
import id.zelory.compressor.Compressor
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody


import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


/**
 * A simple [Fragment] subclass.
 * Use the [DailyWorkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DailyWorkFragment : Fragment() {
    lateinit var mbinding: FragmentDailyWorkBinding
    lateinit var mainViewModel: MainViewModel
    private val API_TOKEN = "9d04d01d5ba1997289fa28f6f544b16ab9e5a8b6"

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    var isFrontCamera = false

    var vrn: String = ""
    var countryCode: String = ""
    var vehicleType: String = ""
    var score: String = ""
    var bounding: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()

//        val SDK_INT = Build.VERSION.SDK_INT
//        if (SDK_INT > 8) {
//            val policy = ThreadPolicy.Builder().permitAll().build()
//            StrictMode.setThreadPolicy(policy)
//            //your codes here
//        }

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
//            checkPermissions()
            navigateTo(R.id.vechileMileageFragment,requireContext(),findNavController())

        }



        return mbinding.root
    }

    private fun checkPermissions() {
        // Request camera permissions
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


    @SuppressLint("NewApi")
    private fun takePhoto() = try {
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
                    Toast.makeText(
                        requireContext(),
                        "Photo capture succeeded processing photo",
                        Toast.LENGTH_SHORT
                    ).show()
                    mbinding.rectange.visibility = View.GONE
                    mbinding.rectangle4.visibility = View.VISIBLE
                    mbinding.imageView5.visibility = View.VISIBLE
                    mbinding.ivTakePhoto.visibility = View.GONE
                    uploadImageToServerAndGetResults(output.savedUri)
                }
            }
        )
    } catch (e: Exception) {
        Log.d(TAG, "Photo capture failed: ${e.message}")
        println("Photo capture failed: ${e.message}")
    }

    private fun uploadImageToServerAndGetResults(savedUri: Uri?) {
        if (savedUri != null) {
            mbinding.pb.visibility = View.VISIBLE
            val apiService: ApiServiceTwo =
                RetrofitHelper.getInstance().create(ApiServiceTwo::class.java)
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
                    Log.e(TAG, "uploadImageToServerAndGetResultssuccess: " + response)
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
                            if (vrn.isNotEmpty()) {
                                getVichleinformation()
                                Log.e(TAG, "uploadImageToServerAndGetResultssuccess: " + vrn)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Registration number not found in image",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Registration number not found in image",
                                Toast.LENGTH_SHORT
                            ).show()
                            mbinding.pb.visibility = View.GONE

                        }
                    }
                }
                Log.e(
                    TAG,
                    "uploadImageToServerAndGetResultsfailue: " + response.errorBody() + response.message()
                )
            }
        }
    }


    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }


    fun getVichleinformation() {
        mbinding.pb.visibility = View.GONE
        mainViewModel.getVichelinformationResponse(
            Prefs.getInstance(App.instance).userID.toString().toDouble(), 0.toDouble(), vrn
        ).observe(requireActivity(), Observer {
            if (it != null) {
                Prefs.getInstance(App.instance)
                    .save("vehicleLastMillage", it.vehicleLastMillage.toString())
                showLog(
                    "TAG------->",
                    "mymileage" + it.vehicleLastMillage.toString() + Prefs.getInstance(App.instance)
                        .get("vehicleLastMillage")
                )
                showAlert()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Vehicle doesn't exist's. Please contact your supervisor.",
                    Toast.LENGTH_SHORT
                ).show()
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
                Toast.makeText(
                    requireActivity(), "Please check the acknowledgment check", Toast.LENGTH_SHORT
                ).show()
            }
        }

        deleteDialog.setView(view)
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();

    }


}
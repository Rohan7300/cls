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
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.AddFilesAdapter
import com.clebs.celerity_admin.adapters.DeleteCallback
import com.clebs.celerity_admin.adapters.RequestTypeListAdapter
import com.clebs.celerity_admin.databinding.ActivityVanHireCollectionBinding
import com.clebs.celerity_admin.databinding.ActivityVanHireReturnAgreementBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.CollectVehicleFromSupplierRequest
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponseItem
import com.clebs.celerity_admin.models.ReturnVehicleToDepoRequest
import com.clebs.celerity_admin.models.VehicleInfoXXXX
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DependencyClass
import com.clebs.celerity_admin.utils.DependencyClass.addBlueMileage
import com.clebs.celerity_admin.utils.DependencyClass.collectionSelectedVehicleFuelId
import com.clebs.celerity_admin.utils.DependencyClass.collectionSelectedVehicleLocId
import com.clebs.celerity_admin.utils.DependencyClass.collectionSelectedVehicleOilLevelListId
import com.clebs.celerity_admin.utils.DependencyClass.crrMileage
import com.clebs.celerity_admin.utils.DependencyClass.currentImageUploadCode
import com.clebs.celerity_admin.utils.DependencyClass.requestTypeList
import com.clebs.celerity_admin.utils.DependencyClass.selectedCompanyId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleFuelId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleLocId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleOilLevelListId
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.SignatureDialog
import com.clebs.celerity_admin.utils.SignatureDialogListener
import com.clebs.celerity_admin.utils.bitmapToBase64
import com.clebs.celerity_admin.utils.createBackgroundUploadRequest
import com.clebs.celerity_admin.utils.getFileUri
import com.clebs.celerity_admin.utils.showDatePickerDialog
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.viewModels.MainViewModel
import okhttp3.MultipartBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class VanHireCollectionActivity : AppCompatActivity(), DeleteCallback {
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

    lateinit var binding: ActivityVanHireCollectionBinding
    private var selectedFileUri: MutableList<String>? = mutableListOf()
    private var selectedFileUriAccident: MutableList<String>? = mutableListOf()
    lateinit var prefs: Prefs
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mainViewModel: MainViewModel
    private lateinit var vehicleInfoXXXX: VehicleInfoXXXX
    val adapter = AddFilesAdapter(mutableListOf())
    val accidentAdapter = AddFilesAdapter(mutableListOf())
    private lateinit var filePart: MultipartBody.Part
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVanHireCollectionBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(this)

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        window.statusBarColor = resources.getColor(R.color.commentbg, null)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        setContentView(binding.root)
        clickListeners()
        prefs = Prefs.getInstance(this)
        prefs.accidentImagePos = 0

        binding.layoutSupplierImages.supplierdocRV.adapter = adapter
        binding.layoutSupplierImages.supplierdocRV.layoutManager = LinearLayoutManager(this)

        binding.layoutSupplierImages.accidentImageRV.adapter = accidentAdapter
        binding.layoutSupplierImages.accidentImageRV.layoutManager = LinearLayoutManager(this)

        observers()
    }

    private fun observers() {

    }

    private fun clickListeners() {
        binding.back.setOnClickListener {
            finish()
        }
        binding.layoutSupplierImages.checkboxUploadAccidentImages.setOnClickListener {
            if (binding.layoutSupplierImages.checkboxUploadAccidentImages.isChecked)
                binding.layoutSupplierImages.accidentImageSection.visibility = View.VISIBLE
            else
                binding.layoutSupplierImages.accidentImageSection.visibility = View.GONE
        }
        binding.layoutSupplierImages.uploadSupplierDocsBtn.setOnClickListener {
            currentImageUploadCode = 5
            if (!prefs.isSupplierDocsUploading)
                browseFiles()
            else
                showToast("Please wait other images are uploading!!", this)
        }
        binding.layoutSupplierImages.saveVehicleInfoBtn.setOnClickListener {

        }
        binding.layoutSupplierImages.uploadAccidentImagesBtn.setOnClickListener {
            currentImageUploadCode = 6
            if (!prefs.isCollectionAccidentDocsUploading)
                browseFiles()
            else
                showToast("Please wait other images are uploading!!", this)
        }
    }

    private fun browseFiles() {
        if (allPermissionsGranted()) {
            upload()
        } else {
            requestPermissions()
        }
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

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    fun upload() {
        val imageTakerActivityIntent = Intent(this, ImageCaptureActivity::class.java)
        resultLauncher.launch(imageTakerActivityIntent)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showSignatureDialog() {
        val dialog = SignatureDialog()
        dialog.setSignatureListener(object : SignatureDialogListener {
            override fun onSignatureSaved(bitmap: Bitmap) {
                if (true) {
                    var vehType = ""
                    if (DependencyClass.crrSelectedVehicleType!!.contains("SMALL VAN")) {
                        vehType = "SV"
                    } else if (DependencyClass.crrSelectedVehicleType!!.contains("LARGE VAN")) {
                        vehType = "LV"
                    }
                    val bse64 = "data:image/png;base64," + bitmapToBase64(bitmap)
                    /* mainViewModel.ReturnVehicleToDepo(
                         ReturnVehicleToDepoRequest(
                             AccidentsChk = binding.checkBoxTickToConfirmAccidents.isChecked,
                             AccidentsChk2 = false,
                             AccidentsChk3 = false,
                             AddBlueMileage = addBlueMileage!!,
                             ChangeVehCount = 0,
                             ConOrAcciComment = binding.atvConAccident.text.toString(),
                             ConOrAcciComment2 = "",
                             ConOrAcciComment3 = "",
                             ConvictionsChk = binding.checkboxConvictions.isChecked,
                             ConvictionsChk2 = false,
                             ConvictionsChk3 = false,
                             DOB = binding.atvDOB.text.toString(),
                             DOB2 = "",
                             DOB3 = "",
                             DriverId = prefs.getCurrentVehicleInfo()!!.DaId,
                             ExistingFirstUsrId = 0,
                             ExistingSecondUsrId = 0,
                             ExistingThirdUsrId = 0,
                             FirstUserAllocPosition = 0,
                             FirstUsrId = 0,
                             IsDAVehReturned = true,
                             IsVehAllocDaLeft = null,
                             IsVehAllocInOurGarage = null,
                             IsVehAllocIsDamaged = null,
                             IsVehReturnToDepo = true,
                             IsVehReturned = true,
                             IsVehReturntoSupplier = false,
                             IsVehRoadWorthy = true,
                             IsVehicleMultiAlloc = false,
                             LicenseEndDate = binding.aTvLicenseEndDate.text.toString(),
                             LicenseEndDate2 = "",
                             LicenseEndDate3 = "",
                             LicenseNo = binding.atvLicenseNumber.text.toString(),
                             LicenseNo2 = "",
                             LicenseNo3 = "",
                             LicenseStartDate = binding.aTvLicenseStartDate.text.toString(),
                             LicenseStartDate2 = "",
                             LicenseStartDate3 = "",
                             NewVmId = 0,
                             OldVmId = selectedVehicleId,
                             ParentCompanyId = selectedCompanyId,
                             RequestTypeIds = requestTypeList.map { it.Id!! },
                             SecondUserAllocPosition = 0,
                             SecondUsrId = 0,
                             Signature1 = bse64,
                             Signature2 = "",
                             Signature3 = "",
                             ThirdUserAllocPosition = 0,
                             ThirdUsrId = 0,
                             VehAllocComments = if (binding.vehicleAlocComment.text != null) binding.vehicleAlocComment.text.toString() else prefs.getCurrentVehicleInfo()!!.Comments
                                 ?: "",
                             VehAllocGarageStartDate = null,
                             VehAllocStatusId = null,
                             VehCurrentFuelLevelId = selectedVehicleFuelId,
                             VehCurrentMileage = crrMileage.toString(),
                             VehCurrentOILLevelId = selectedVehicleOilLevelListId,
                             VehSelectedLocationId = selectedVehicleLocId,
                             VehType = vehType,
                             supervisorId = prefs.osmUserId.toInt(),
                         )
                     )*/
                    /*                    mainViewModel.CollectVehicelFromSupplier(
                                            CollectVehicleFromSupplierRequest(
                                                AddBlueMileage = addBlueMileage!!,
                                                ClientRefId =,
                                                DriverId =,
                                                InspectionDate =,
                                                IsVehAllocDaLeft =,
                                                IsVehAllocInOurGarage =,
                                                IsVehAllocIsDamaged =,
                                                IsVehCollected =,
                                                NewCollectedRegNo =,
                                                NewVmId =0,
                                                OldVmId =0,
                                                ParentCompanyId =,
                                                Signature1 ="null",
                                                VehAllocComments =,
                                                VehAllocGarageStartDate =,
                                                VehAllocStatusId =,
                                                VehCurrentFuelLevelId =collectionSelectedVehicleFuelId,
                                                VehCurrentMileage =,
                                                VehCurrentOILLevelId =collectionSelectedVehicleOilLevelListId,
                                                VehSelectedLocationId =collectionSelectedVehicleLocId,
                                                VehType ="null",
                                                supervisorId =prefs.osmUserId.toInt()
                                            )
                                        )*/
                }
            }
        })
        dialog.show(supportFragmentManager, "sign")
    }

    override fun onDelete(item: GetVehicleDamageWorkingStatusResponseItem, position: Int) {
        requestTypeList.remove(item)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
  if (result.resultCode == 10 && currentImageUploadCode==5) {
                val data: Intent? = result.data
                val outputUri = data?.getStringExtra("outputUri")
                if (outputUri != null) {
                    selectedFileUri!!.add(outputUri)
                    adapter.data = selectedFileUri!!
                    adapter.notifyItemInserted(selectedFileUri!!.size)
                    prefs.saveUrisForAccidentsImages(selectedFileUri!!)

                    val inputData = Data.Builder().putInt("defectSheetID", 0)
                        .putInt("defectSheetUserId", 0).build()
                    createBackgroundUploadRequest(inputData, this, 7)
                    Log.d("ImageCaptureXX", "Output URI: $outputUri")
                } else {
                    showToast("Failed to fetch image!!", this)
                }
            }
            else if(result.resultCode==10&& currentImageUploadCode==6){
                val data: Intent? = result.data
                val outputUri = data?.getStringExtra("outputUri")
                if (outputUri != null) {
                    selectedFileUriAccident!!.add(outputUri)
                    accidentAdapter.data = selectedFileUriAccident!!
                    accidentAdapter.notifyItemInserted(selectedFileUriAccident!!.size)
                    prefs.saveUrisForAccidentsImages(selectedFileUri!!)

                    val inputData = Data.Builder().putInt("defectSheetID", 0)
                        .putInt("defectSheetUserId", 0).build()
                    createBackgroundUploadRequest(inputData, this, 8)
                    Log.d("ImageCaptureXX", "Output URI: $outputUri")
                } else {
                    showToast("Failed to fetch image!!", this)
                }
            }
            else {

              showToast("Failed!!", this)
            }
        }
}
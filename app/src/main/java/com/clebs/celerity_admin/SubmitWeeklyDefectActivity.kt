package com.clebs.celerity_admin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.bumptech.glide.Glide
import com.clebs.celerity_admin.database.CheckInspection
import com.clebs.celerity_admin.database.DefectSheet
import com.clebs.celerity_admin.database.IsInspectionDone
import com.clebs.celerity_admin.databinding.ActivitySubmitWeeklyDefectBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.SaveInspectionRequestBody
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.App
import com.clebs.celerity_admin.utils.BackgroundUploadWorker
import com.clebs.celerity_admin.utils.DefectFileType
import com.clebs.celerity_admin.utils.DependencyClass.currentWeeklyDefectItem
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.convertImageUriToBase64
import com.clebs.celerity_admin.utils.dateToday
import com.clebs.celerity_admin.utils.getMimeType
import com.clebs.celerity_admin.utils.radioButtonState
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.utils.toast
import com.clebs.celerity_admin.utils.uriToFileName
import com.clebs.celerity_admin.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class SubmitWeeklyDefectActivity : AppCompatActivity() {
    lateinit var binding: ActivitySubmitWeeklyDefectBinding
    private lateinit var vm: MainViewModel
    private val syncWorker by lazy { OneTimeWorkRequestBuilder<SyncWorker>().build() }
    private var selectedOilLevelID: Int = 0
    private var selectedEngineCoolantLevelID: Int = 0
    private var selectedBreakFluidLevelID: Int = 0
    private var selectedWindscreenWashingID: Int = 0
    private var selectedWindScreenConditionID: Int = 0
    private lateinit var oilListNames: List<String>
    private lateinit var loadingDialog: LoadingDialog
    private var selectedFileUri: Uri? = null
    private lateinit var oilLevelIds: List<Int>
    private var imageMode = 0
    private var dbDefectSheet: DefectSheet? = null
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var regexPattern: Regex
    private var currentDefSheetID = 0
    private var inspectionID = String()
    private var VdhCheckDaId = String()
    private var VdhCheckVmId = String()
    private var VehCheckLmId = String()
    private var VdhCheckWeekNo = String()
    private var vdhCheckId = String()
    private var VdhCheckYearNo = String()
    private var startonetime: Boolean? = false
    private var otherImagesList: String = ""
    private var inspectionreg: String? = null
    private var isfirst: Boolean? = false
    private var isupload: Boolean? = true
    var defectSheetUserId: Int = 0
    private var crrType: Int = 0

    companion object {

        private val REQUIRED_PERMISSIONS = mutableListOf(
            Manifest.permission.CAMERA
        ).apply {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }.toTypedArray()
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        vm = ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_submit_weekly_defect)
        loadingDialog = LoadingDialog(this)
        cqCode()
        binding.daTv.setText(currentWeeklyDefectItem!!.dAName)
        binding.daReg.setText(currentWeeklyDefectItem!!.vehRegNo)
        binding.daLoc.setText(currentWeeklyDefectItem!!.locationName)
        vm.GetVehWeeklyDefectSheetInspectionInfo(
            currentWeeklyDefectItem!!.vdhCheckId
        )
        vm.isinspectiondonelivedata.observe(this, Observer {
            if (it != null) {
                if (it.isInspectionDone) {
                    binding.llmain.visibility = View.VISIBLE
                    binding.llstart.visibility = View.VISIBLE
                    binding.tvInspection.setText("OSM Vehicle Inspection Completed")
                    binding.llstart.strokeColor = ContextCompat.getColor(this, R.color.green)
                    binding.tvInspection.setTextColor(ContextCompat.getColor(this, R.color.green))
                    binding.btStart.visibility = View.GONE
                    binding.done.visibility = View.VISIBLE
                    binding.done.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@SubmitWeeklyDefectActivity,
                            R.drawable.donessss
                        )
                    )

                }
            } else {
                binding.llmain.visibility = View.GONE
                binding.btStart.visibility = View.VISIBLE
                binding.llstart.setStrokeColor(
                    ContextCompat.getColor(
                        this, R.color.very_very_light_red
                    )
                )
                binding.tvInspection.setTextColor(ContextCompat.getColor(this, R.color.text_color))
                binding.tvInspection.setText("Start OSM Inspection *")
                binding.done.visibility = View.GONE
                binding.llstart.visibility = View.VISIBLE
            }


        })

        if (currentWeeklyDefectItem != null) vm.GetWeeklyDefectCheckImages(currentWeeklyDefectItem!!.vdhCheckId)

        currentDefSheetID = currentWeeklyDefectItem!!.vdhCheckId
        defectSheetUserId = currentWeeklyDefectItem!!.vdhCheckDaId


        if (currentWeeklyDefectItem != null) {
            loadingDialog.show()
            vm.GetWeeklyDefectCheckImages(currentWeeklyDefectItem!!.vdhCheckId)
        }

        dbDefectSheet = App.offlineSyncDB?.getDefectSheet(
            currentWeeklyDefectItem!!.vdhCheckId
        )

        if (dbDefectSheet == null) {
            lifecycleScope.launch {
                App.offlineSyncDB?.insertOrUpdate(
                    DefectSheet(
                        id = currentWeeklyDefectItem!!.vdhCheckId
                    )
                )
                dbDefectSheet =
                    App.offlineSyncDB?.getDefectSheet(currentWeeklyDefectItem!!.vdhCheckId)

            }
        }

        binding.back.setOnClickListener {
            finish()
        }
        binding.cancel.setOnClickListener {
            finish()
        }

        if (dbDefectSheet != null) {
            Log.d("DbDefectSheet", dbDefectSheet.toString())
            selectedOilLevelID = dbDefectSheet?.oilLevelID!!
            selectedEngineCoolantLevelID = dbDefectSheet!!.engineCoolantLevelID
            selectedBreakFluidLevelID = dbDefectSheet!!.brakeFluidLevelID
            selectedWindscreenWashingID = dbDefectSheet!!.windScreenWashingLevelId
            selectedWindScreenConditionID = dbDefectSheet!!.windScreenConditionId
            binding.checkpower.isChecked = dbDefectSheet!!.powerSteeringCheck
//            binding.signactioncheck.isChecked = dbDefectSheet!!.WeeklyActionCheck
//            binding.signaprrovecheck.isChecked = dbDefectSheet!!.WeeklyApproveCheck
            setUploadCardBtn2(
                dbDefectSheet!!.tyreDepthFrontNSImage,
                binding.tyreDepthFrontImageUploadBtn,
                binding.tyreDepthFrontImageFileName
            )

            setRadioCard(
                dbDefectSheet!!.tyrePressureRearOSRB == 1,
                binding.tyrePressureFrontFullRB,
                binding.tyrePressureFrontBelowRB
            )

            setUploadCardBtn2(
                dbDefectSheet!!.tyreDepthRearNSImage,
                binding.tyreDepthRearImageUploadBtn,
                binding.tyreDepthRearImageUploadFileName
            )

            setRadioCard(
                dbDefectSheet!!.tyrePressureRearNSRB == 1,
                binding.tyrePressureRearNSFullRB,
                binding.tyrePressureRearNSBelowRB
            )

            setUploadCardBtn2(
                dbDefectSheet!!.tyreDepthRearOSImage,
                binding.tyreDepthRearOSImageUploadBtn,
                binding.tyreDepthRearOSFileNameTV
            )

            setRadioCard(
                dbDefectSheet!!.tyrePressureRearOSRB == 1,
                binding.tyrePressureRearOSFULLRB,
                binding.tyrePressureRearOSBelowRB
            )

            setUploadCardBtn2(
                dbDefectSheet!!.tyreDepthFrontOSImage,
                binding.tyreDepthFrontOSImageUploadBtn,
                binding.tyreDepthFrontOSImageFilenameTV
            )

            setRadioCard(
                dbDefectSheet?.tyrePressureFrontNSRB == 1,
                binding.tyrePressureFrontOSFullRB,
                binding.tyrePressureFrontOSBelowRB
            )

            setUploadCardBtn2(
                dbDefectSheet!!.engineLevelImage,
                binding.engineOilImageUploadBtn,
                binding.engineOilImageUploadFileName
            )

            setUploadCardBtn2(
                dbDefectSheet!!.addBlueLevelImage,
                binding.addBlueLevelUploadBtn,
                binding.addBlueLevelUploadFileName
            )

            setUploadCardBtn2(
                dbDefectSheet!!.nsWingMirrorImage,
                binding.nsWingMirrorUploadBtn,
                binding.nsWingMirrorUploadFileName
            )

            setUploadCardBtn2(
                dbDefectSheet!!.osWingMirrorImage,
                binding.osWingMirrorUploadBtn,
                binding.osWingMirrorUploadFileName
            )
            setUploadCardBtn2(
                dbDefectSheet!!.threeSixtyVideo,
                binding.Three60VideoUploadBtn,
                binding.Three60VideoFileNameTV
            )

            if (dbDefectSheet?.otherImages != null) {
                var size = dbDefectSheet?.otherImages!!.split(",").size
                var others = ""
                for (i in 0 until size) {
                    others += "im$i.jpg \n"
                }
                binding.otherImagesTV.text = others

            }
            if (!dbDefectSheet!!.comment.isNullOrBlank()) {
                binding.actionCommentET.setText(dbDefectSheet!!.comment!!)
            }/*            Toast.makeText(
                            this@SubmitWeeklyDefectActivity,
                            " ${dbDefectSheet!!.comment}",
                            Toast.LENGTH_SHORT
                        ).show()*/
        }

        observers()
        clickListeners()
        cqCode()
    }

    private fun clickListeners() {
        binding.tyreDepthFrontImageUploadBtn.setOnClickListener {
            addImage(0)


        }

        binding.tyreDepthRearImageUploadBtn.setOnClickListener {
            addImage(1)
        }

        binding.tyreDepthRearOSImageUploadBtn.setOnClickListener {
            addImage(2)
        }
        binding.tyreDepthFrontOSImageUploadBtn.setOnClickListener {
            addImage(3)
        }

        binding.engineOilImageUploadBtn.setOnClickListener {
            addImage(4)
        }

        binding.addBlueLevelUploadBtn.setOnClickListener {
            addImage(5)
        }

        binding.nsWingMirrorUploadBtn.setOnClickListener {
            addImage(6)
        }

        binding.osWingMirrorUploadBtn.setOnClickListener {
            addImage(7)
        }

        binding.Three60VideoUploadBtn.setOnClickListener {
            imageMode = 8
            crrType = 1
            if (allPermissionsGranted()) {
                upload()
            } else {
                requestPermissions()
            }
        }

        binding.otherPictureUploadBtn.setOnClickListener {
            addImage(9)
        }

        binding.save.setOnClickListener {
            dbDefectSheet?.tyrePressureFrontNSRB = radioButtonState(
                binding.tyrePressureFrontFullRB, binding.tyrePressureFrontBelowRB
            )

            dbDefectSheet?.tyrePressureRearNSRB = radioButtonState(
                binding.tyrePressureRearNSFullRB, binding.tyrePressureRearNSBelowRB
            )

            dbDefectSheet?.tyrePressureFrontOSRB = radioButtonState(
                binding.tyrePressureFrontOSFullRB, binding.tyrePressureFrontOSBelowRB
            )

            dbDefectSheet?.tyrePressureRearOSRB = radioButtonState(
                binding.tyrePressureRearOSFULLRB, binding.tyrePressureRearOSBelowRB
            )

            dbDefectSheet?.brakeFluidLevelID = selectedBreakFluidLevelID
            dbDefectSheet?.engineCoolantLevelID = selectedEngineCoolantLevelID
            dbDefectSheet?.oilLevelID = selectedOilLevelID
            dbDefectSheet?.windScreenWashingLevelId = selectedWindscreenWashingID
            dbDefectSheet?.windScreenConditionId = selectedWindScreenConditionID
            dbDefectSheet?.powerSteeringCheck = binding.checkpower.isChecked
            dbDefectSheet?.WeeklyActionCheck = binding.signactioncheck.isChecked
            dbDefectSheet?.WeeklyApproveCheck = binding.signaprrovecheck.isChecked
            if (binding.actionCommentET.text != null)
                dbDefectSheet?.comment = binding.actionCommentET.text.toString()

            if (otherImagesList.isNotBlank()) //sir ab dekhna ok
                dbDefectSheet?.otherImages = otherImagesList

            if (binding.actionCommentET.text.isNullOrEmpty()) {

                Toast.makeText(this, "Please enter comment.", Toast.LENGTH_SHORT).show()


            } else {
                if (isWorkCompleted(isupload!!)) {

//                    if (dbDefectSheet?.tyreDepthFrontNSImage.isNullOrEmpty()) {
//                        Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
//                        binding.signactioncheck.visibility = View.GONE
//                        binding.signaprrovecheck.visibility = View.GONE
//
//                    } else if (dbDefectSheet?.tyreDepthRearNSImage.isNullOrEmpty()) {
//
//                        Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
//                        binding.signactioncheck.visibility = View.GONE
//                        binding.signaprrovecheck.visibility = View.GONE
//                    } else if (dbDefectSheet?.tyreDepthRearOSImage.isNullOrEmpty()) {
//                        binding.signactioncheck.visibility = View.GONE
//                        binding.signaprrovecheck.visibility = View.GONE
//                        Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
//
//                    } else if (dbDefectSheet?.tyreDepthFrontOSImage.isNullOrEmpty()) {
//                        binding.signactioncheck.visibility = View.GONE
//                        binding.signaprrovecheck.visibility = View.GONE
//
//                        Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
//                    } else if (dbDefectSheet?.addBlueLevelImage.isNullOrEmpty()) {
//                        binding.signactioncheck.visibility = View.GONE
//                        binding.signaprrovecheck.visibility = View.GONE
//
//                        Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
//                    } else if (dbDefectSheet?.engineLevelImage.isNullOrEmpty()) {
//                        binding.signactioncheck.visibility = View.GONE
//                        binding.signaprrovecheck.visibility = View.GONE
//
//                        Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
//                    } else if (dbDefectSheet?.nsWingMirrorImage.isNullOrEmpty()) {
//                        binding.signactioncheck.visibility = View.GONE
//                        binding.signaprrovecheck.visibility = View.GONE
//                        Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
//                    } else if (dbDefectSheet?.osWingMirrorImage.isNullOrEmpty()) {
//                        binding.signactioncheck.visibility = View.GONE
//                        binding.signaprrovecheck.visibility = View.GONE
//                        Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
//                    } else {
//                        binding.signactioncheck.visibility = View.VISIBLE
//                        binding.signaprrovecheck.visibility = View.VISIBLE
//                        lifecycleScope.launch {
//                            App.offlineSyncDB?.insertOrUpdate(
//                                dbDefectSheet!!
//                            )
//                        }
                    binding.signactioncheck.visibility = View.VISIBLE
                    binding.signaprrovecheck.visibility = View.VISIBLE
                    if (!binding.signactioncheck.isChecked || !binding.signaprrovecheck.isChecked) {
                        Toast.makeText(this, "Please check the ticks", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        saveWithWorker()
                    }

                }
            }

//                } else {
//                    Toast.makeText(
//                        this@SubmitWeeklyDefectActivity,
//                        "Background upload still in progress",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }

//            saveWithWorker()
//            lifecycleScope.launch {
//                val isWorkSuccessful = observeUniqueWork(this@SubmitWeeklyDefectActivity)
//                if (isWorkSuccessful) {
//                    saveWithWorker()
//                    // The unique work succeeded
//                } else {
//                    Toast.makeText(
//                        this@SubmitWeeklyDefectActivity,
//                        "Uploading work is in progress",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    // The unique work failed or was cancelled
//                }
//            }


        }

    }


    private fun addImage(mode: Int) {
        imageMode = mode
        crrType = 0
        if (allPermissionsGranted()) {
            upload()
        } else {
            requestPermissions()
        }
    }


    private fun observers() {
        vm.lDGetWeeklyDefectCheckImages.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                selectedOilLevelID = it.VdhDefChkImgOilLevelId
                selectedEngineCoolantLevelID = it.EngineCoolantLevelId
                selectedBreakFluidLevelID = it.BrakeFluidLevelId
                selectedWindscreenWashingID = it.WindowScreenWashingLiquidId
                selectedWindScreenConditionID = it.WindScreenConditionId
                Log.e("lkekskdsldkls", "observers: " + it)
                vm.GetVehOilLevelList()
                vm.GetVehWindScreenConditionStatus()
                Log.d(
                    "Selections",
                    "$selectedOilLevelID \n$selectedEngineCoolantLevelID \n$selectedBreakFluidLevelID \n$selectedWindscreenWashingID \n$selectedWindScreenConditionID"
                )
                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthFrontNs,
                    binding.tyreDepthFrontImageUploadBtn,
                    binding.tyreDepthFrontImageFileName
                )

                setRadioCard(
                    it.TyrePressureFrontNS,
                    binding.tyrePressureFrontFullRB,
                    binding.tyrePressureFrontBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthRearNs,
                    binding.tyreDepthRearImageUploadBtn,
                    binding.tyreDepthRearImageUploadFileName
                )

                setRadioCard(
                    it.TyrePressureFrontNS,
                    binding.tyrePressureRearNSFullRB,
                    binding.tyrePressureRearNSBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthRearOs,
                    binding.tyreDepthRearOSImageUploadBtn,
                    binding.tyreDepthRearOSFileNameTV
                )

                setRadioCard(
                    it.TyrePressureRearOS,
                    binding.tyrePressureRearOSFULLRB,
                    binding.tyrePressureRearOSBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthFrontOs,
                    binding.tyreDepthFrontOSImageUploadBtn,
                    binding.tyreDepthFrontOSImageFilenameTV
                )

                setRadioCard(
                    it.TyrePressureFrontOS,
                    binding.tyrePressureFrontOSFullRB,
                    binding.tyrePressureFrontOSBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgEngineOilLevel,
                    binding.engineOilImageUploadBtn,
                    binding.engineOilImageUploadFileName
                )

                setUploadCardBtn(
                    it.VdhDefChkImgAddBlueLevel,
                    binding.addBlueLevelUploadBtn,
                    binding.addBlueLevelUploadFileName
                )

                setUploadCardBtn(
                    it.VdhDefChkImgNswingMirror,
                    binding.nsWingMirrorUploadBtn,
                    binding.nsWingMirrorUploadFileName
                )

                setUploadCardBtn(
                    it.VdhDefChkImgOswingMirror,
                    binding.osWingMirrorUploadBtn,
                    binding.osWingMirrorUploadFileName
                )
                setUploadCardBtn(
                    it.VdhDefChkImgVan360Video,
                    binding.Three60VideoUploadBtn,
                    binding.Three60VideoFileNameTV
                )

                if (it.VdhVerAdminComment.isNotBlank()) {
                    binding.actionCommentET.setText(it.VdhVerAdminComment)
                }
            }
        }


        vm.lDGetVehWindScreenConditionStatus.observe(this) { windScreenConditionList ->
            if (windScreenConditionList != null) {

                val windScreenConditionStatusNameList = windScreenConditionList.map { it.Name }
                val windScreenConditionStatusNameId = windScreenConditionList.map { it.Id }

//                if (selectedWindScreenConditionID > 0) {
//                    binding.spinnerWindScreenCondition.setText(
//                        windScreenConditionStatusNameList[windScreenConditionStatusNameId.indexOf(
//                            selectedWindScreenConditionID
//                        )]
//                    )
//                    binding.spinnerWindScreenCondition.setSelection(
//                        windScreenConditionStatusNameId.indexOf(
//                            selectedWindScreenConditionID
//                        )
//                    )
//                }

                setSpinner(
                    binding.spinnerWindScreenCondition,
                    windScreenConditionStatusNameList,
                    windScreenConditionStatusNameId
                )

            }
        }

        vm.lDGetVehOilLevelList.observe(this) { oilLevelList ->
            if (oilLevelList != null) {
                oilListNames = oilLevelList.map { it.VehOilLevelName }
                oilLevelIds = oilLevelList.map { it.VehOilLevelId }
                Log.e("ksdskjdsk", "observers: " + oilLevelList)
                Log.d(
                    "Selections",
                    "indexes ${oilLevelIds.indexOf(selectedOilLevelID)}" + "\n${
                        oilLevelIds.indexOf(selectedEngineCoolantLevelID)
                    }" + "\n${oilLevelIds.indexOf(selectedBreakFluidLevelID)}" + "\n${
                        oilLevelIds.indexOf(
                            selectedWindscreenWashingID
                        )
                    }"
                )

                if (selectedOilLevelID > 0) {
                    binding.spinnerOilLevel.setText(
                        oilListNames[oilLevelIds.indexOf(
                            selectedOilLevelID
                        )]
                    )
                    binding.spinnerOilLevel.setSelection(oilLevelIds.indexOf(selectedOilLevelID))
                }


                if (selectedEngineCoolantLevelID > 0) {
                    binding.spinnerEngineCoolant.setText(
                        oilListNames[oilLevelIds.indexOf(
                            selectedEngineCoolantLevelID
                        )]
                    )
                    binding.spinnerEngineCoolant.setSelection(
                        oilLevelIds.indexOf(
                            selectedEngineCoolantLevelID
                        )
                    )
                }

                if (selectedBreakFluidLevelID > 0) {
                    binding.spinnerBrakeFluid.setText(
                        oilListNames[oilLevelIds.indexOf(
                            selectedBreakFluidLevelID
                        )]
                    )
                    binding.spinnerBrakeFluid.setSelection(
                        oilLevelIds.indexOf(
                            selectedBreakFluidLevelID
                        )
                    )
                }

//                if (selectedWindscreenWashingID > 0) {
//                    binding.spinnerWindscreenWashingLiquid.setText(
//                        oilListNames[oilLevelIds.indexOf(
//                            selectedWindscreenWashingID
//                        )]
//                    )
//
//
//                    binding.spinnerWindscreenWashingLiquid.setSelection(
//                        oilLevelIds.indexOf(
//                            selectedWindscreenWashingID
//                        )
//                    )


//                    }


                setSpinner(binding.spinnerOilLevel, oilListNames, oilLevelIds)
                setSpinner(binding.spinnerEngineCoolant, oilListNames, oilLevelIds)
                setSpinner(binding.spinnerBrakeFluid, oilListNames, oilLevelIds)
                setSpinner(binding.spinnerWindscreenWashingLiquid, oilListNames, oilLevelIds)

            }
        }

    }

    private fun setUploadCardBtn(
        vdhDefChkImgTyreThreadDepthFrontNs: String,
        tyreDepthFrontImageUploadBtn: AppCompatButton,
        tyreDepthFrontImageFileName: TextView
    ) {
        if (vdhDefChkImgTyreThreadDepthFrontNs.isNotBlank()) {
            "Upload Again".also { tyreDepthFrontImageUploadBtn.text = it }
            tyreDepthFrontImageFileName.text = vdhDefChkImgTyreThreadDepthFrontNs
            tyreDepthFrontImageFileName.setTextColor(ContextCompat.getColor(this, R.color.blue_hex))
            tyreDepthFrontImageUploadBtn.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.green)
        }
    }

    private fun setUploadCardBtn2(
        vdhDefChkImgTyreThreadDepthFrontNs: String?,
        tyreDepthFrontImageUploadBtn: AppCompatButton,
        tyreDepthFrontImageFileName: TextView
    ) {
        if (!vdhDefChkImgTyreThreadDepthFrontNs.isNullOrBlank()) {
            "Upload Again".also { tyreDepthFrontImageUploadBtn.text = it }
            tyreDepthFrontImageFileName.text = uriToFileName(vdhDefChkImgTyreThreadDepthFrontNs!!)
            tyreDepthFrontImageFileName.setTextColor(ContextCompat.getColor(this, R.color.blue_hex))
            tyreDepthFrontImageUploadBtn.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.greenBtn)
        }
    }

    private fun setRadioCard(
        tyrePressureFrontNS: Boolean,
        tyrePressureFrontFullRB: RadioButton,
        tyrePressureFrontBelowRB: RadioButton
    ) {
        if (tyrePressureFrontNS) {
            tyrePressureFrontFullRB.isChecked = true
            tyrePressureFrontBelowRB.isChecked = false
        } else {
            tyrePressureFrontBelowRB.isChecked = true
            tyrePressureFrontFullRB.isChecked = false
        }
    }

    private fun setSpinner(
        spinner: AutoCompleteTextView, items: List<String>, ids: List<Int>
    ) {
        val itemsList = mutableListOf<String>()
        Log.d("ID", "$ids")
        itemsList.addAll(items)
        val adapter = ArrayAdapter(this, R.layout.dropdown_menu_popup_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.setAdapter(adapter)
        spinner.setOnItemClickListener { parent, _, position, _ ->
            run {
                parent?.let { nonNullParent ->

                    val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                    selectedItem.let {
                        when (spinner) {
                            binding.spinnerOilLevel -> {
                                selectedOilLevelID = ids[position]
                            }

                            binding.spinnerBrakeFluid -> {
                                selectedBreakFluidLevelID = ids[position]
                            }

                            binding.spinnerEngineCoolant -> {
                                selectedEngineCoolantLevelID = ids[position]
                            }

                            binding.spinnerWindscreenWashingLiquid -> {
                                selectedWindscreenWashingID = ids[position]

                            }

                            binding.spinnerWindScreenCondition -> {
                                selectedWindScreenConditionID = ids[position]
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && !it.value) permissionGranted = false
        }
        if (!permissionGranted) {
            showToast("Permission denied", this)
        } else {
            upload()
        }
    }

    fun upload() {
        if (crrType == 0) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        } else {
            pickVideo()
        }
    }

    private fun pickVideo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)

        intent.type = "video/*"
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                data?.data?.let {

                    selectedFileUri = it
                    when (imageMode) {
                        0 -> {
                            dbDefectSheet?.tyreDepthFrontNSImage = selectedFileUri.toString()
                            setUploadCardBtn2(
                                dbDefectSheet!!.tyreDepthFrontNSImage!!,
                                binding.tyreDepthFrontImageUploadBtn,
                                binding.tyreDepthFrontImageFileName
                            )
                        }

                        1 -> {
                            dbDefectSheet?.tyreDepthRearNSImage = selectedFileUri.toString()
                            setUploadCardBtn2(
                                dbDefectSheet!!.tyreDepthRearNSImage!!,
                                binding.tyreDepthRearImageUploadBtn,
                                binding.tyreDepthRearImageUploadFileName
                            )
                        }

                        2 -> {
                            dbDefectSheet?.tyreDepthRearOSImage = selectedFileUri.toString()
                            setUploadCardBtn2(
                                dbDefectSheet!!.tyreDepthRearOSImage!!,
                                binding.tyreDepthRearOSImageUploadBtn,
                                binding.tyreDepthRearOSFileNameTV
                            )
                        }

                        3 -> {
                            dbDefectSheet?.tyreDepthFrontOSImage = selectedFileUri.toString()
                            setUploadCardBtn2(
                                dbDefectSheet!!.tyreDepthFrontOSImage!!,
                                binding.tyreDepthFrontOSImageUploadBtn,
                                binding.tyreDepthFrontOSImageFilenameTV
                            )
                        }

                        4 -> {
                            dbDefectSheet?.engineLevelImage = selectedFileUri.toString()
                            setUploadCardBtn2(
                                dbDefectSheet!!.engineLevelImage!!,
                                binding.engineOilImageUploadBtn,
                                binding.engineOilImageUploadFileName
                            )
                        }

                        5 -> {
                            dbDefectSheet?.addBlueLevelImage = selectedFileUri.toString()
                            setUploadCardBtn2(
                                dbDefectSheet!!.addBlueLevelImage!!,
                                binding.addBlueLevelUploadBtn,
                                binding.addBlueLevelUploadFileName
                            )
                        }

                        6 -> {
                            dbDefectSheet?.nsWingMirrorImage = selectedFileUri.toString()
                            setUploadCardBtn2(
                                dbDefectSheet!!.nsWingMirrorImage!!,
                                binding.nsWingMirrorUploadBtn,
                                binding.nsWingMirrorUploadFileName
                            )
                        }

                        7 -> {
                            dbDefectSheet?.osWingMirrorImage = selectedFileUri.toString()
                            setUploadCardBtn2(
                                dbDefectSheet!!.osWingMirrorImage!!,
                                binding.osWingMirrorUploadBtn,
                                binding.osWingMirrorUploadFileName
                            )
                        }

                        8 -> {
                            dbDefectSheet?.threeSixtyVideo = selectedFileUri.toString()
                            Log.e("URIIIIIIIIIIIIIIIIII", "::::: "+selectedFileUri, )
                            setUploadCardBtn2(
                                dbDefectSheet!!.threeSixtyVideo!!,
                                binding.Three60VideoUploadBtn,
                                binding.Three60VideoFileNameTV
                            )
                        }

                        9 -> {
                            otherImagesList = selectedFileUri.toString()
                        }

                        else -> {
                            showToast("Wrong Selection", this)
                        }
                    }/*     val mimeType = getMimeType(selectedFileUri!!)?.toMediaTypeOrNull()
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
                             "UploadTicketDoc",
                             selectedFileUri!!.lastPathSegment + "." + (fileExtension ?: "jpg"),
                             requestBody
                         )*/
                    //save()
                }
            } else {
                finish()
                showToast("Attachment not selected!!", this)
            }
        }

    private fun cqCode() {
        cqSDKInitializer = CQSDKInitializer(this)


//        val inspectionInfo = App.offlineSyncDB!!.getInspectionInfo()
//        Log.e("result4", "onCreate: " + inspectionInfo)
//        if (!App.offlineSyncDB!!.isInspectionTableEmpty()) {
//
//
//            inspectionInfo.forEach {
//                if (Prefs.getInstance(App.instance).vehinspection == it.InspectionDoneRegNo) {
//                    binding.llmain.visibility = View.VISIBLE
//                    binding.llstart.visibility = View.VISIBLE
//                    binding.tvInspection.setText("OSM Vehicle Inspection Completed")
//                    binding.btStart.visibility = View.GONE
//                    binding.done.visibility = View.VISIBLE
//                    Glide.with(this).load(R.raw.dones).into(binding.done)
//
//                } else {
//                    Log.e("result3", "onCreate: ")
//                    binding.llmain.visibility = View.GONE
//                    binding.btStart.visibility = View.VISIBLE
//                    binding.tvInspection.setText("Start OSM Inspection *")
//                    binding.done.visibility = View.GONE
//                    binding.llstart.visibility = View.VISIBLE
//                }
//            }
//        }
        isfirst = Prefs.getInstance(this).Isfirst
        startonetime = isfirst

//        Log.e("newinspection", "onCreate: " + Prefs.getInstance(App.instance).vehinspection)

        binding.btStart.setOnClickListener {
            startInspection()
        }
    }

    private fun startInspection() {
        clientUniqueID()



        if (cqSDKInitializer.isCQSDKInitialized()) {

            Log.e("sdkskdkdkskdkskd", "onCreateView " + inspectionreg)

            try {
                cqSDKInitializer.startInspection(activity = this, clientAttrs = ClientAttrs(
                    userName = " ",
                    dealer = " ",
                    dealerIdentifier = " ",
                    client_unique_id = inspectionID

                    //drivers ID +vechile iD + TOdays date dd// mm //yy::tt,mm
                ), inputDetails = InputDetails(
                    vehicleDetails = VehicleDetails(
                        regNumber = currentWeeklyDefectItem!!.vehRegNo.replace(
                            " ", ""
                        ), //if sent, user can't edit
                        make = "Van", //if sent, user can't edit
                        model = "Any Model", //if sent, user can't edit
                        bodyStyle = "Van"  // if sent, user can't edit - Van, Boxvan, Sedan, SUV, Hatch, Pickup [case sensitive]
                    ), customerDetails = CustomerDetails(
                        name = "", //if sent, user can't edit
                        email = "", //if sent, user can't edit
                        dialCode = "", //if sent, user can't edit
                        phoneNumber = "", //if sent, user can't edit
                    )
                ), userFlowParams = UserFlowParams(
                    isOffline = !startonetime!!, // true, Offline quote will be created | false, online quote will be created | null, online

                    skipInputPage = true, // true, Inspection will be started with camera page | false, Inspection will be started

                ),

                    result = { isStarted, msg, code ->

                        Log.e("messsagesss", "startInspection: " + msg + code)
                        if (isStarted) {
                            Prefs.getInstance(App.instance).Isfirst = false
                            startonetime = Prefs.getInstance(App.instance).Isfirst
                            Log.d("CQSDKXX", "isStarted " + msg)
                        } else {
                            Prefs.getInstance(App.instance).Isfirst = true
                            startonetime = Prefs.getInstance(App.instance).Isfirst
                            if (msg.equals("Online quote can not be created without internet")) {
                                Toast.makeText(
                                    this, "Please Turn on the internet", Toast.LENGTH_SHORT
                                ).show()
                                Log.d("CQSDKXX", "Not isStarted1  " + msg)
                            } else if (msg.equals("Sufficient data not available to create an offline quote")) {
                                Toast.makeText(
                                    this, "Please Turn on the internet", Toast.LENGTH_SHORT
                                ).show()
                                Log.d("CQSDKXX", "Not isStarted2  " + msg)
                            } else if (msg.equals("Unable to download setting updates, Please check internet")) {
                                Toast.makeText(
                                    this, "Please Turn on the internet", Toast.LENGTH_SHORT
                                ).show()
                                Log.d("CQSDKXX", "Not isStarted3  " + msg)
                            }

                            Log.d("CQSDKXX", "Not isStarted4  " + msg)
                        }
                        if (msg == "Success") {
                            Log.d("CQSDKXX", "Success " + msg)
                        } else {

                            Log.d("CQSDKXX", "Not Success " + msg)
                        }
                        if (!isStarted) {
                            Log.e("startedinspection", "onCreateView: $msg$isStarted")
                        }
                    })
            } catch (_: Exception) {

            }
        }
    }

    private fun clientUniqueID(): String {
        val x = "123456"
        val y = "123456"
        // example string
        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))

        regexPattern = Regex("${x.take(3)}${y.take(3)}${formattedDate}")


        inspectionID = regexPattern.toString()
        Prefs.getInstance(App.instance).vehinspectionUniqueID = inspectionID
        return regexPattern.toString()
        Log.e("resistrationvrnpatterhn", "clientUniqueID: " + inspectionID)
    }

    override fun onResume() {
        super.onResume()
        val message = intent?.getStringExtra(PublicConstants.quoteCreationFlowStatusMsgKeyInIntent)
            ?: "Could not identify status message"
        val tempCode =
            intent?.getIntExtra(PublicConstants.quoteCreationFlowStatusCodeKeyInIntent, -1)

        Log.e("tempcode", "onNewIntent: " + tempCode)
        if (tempCode == 200) {

            App.offlineSyncDB!!.insertinspectionInfo(
                IsInspectionDone(
                    InspectionDoneRegNo = Prefs.getInstance(App.instance).vehinspection.replace(
                        " ", ""
                    ),
                    InspectionClientUniqueID = Prefs.getInstance(App.instance).vehinspectionUniqueID.replace(
                        " ", ""
                    )
                )
            )


            vm.SaveVehWeeklyDefectSheetInspectionInfo(
                SaveInspectionRequestBody(
                    currentWeeklyDefectItem!!.vdhCheckId,
                    Prefs.getInstance(App.instance).vehinspectionUniqueID,
                    currentWeeklyDefectItem!!.vdhCheckDaId,
                    currentWeeklyDefectItem!!.vdhCheckVmId,
                    currentWeeklyDefectItem!!.vehWkCheckWeek,
                    currentWeeklyDefectItem!!.vdhCheckYear,
                    currentWeeklyDefectItem!!.vehCheckLmId
                )
            )
//vm.UploadVehOSMDefectChkFile(vdhCheckId.toInt(),"",
//    uriToMultipart(this@SubmitWeeklyDefectActivity,selectedFileUri!!)
//)

            vm.saveinspectionlivedata.observe(this, Observer {
                if (it?.Status == "200") {
                    vm.GetVehWeeklyDefectSheetInspectionInfo(
                        currentWeeklyDefectItem!!.vdhCheckId
                    )
                    Toast.makeText(this, "Inspection saved", Toast.LENGTH_SHORT).show()
                    vm.isinspectiondonelivedata.observe(this, Observer {
                        if (it != null) {

                            if (it.isInspectionDone) {

                                binding.llmain.visibility = View.VISIBLE
                                binding.llstart.visibility = View.VISIBLE
                                binding.tvInspection.setText("OSM Vehicle Inspection is done.")
                                binding.llstart.strokeColor =
                                    ContextCompat.getColor(this, R.color.green)
                                binding.llstart.strokeWidth = 4
                                binding.tvInspection.setTextColor(
                                    ContextCompat.getColor(
                                        this, R.color.green
                                    )
                                )
                                binding.btStart.visibility = View.GONE
                                binding.done.visibility = View.VISIBLE
                                binding.done.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        this@SubmitWeeklyDefectActivity,
                                        R.drawable.donessss
                                    )
                                )
                            }
                        } else {
                            binding.llmain.visibility = View.GONE
                            binding.btStart.visibility = View.VISIBLE
                            binding.llstart.strokeWidth = 2
                            binding.llstart.setStrokeColor(
                                ContextCompat.getColor(
                                    this, R.color.very_very_light_red
                                )
                            )
                            binding.tvInspection.setTextColor(
                                ContextCompat.getColor(
                                    this, R.color.text_color
                                )
                            )
                            binding.tvInspection.setText("Start OSM Inspection *")
                            binding.done.visibility = View.GONE
                            binding.llstart.visibility = View.VISIBLE
                        }


                    })

                }
            })

        } else {

            Log.d("hdhsdshdsdjshhsds", "else $tempCode $message")
        }
    }

    private fun saveWithWorker() {
        val inputData = Data.Builder().putInt("defectSheetID", currentDefSheetID)
            .putInt("defectSheetUserId", defectSheetUserId).build()


        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val uploadWorkRequest =
            OneTimeWorkRequestBuilder<BackgroundUploadWorker>().setInputData(inputData)
                .setConstraints(constraints).build()

        WorkManager.getInstance(this)
            .enqueue(uploadWorkRequest)

        Toast.makeText(
            this@SubmitWeeklyDefectActivity,
            "Data is being uploaded.",
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    fun isWorkCompleted(isuploadnew: Boolean): Boolean {
        // Get the WorkInfo for the given work ID
        val workManager = WorkManager.getInstance(this)

// Get the work info for the unique work with the tag "sync"
        val workInfoListenableFuture = workManager.getWorkInfoById(syncWorker.id)
        workInfoListenableFuture.addListener({
            // Get the WorkInfo object
            val workInfo = workInfoListenableFuture.get()

            // Check the work status

            if (workInfo != null) {
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        isupload = true
                        Log.e("completed", "isWorkCompletedcompleted: ")
                        // Work completed successfully
                        // Handle success scenario
                    }

                    WorkInfo.State.FAILED -> {
                        isupload = false
                        Log.e("completed", "isWorkfailded: ")
                        // Work failed
                        // Handle failure scenario
                    }

                    WorkInfo.State.RUNNING -> {
                        isupload = false
                        Log.e("completed", "i ")
                        // Work is currently running
                        // Handle running scenario
                    }

                    WorkInfo.State.ENQUEUED -> {
                        Log.e("completed", "j ")
                        isupload = false
                        // Work is enqueued and waiting to run
                        // Handle enqueued scenario
                    }

                    WorkInfo.State.BLOCKED -> {
                        isupload = false
                        Log.e("completed", "k ")
                        // Work is blocked and cannot run
                        // Handle blocked scenario
                    }

                    WorkInfo.State.CANCELLED -> {
                        Log.e("completed", "l")
                        isupload = false
                        // Work was cancelled
                        // Handle cancelled scenario
                    }
                }
            }
        }, Executors.newSingleThreadExecutor())

        return isuploadnew

    }


}

// Get the work info for the unique work with the given tag

// Wait for the work info to be available


class SyncWorker(
    context: Context, workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        // Perform the sync work here
        // ...
        return Result.success()
    }


}



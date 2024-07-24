package com.clebs.celerity_admin

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
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
import com.clebs.celerity_admin.ui.App.Companion.prefs
import com.clebs.celerity_admin.utils.BackgroundUploadWorker
import com.clebs.celerity_admin.utils.DefectFileType
import com.clebs.celerity_admin.utils.DependencyClass.currentWeeklyDefectItem
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.convertStringToList
import com.clebs.celerity_admin.utils.radioButtonState
import com.clebs.celerity_admin.utils.shortenFileName
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.utils.uriToFileName
import com.clebs.celerity_admin.viewModels.MainViewModel
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
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
    private var startonetime: Boolean? = true
    private var otherImagesList: MutableList<String> = mutableListOf()
    private var inspectionreg: String? = null
    private var isfirst: Boolean? = false
    private var isupload: Boolean? = true
    var defectSheetUserId: Int = 0
    private var crrType: Int = 0

    var tyreThreadDepthFrontNS: String? = null
    var tyreThreadDepthRearNS: String? = null
    var tyreThreadDepthFrontOS: String? = null
    var tyreThreadDepthRearOS: String? = null
    var engineOilLevelImage: String? = null
    var addBlueLevelImage: String? = null
    var nsWingMirrorImage: String? = null
    var osWingMirrorImage: String? = null
    var three60Video: String? = null

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

        binding.ulTyreDepthFrontNS.visibility = View.GONE
        binding.ulTyreDepthRearNS.visibility = View.GONE
        binding.ulTyreDepthRearOS.visibility = View.GONE
        binding.ulTyreDepthFrontOS.visibility = View.GONE
        binding.ulEngineOilLevelImage.visibility = View.GONE
        binding.ulAddBlueLevelImage.visibility = View.GONE
        binding.ulNSWingMirrorImage.visibility = View.GONE
        binding.ulOSWingMirrorImage.visibility = View.GONE
        binding.ulThreeSixtyVideo.visibility = View.GONE
        binding.ulotherPictureofPartsImage.visibility = View.GONE

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

        //if (currentWeeklyDefectItem != null) vm.GetWeeklyDefectCheckImages(currentWeeklyDefectItem!!.vdhCheckId)

        currentDefSheetID = currentWeeklyDefectItem!!.vdhCheckId
        defectSheetUserId = currentWeeklyDefectItem!!.vdhCheckDaId

        vm.GetOtherDefectCheckImagesInDropBox(currentDefSheetID, "OtherPicOfParts")

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

            if (!dbDefectSheet!!.tyreDepthFrontNSImage.isNullOrBlank()) {
                tyreThreadDepthFrontNS = dbDefectSheet!!.tyreDepthFrontNSImage
                binding.ulTyreDepthFrontNS.visibility = View.VISIBLE
            }
            if (!dbDefectSheet!!.tyreDepthRearNSImage.isNullOrBlank()) {
                tyreThreadDepthRearNS = dbDefectSheet!!.tyreDepthRearNSImage
                binding.ulTyreDepthRearNS.visibility = View.VISIBLE
            }
            if (!dbDefectSheet!!.tyreDepthRearOSImage.isNullOrBlank()) {
                tyreThreadDepthRearOS = dbDefectSheet!!.tyreDepthRearOSImage
                binding.ulTyreDepthRearOS.visibility = View.VISIBLE
            }

            if (!dbDefectSheet!!.tyreDepthFrontOSImage.isNullOrBlank()) {
                tyreThreadDepthFrontOS = dbDefectSheet!!.tyreDepthFrontOSImage
                binding.ulTyreDepthFrontOS.visibility = View.VISIBLE
            }

            if (!dbDefectSheet!!.engineLevelImage.isNullOrBlank()) {
                engineOilLevelImage = dbDefectSheet!!.engineLevelImage
                binding.ulEngineOilLevelImage.visibility = View.VISIBLE
            }

            if (!dbDefectSheet!!.addBlueLevelImage.isNullOrBlank()) {
                addBlueLevelImage = dbDefectSheet!!.addBlueLevelImage
                binding.ulAddBlueLevelImage.visibility = View.VISIBLE
            }
            if (!dbDefectSheet!!.nsWingMirrorImage.isNullOrBlank()) {
                nsWingMirrorImage = dbDefectSheet!!.nsWingMirrorImage
                binding.ulNSWingMirrorImage.visibility = View.VISIBLE
            }
            if (!dbDefectSheet!!.osWingMirrorImage.isNullOrBlank()) {
                osWingMirrorImage = dbDefectSheet!!.osWingMirrorImage
                binding.ulOSWingMirrorImage.visibility = View.VISIBLE
            }
            if (!dbDefectSheet!!.threeSixtyVideo.isNullOrBlank()) {
                three60Video = dbDefectSheet!!.threeSixtyVideo
                binding.ulThreeSixtyVideo.visibility = View.VISIBLE
            }

            binding.signactioncheck.isChecked = dbDefectSheet!!.WeeklyActionCheck
            binding.signaprrovecheck.isChecked = dbDefectSheet!!.WeeklyApproveCheck

            setUploadCardBtn2(
                dbDefectSheet!!.tyreDepthFrontNSImage,
                binding.tyreDepthFrontImageUploadBtn,
                binding.tyreDepthFrontImageFileName
            )

            setRadioCard(
                dbDefectSheet!!.tyrePressureRearOSRB,
                binding.tyrePressureFrontFullRB,
                binding.tyrePressureFrontBelowRB
            )

            setUploadCardBtn2(
                dbDefectSheet!!.tyreDepthRearNSImage,
                binding.tyreDepthRearImageUploadBtn,
                binding.tyreDepthRearImageUploadFileName
            )

            setRadioCard(
                dbDefectSheet!!.tyrePressureRearNSRB,
                binding.tyrePressureRearNSFullRB,
                binding.tyrePressureRearNSBelowRB
            )

            setUploadCardBtn2(
                dbDefectSheet!!.tyreDepthRearOSImage,
                binding.tyreDepthRearOSImageUploadBtn,
                binding.tyreDepthRearOSFileNameTV
            )

            setRadioCard(
                dbDefectSheet!!.tyrePressureRearOSRB,
                binding.tyrePressureRearOSFULLRB,
                binding.tyrePressureRearOSBelowRB
            )

            setUploadCardBtn2(
                dbDefectSheet!!.tyreDepthFrontOSImage,
                binding.tyreDepthFrontOSImageUploadBtn,
                binding.tyreDepthFrontOSImageFilenameTV
            )

            setRadioCard(
                dbDefectSheet!!.tyrePressureFrontNSRB,
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
                binding.ulotherPictureofPartsImage.visibility = View.VISIBLE
                var others = ""
                for (i in 0 until size) {
                    if (i != size - 1)
                        others += "im$i.jpg \n"
                    else
                        others += "im$i.jpg"
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
        //cqCode()
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

            if (otherImagesList.size > 0)
                dbDefectSheet?.otherImages = otherImagesList.joinToString(",")


            //if (isWorkCompleted(isupload!!)) {
            if (canStartNewWork()) {

                if (tyreThreadDepthFrontNS.isNullOrEmpty()) {
                    Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
                    /*   binding.signactioncheck.visibility = View.GONE
                                binding.signaprrovecheck.visibility = View.GONE*/

                } else if (tyreThreadDepthRearNS.isNullOrEmpty()) {

                    Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
                    /*   binding.signactioncheck.visibility = View.GONE
                                 binding.signaprrovecheck.visibility = View.GONE*/
                } else if (tyreThreadDepthRearOS.isNullOrEmpty()) {
                    /*   binding.signactioncheck.visibility = View.GONE
                                    binding.signaprrovecheck.visibility = View.GONE*/
                    Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
                } else if (tyreThreadDepthFrontOS.isNullOrEmpty()) {
                    /*   binding.signactioncheck.visibility = View.GONE
                               binding.signaprrovecheck.visibility = View.GONE*/

                    Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
                } else if (addBlueLevelImage.isNullOrEmpty()) {
                    /*   binding.signactioncheck.visibility = View.GONE
                          binding.signaprrovecheck.visibility = View.GONE*/

                    Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
                } else if (engineOilLevelImage.isNullOrEmpty()) {
                    /*   binding.signactioncheck.visibility = View.GONE
                                 binding.signaprrovecheck.visibility = View.GONE*/

                    Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
                } else if (nsWingMirrorImage.isNullOrEmpty()) {
                    /*   binding.signactioncheck.visibility = View.GONE
                        binding.signaprrovecheck.visibility = View.GONE*/
                    Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
                } else if (osWingMirrorImage.isNullOrEmpty()) {
                    /*   binding.signactioncheck.visibility = View.GONE
                     binding.signaprrovecheck.visibility = View.GONE*/
                    Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
                } else if (three60Video.isNullOrEmpty()) {
                    /*   binding.signactioncheck.visibility = View.GONE
                       binding.signaprrovecheck.visibility = View.GONE*/
                    Toast.makeText(this, "Please upload all images.", Toast.LENGTH_SHORT).show()
                } else if (binding.actionCommentET.text.isNullOrEmpty()) {

                    Toast.makeText(
                        this,
                        "Please add comment.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    /*binding.signactioncheck.visibility = View.VISIBLE
                    binding.signaprrovecheck.visibility = View.VISIBLE*/
                    lifecycleScope.launch {
                        App.offlineSyncDB?.insertOrUpdate(
                            dbDefectSheet!!
                        )
                    }
                    if (!binding.signactioncheck.isChecked || !binding.signaprrovecheck.isChecked) {
                        Toast.makeText(this, "Please check the ticks", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        saveWithWorker()
                    }
                }
            } else {
                Toast.makeText(
                    this@SubmitWeeklyDefectActivity,
                    "Background upload still in progress",
                    Toast.LENGTH_SHORT
                ).show()
            }
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


                if (it.VdhDefChkImgTyrethreaddepthFrontNs.isNotBlank()) {
                    tyreThreadDepthFrontNS = it.VdhDefChkImgTyrethreaddepthFrontNs
                    dbDefectSheet!!.uploadTyreDepthFrontNSImage = false
                    binding.ulTyreDepthFrontNS.visibility = View.GONE
                }
                if (it.VdhDefChkImgTyrethreaddepthRearNs.isNotBlank()) {
                    tyreThreadDepthRearNS = it.VdhDefChkImgTyrethreaddepthRearNs
                    dbDefectSheet!!.uploadTyreDepthRearNSImage = false

                    binding.ulTyreDepthRearNS.visibility = View.GONE
                }
                if (it.VdhDefChkImgTyrethreaddepthRearOs.isNotBlank()) {
                    tyreThreadDepthRearOS = it.VdhDefChkImgTyrethreaddepthRearOs
                    dbDefectSheet!!.uploadTyreDepthRearOSImage = false

                    binding.ulTyreDepthRearOS.visibility = View.GONE
                }

                if (it.VdhDefChkImgTyrethreaddepthFrontOs.isNotBlank()) {
                    tyreThreadDepthFrontOS = it.VdhDefChkImgTyrethreaddepthFrontOs
                    dbDefectSheet!!.uploadTyreDepthFrontOSImage = false

                    binding.ulTyreDepthFrontOS.visibility = View.GONE
                }

                if (it.VdhDefChkImgEngineOilLevel.isNotBlank()) {
                    engineOilLevelImage = it.VdhDefChkImgEngineOilLevel
                    dbDefectSheet!!.uploadEngineLevelImage = false

                    binding.ulEngineOilLevelImage.visibility = View.GONE
                }

                if (it.VdhDefChkImgAddBlueLevel.isNotBlank()) {
                    addBlueLevelImage = it.VdhDefChkImgAddBlueLevel
                    dbDefectSheet!!.uploadAddBlueLevelImage = false

                    binding.ulAddBlueLevelImage.visibility = View.GONE
                }
                if (it.VdhDefChkImgNswingMirror.isNotBlank()) {
                    nsWingMirrorImage = it.VdhDefChkImgNswingMirror
                    dbDefectSheet!!.uploadNSWingMirrorImage = false

                    binding.ulNSWingMirrorImage.visibility = View.GONE
                }
                if (it.VdhDefChkImgOswingMirror.isNotBlank()) {
                    osWingMirrorImage = it.VdhDefChkImgOswingMirror
                    dbDefectSheet!!.uploadOSWingMirrorImage = false

                    binding.ulOSWingMirrorImage.visibility = View.GONE
                }
                if (it.VdhDefChkImgVan360Video.isNotBlank()) {
                    three60Video = it.VdhDefChkImgVan360Video
                    dbDefectSheet!!.uploadThreeSixtyVideo = false
                    binding.ulThreeSixtyVideo.visibility = View.GONE
                }

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthFrontNs,
                    binding.tyreDepthFrontImageUploadBtn,
                    binding.tyreDepthFrontImageFileName
                )

                setRadioCard2(
                    it.TyrePressureFrontNS,
                    binding.tyrePressureFrontFullRB,
                    binding.tyrePressureFrontBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthRearNs,
                    binding.tyreDepthRearImageUploadBtn,
                    binding.tyreDepthRearImageUploadFileName
                )

                setRadioCard2(
                    it.TyrePressureRearNS,
                    binding.tyrePressureRearNSFullRB,
                    binding.tyrePressureRearNSBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthRearOs,
                    binding.tyreDepthRearOSImageUploadBtn,
                    binding.tyreDepthRearOSFileNameTV
                )

                setRadioCard2(
                    it.TyrePressureRearOS,
                    binding.tyrePressureRearOSFULLRB,
                    binding.tyrePressureRearOSBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthFrontOs,
                    binding.tyreDepthFrontOSImageUploadBtn,
                    binding.tyreDepthFrontOSImageFilenameTV
                )

                setRadioCard2(
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

                if (selectedWindScreenConditionID > 0) {
                    binding.spinnerWindScreenCondition.setText(
                        windScreenConditionStatusNameList[windScreenConditionStatusNameId.indexOf(
                            selectedWindScreenConditionID
                        )]
                    )
                    binding.spinnerWindScreenCondition.setSelection(
                        windScreenConditionStatusNameId.indexOf(
                            selectedWindScreenConditionID
                        )
                    )
                }

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

                if (selectedOilLevelID > 0 && oilLevelIds.indexOf(selectedOilLevelID) != -1) {
                    binding.spinnerOilLevel.setText(
                        oilListNames[oilLevelIds.indexOf(
                            selectedOilLevelID
                        )]
                    )
                    binding.spinnerOilLevel.setSelection(oilLevelIds.indexOf(selectedOilLevelID))
                }


                if (selectedEngineCoolantLevelID > 0 && oilLevelIds.indexOf(
                        selectedEngineCoolantLevelID
                    ) != -1
                ) {
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

                if (selectedBreakFluidLevelID > 0 && oilLevelIds.indexOf(
                        selectedBreakFluidLevelID
                    ) != -1
                ) {
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

                if (selectedWindscreenWashingID > 0 && oilLevelIds.indexOf(
                        selectedWindscreenWashingID
                    ) != -1
                ) {
                    binding.spinnerWindscreenWashingLiquid.setText(
                        oilListNames[oilLevelIds.indexOf(
                            selectedWindscreenWashingID
                        )]
                    )


                    binding.spinnerWindscreenWashingLiquid.setSelection(
                        oilLevelIds.indexOf(
                            selectedWindscreenWashingID
                        )
                    )
                }

                setSpinner(binding.spinnerOilLevel, oilListNames, oilLevelIds)
                setSpinner(binding.spinnerEngineCoolant, oilListNames, oilLevelIds)
                setSpinner(binding.spinnerBrakeFluid, oilListNames, oilLevelIds)
                setSpinner(binding.spinnerWindscreenWashingLiquid, oilListNames, oilLevelIds)

            }
        }

        vm.otherImagesListLiveData.observe(this) {
            binding.pbX.visibility = View.GONE
            binding.otherImagesTV.visibility = View.VISIBLE
            binding.otherPictureUploadBtn.isEnabled = true
            if (it != null) {
                binding.ulotherPictureofPartsImage.visibility = View.GONE
                val otherFiles = it.map { it.FileName }
                setUploadCardOtherImages(
                    otherFiles.toMutableList(),
                    binding.otherPictureUploadBtn,
                    binding.otherImagesTV
                )
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
            tyreDepthFrontImageFileName.text = shortenFileName(vdhDefChkImgTyreThreadDepthFrontNs)
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

    private fun setUploadCardOtherImages(
        imagesString: MutableList<String>,
        tyreDepthFrontImageUploadBtn: AppCompatButton,
        tyreDepthFrontImageFileName: TextView
    ) {
        "Add More".also { tyreDepthFrontImageUploadBtn.text = it }
        var otherFiles = ""
        for (i in 0 until imagesString.size) {
            otherFiles += if (i != imagesString.size - 1)
                shortenFileName(uriToFileName(imagesString[i])) + "\n"
            else
                shortenFileName(uriToFileName(imagesString[i]))
        }

        tyreDepthFrontImageFileName.text = otherFiles
        tyreDepthFrontImageFileName.setTextColor(ContextCompat.getColor(this, R.color.blue_hex))
        tyreDepthFrontImageUploadBtn.backgroundTintList =
            ContextCompat.getColorStateList(this, R.color.greenBtn)
    }

    private fun setRadioCard(
        tyrePressureFrontNS: Int,
        tyrePressureFrontFullRB: RadioButton,
        tyrePressureFrontBelowRB: RadioButton
    ) {
        if (tyrePressureFrontNS == 1) {
            tyrePressureFrontFullRB.isChecked = true
            tyrePressureFrontBelowRB.isChecked = false
        } else if (tyrePressureFrontNS == -1) {
            tyrePressureFrontBelowRB.isChecked = false
            tyrePressureFrontFullRB.isChecked = false
        } else {
            tyrePressureFrontBelowRB.isChecked = true
            tyrePressureFrontFullRB.isChecked = false
        }
    }

    private fun setRadioCard2(
        tyrePressureFrontNS: Boolean?,
        tyrePressureFrontFullRB: RadioButton,
        tyrePressureFrontBelowRB: RadioButton
    ) {
        if (tyrePressureFrontNS == null) {
            tyrePressureFrontFullRB.isChecked = false
            tyrePressureFrontBelowRB.isChecked = false
        } else if (tyrePressureFrontNS) {
            tyrePressureFrontBelowRB.isChecked = false
            tyrePressureFrontFullRB.isChecked = true
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
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        } else {
            pickVideo()
        }
    }

    private fun pickVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    try {
                        contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }

                    selectedFileUri = it
                    when (imageMode) {
                        0 -> {
                            dbDefectSheet?.tyreDepthFrontNSImage = selectedFileUri.toString()
                            tyreThreadDepthFrontNS = selectedFileUri.toString()
                            dbDefectSheet!!.uploadTyreDepthFrontNSImage = true
                            setUploadCardBtn2(
                                dbDefectSheet!!.tyreDepthFrontNSImage!!,
                                binding.tyreDepthFrontImageUploadBtn,
                                binding.tyreDepthFrontImageFileName
                            )
                        }

                        1 -> {
                            dbDefectSheet?.tyreDepthRearNSImage = selectedFileUri.toString()
                            tyreThreadDepthRearNS = selectedFileUri.toString()
                            dbDefectSheet!!.uploadTyreDepthRearNSImage = true
                            setUploadCardBtn2(
                                dbDefectSheet!!.tyreDepthRearNSImage!!,
                                binding.tyreDepthRearImageUploadBtn,
                                binding.tyreDepthRearImageUploadFileName
                            )
                        }

                        2 -> {
                            dbDefectSheet?.tyreDepthRearOSImage = selectedFileUri.toString()
                            tyreThreadDepthRearOS = selectedFileUri.toString()
                            dbDefectSheet!!.uploadTyreDepthRearOSImage = true
                            setUploadCardBtn2(
                                dbDefectSheet!!.tyreDepthRearOSImage!!,
                                binding.tyreDepthRearOSImageUploadBtn,
                                binding.tyreDepthRearOSFileNameTV
                            )
                        }

                        3 -> {
                            dbDefectSheet?.tyreDepthFrontOSImage = selectedFileUri.toString()
                            tyreThreadDepthFrontOS = selectedFileUri.toString()
                            dbDefectSheet!!.uploadTyreDepthFrontOSImage = true
                            setUploadCardBtn2(
                                dbDefectSheet!!.tyreDepthFrontOSImage!!,
                                binding.tyreDepthFrontOSImageUploadBtn,
                                binding.tyreDepthFrontOSImageFilenameTV
                            )
                        }

                        4 -> {
                            dbDefectSheet?.engineLevelImage = selectedFileUri.toString()
                            engineOilLevelImage = selectedFileUri.toString()
                            dbDefectSheet!!.uploadEngineLevelImage = true
                            setUploadCardBtn2(
                                dbDefectSheet!!.engineLevelImage!!,
                                binding.engineOilImageUploadBtn,
                                binding.engineOilImageUploadFileName
                            )
                        }

                        5 -> {
                            dbDefectSheet?.addBlueLevelImage = selectedFileUri.toString()
                            addBlueLevelImage = selectedFileUri.toString()
                            dbDefectSheet!!.uploadAddBlueLevelImage = true
                            setUploadCardBtn2(
                                dbDefectSheet!!.addBlueLevelImage!!,
                                binding.addBlueLevelUploadBtn,
                                binding.addBlueLevelUploadFileName
                            )
                        }

                        6 -> {
                            dbDefectSheet?.nsWingMirrorImage = selectedFileUri.toString()
                            nsWingMirrorImage = selectedFileUri.toString()
                            dbDefectSheet!!.uploadNSWingMirrorImage = true
                            setUploadCardBtn2(
                                dbDefectSheet!!.nsWingMirrorImage!!,
                                binding.nsWingMirrorUploadBtn,
                                binding.nsWingMirrorUploadFileName
                            )
                        }

                        7 -> {
                            dbDefectSheet?.osWingMirrorImage = selectedFileUri.toString()
                            osWingMirrorImage = selectedFileUri.toString()
                            dbDefectSheet!!.uploadOSWingMirrorImage = true
                            setUploadCardBtn2(
                                dbDefectSheet!!.osWingMirrorImage!!,
                                binding.osWingMirrorUploadBtn,
                                binding.osWingMirrorUploadFileName
                            )
                        }

                        8 -> {

                            /*               this.contentResolver.releasePersistableUriPermission(
                                               selectedFileUri.toString().toUri(), FLAG_GRANT_READ_URI_PERMISSION
                                           );*/
                            dbDefectSheet?.threeSixtyVideo = selectedFileUri.toString()
                            dbDefectSheet!!.uploadThreeSixtyVideo = true
                            three60Video = selectedFileUri.toString()
                            setUploadCardBtn2(
                                dbDefectSheet!!.threeSixtyVideo!!,
                                binding.Three60VideoUploadBtn,
                                binding.Three60VideoFileNameTV
                            )
                        }

                        9 -> {
                            otherImagesList.add(selectedFileUri.toString())
                            dbDefectSheet!!.uploadOtherImages = true
                            setUploadCardOtherImages(
                                otherImagesList,
                                binding.otherPictureUploadBtn,
                                binding.otherImagesTV
                            )
                        }

                        else -> {
                            showToast("Wrong Selection", this)
                        }
                    }
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
//        isfirst = Prefs.getInstance(this).Isfirst
//        startonetime = isfirst

//        Log.e("newinspection", "onCreate: " + Prefs.getInstance(App.instance).vehinspection)
        prefs?.Isfirst=true
        startonetime = prefs?.Isfirst!!
        binding.btStart.setOnClickListener {
            startInspection()
        }
    }

    private fun startInspection() {
        clientUniqueID()


        if (cqSDKInitializer.isCQSDKInitialized()) {

            Log.e("sdkskdkdkskdkskd", "onCreateView " + inspectionreg)

            try {
                loadingDialog.show()
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
                            loadingDialog.dismiss()
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
                loadingDialog.dismiss()
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
        loadingDialog.dismiss()
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
                  //  Toast.makeText(this, "Inspection saved", Toast.LENGTH_SHORT).show()
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

        }

        else {
            Prefs.getInstance(App.instance).Isfirst = true
            startonetime = Prefs.getInstance(App.instance).Isfirst

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


        val workManager = WorkManager.getInstance(this)
        val workRequestId = uploadWorkRequest.id
        workManager.enqueue(uploadWorkRequest)

        Prefs.getInstance(this).saveWorkRequestId(workRequestId.toString())

        Toast.makeText(
            this@SubmitWeeklyDefectActivity,
            "Data is saved successfully.",
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
                        Log.e("completed", "isWorkfailed: ")
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

    private fun canStartNewWork(): Boolean {
        val lastWorkRequestId = Prefs.getInstance(this).getWorkRequestId()

        return if (lastWorkRequestId != null) {
            val workManager = WorkManager.getInstance(this)
            val workInfo = workManager.getWorkInfoById(UUID.fromString(lastWorkRequestId)).get()
            workInfo.state.isFinished
        } else {
            true
        }
    }
}

class SyncWorker(
    context: Context, workerParameters: WorkerParameters
) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        // Perform the sync work here
        // ...
        return Result.success()
    }
}



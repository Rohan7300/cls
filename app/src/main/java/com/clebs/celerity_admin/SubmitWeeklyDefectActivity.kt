package com.clebs.celerity_admin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.clebs.celerity_admin.database.CheckInspection
import com.clebs.celerity_admin.database.DefectSheet
import com.clebs.celerity_admin.databinding.ActivitySubmitWeeklyDefectBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.App
import com.clebs.celerity_admin.utils.DependencyClass.currentWeeklyDefectItem
import com.clebs.celerity_admin.utils.getMimeType
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.viewModels.MainViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class SubmitWeeklyDefectActivity : AppCompatActivity() {
    lateinit var binding: ActivitySubmitWeeklyDefectBinding
    private lateinit var vm: MainViewModel
    private var selectedOilLevelID: Int = -1
    private var selectedEngineCoolantLevelID: Int = -1
    private var selectedBreakFluidLevelID: Int = -1
    private var selectedWindscreenWashingID: Int = -1
    private var selectedWindScreenConditionID: Int = -1
    private lateinit var oilListNames: List<String>
    private var selectedFileUri: Uri? = null
    private lateinit var oilLevelIds: List<Int>
    lateinit var filePart: MultipartBody.Part
    var imageMode = -1
    var dbDefectSheet: DefectSheet?=null

    companion object {
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

        if (currentWeeklyDefectItem != null)
            vm.GetWeeklyDefectCheckImages(currentWeeklyDefectItem!!.vdhCheckId)


        dbDefectSheet = App.offlineSyncDB?.getDefectSheet(
            currentWeeklyDefectItem!!.vdhCheckId
        )


        if(dbDefectSheet==null){
            lifecycleScope.launch{
                App.offlineSyncDB?.insertOrUpdate(
                    DefectSheet(
                        id =  currentWeeklyDefectItem!!.vdhCheckId
                    ))

                dbDefectSheet = App.offlineSyncDB?.getDefectSheet(currentWeeklyDefectItem!!.vdhCheckId)
            }
        }

        observers()
        clickListeners()
    }

    private fun clickListeners() {
        binding.tyreDepthFrontImageUploadBtn.setOnClickListener {
            addImage(0)
        }

        binding.tyreDepthRearImageUploadBtn.setOnClickListener {
            addImage(1)
        }

        binding.tyreDepthRearOSImageUploadBtn.setOnClickListener {
            addImage(3)
        }

        binding.tyreDepthRearOSImageUploadBtn.setOnClickListener {
            addImage(4)
        }

        binding.tyreDepthFrontOSImageUploadBtn.setOnClickListener {
            addImage(5)
        }

        binding.engineOilImageUploadBtn.setOnClickListener {
            addImage(6)
        }

        binding.addBlueLevelUploadBtn.setOnClickListener {
            addImage(7)
        }

        binding.nsWingMirrorUploadBtn.setOnClickListener {
            addImage(8)
        }

        binding.osWingMirrorUploadBtn.setOnClickListener {
            addImage(9)
        }

        binding.Three60VideoUploadBtn.setOnClickListener {

        }

        binding.otherPictureUploadBtn.setOnClickListener {

        }


    }

    private fun addImage(mode: Int) {
        imageMode = mode
        if (allPermissionsGranted()) {
            upload()
        } else {
            requestPermissions()
        }
    }

    private fun observers() {
        vm.lDGetWeeklyDefectCheckImages.observe(this) {
            if (it != null) {
                selectedOilLevelID = it.VdhDefChkImgOilLevelId
                selectedEngineCoolantLevelID = it.EngineCoolantLevelId
                selectedBreakFluidLevelID = it.BrakeFluidLevelId
                selectedWindscreenWashingID = it.WindowScreenWashingLiquidId
                selectedWindScreenConditionID = it.WindScreenConditionId
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

                Log.d(
                    "Selections", "indexes ${oilLevelIds.indexOf(selectedOilLevelID)}" +
                            "\n${oilLevelIds.indexOf(selectedEngineCoolantLevelID)}" +
                            "\n${oilLevelIds.indexOf(selectedBreakFluidLevelID)}" +
                            "\n${oilLevelIds.indexOf(selectedWindscreenWashingID)}"
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

                if (selectedWindscreenWashingID > 0) {
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
        } else {
            tyrePressureFrontBelowRB.isChecked = true
        }
    }

    private fun setSpinner(
        spinner: AutoCompleteTextView,
        items: List<String>,
        ids: List<Int>
    ) {
        val itemsList = mutableListOf<String>()
        Log.d("ID", "$ids")
        itemsList.addAll(items)
        val adapter =
            ArrayAdapter(this, R.layout.dropdown_menu_popup_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.setAdapter(adapter)
        spinner.setOnItemClickListener { parent, _, position, _ ->
            run {
                parent?.let { nonNullParent ->

                    val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                    selectedItem.let {
                        /*                  when (spinner) {
                                              *//*                     binding.spinnerRouteType -> {
                                                     selectedRouteType = selectedItem
                                                     selectedRouteId = ids[position]
                                                 }*//*
                        }*/
                    }

                }
            }
        }
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

    fun upload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
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
//dbDefectSheet.tyreDepthFrontNSImage
                        }

                        1 -> {

                        }

                        2 -> {

                        }

                        3 -> {

                        }

                        4 -> {

                        }

                        5 -> {

                        }

                        6 -> {

                        }

                        7 -> {

                        }

                        8 -> {

                        }

                        9 -> {

                        }
                    }
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
                        "UploadTicketDoc",
                        selectedFileUri!!.lastPathSegment + "." + (fileExtension ?: "jpg"),
                        requestBody
                    )
                    //save()
                }
            } else {
                finish()
                showToast("Attachment not selected!!", this)
            }
        }

}
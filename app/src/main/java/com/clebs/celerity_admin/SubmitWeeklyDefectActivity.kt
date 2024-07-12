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
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.clebs.celerity_admin.database.CheckInspection
import com.clebs.celerity_admin.database.DefectSheet
import com.clebs.celerity_admin.database.IsInspectionDone
import com.clebs.celerity_admin.databinding.ActivitySubmitWeeklyDefectBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.SaveInspectionRequestBody
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.App
import com.clebs.celerity_admin.utils.DependencyClass.currentWeeklyDefectItem
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.getMimeType
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.utils.toast
import com.clebs.celerity_admin.viewModels.MainViewModel
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
    var dbDefectSheet: DefectSheet? = null
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var regexPattern: Regex
    private var inspectionID = String()
    private var VdhCheckDaId = String()
    private var VdhCheckVmId = String()
    private var VehCheckLmId = String()
    private var VdhCheckWeekNo = String()
    private var vdhCheckId = String()
    private var VdhCheckYearNo = String()
    private var startonetime: Boolean? = false
    private var inspectionreg: String? = null
    private var isfirst: Boolean? = false

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
        cqCode()
        vm.GetVehWeeklyDefectSheetInspectionInfo(
            Prefs.getInstance(App.instance).vdhCheckId.toString().toInt()
        )
        vm.isinspectiondonelivedata.observe(this, Observer {
            if (it != null) {
                if (it.isInspectionDone) {
                    binding.llmain.visibility = View.VISIBLE
                    binding.llstart.visibility = View.VISIBLE
                    binding.tvInspection.setText("OSM Vehicle Inspection Completed")
                    binding.llstart.strokeColor = ContextCompat.getColor(this,R.color.green)
                    binding.tvInspection.setTextColor(ContextCompat.getColor(this,R.color.green))
                    binding.btStart.visibility = View.GONE
                    binding.done.visibility = View.VISIBLE
                    Glide.with(this).load(R.raw.dones).into(binding.done)

                }
            } else {
                binding.llmain.visibility = View.GONE
                binding.btStart.visibility = View.VISIBLE
                binding.llstart.setStrokeColor(ContextCompat.getColor(this,R.color.very_very_light_red))
                binding.tvInspection.setTextColor(ContextCompat.getColor(this,R.color.text_color))
                binding.tvInspection.setText("Start OSM Inspection *")
                binding.done.visibility = View.GONE
                binding.llstart.visibility = View.VISIBLE
            }


        })

        if (currentWeeklyDefectItem != null) vm.GetWeeklyDefectCheckImages(currentWeeklyDefectItem!!.vdhCheckId)


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
                    "Selections", "indexes ${oilLevelIds.indexOf(selectedOilLevelID)}" + "\n${
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

    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        var permissionGranted = true
        permissions.entries.forEach {
            if (it.key in REQUIRED_PERMISSIONS && it.value == false) permissionGranted = false
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

    private fun cqCode() {
        cqSDKInitializer = CQSDKInitializer(this)

        if (intent.hasExtra("vdhCheckId")) {
            vdhCheckId = intent.getIntExtra("vdhCheckId", 0).toString()
            Prefs.getInstance(App.instance).vdhCheckId = vdhCheckId
        }
        if (intent.hasExtra("VdhCheckDaId")) {
            VdhCheckDaId = intent.getIntExtra("VdhCheckDaId", 0).toString()
            Prefs.getInstance(App.instance).VdhCheckDaId = VdhCheckDaId
        }
        if (intent.hasExtra("VdhCheckVmId")) {
            VdhCheckVmId = intent.getIntExtra("VdhCheckVmId", 0).toString()
            Prefs.getInstance(App.instance).VdhCheckVmId = VdhCheckVmId
        }

        if (intent.hasExtra("VdhCheckWeekNo")) {
            VdhCheckWeekNo = intent.getIntExtra("VdhCheckWeekNo", 0).toString()
            Prefs.getInstance(App.instance).VdhCheckWeekNo = VdhCheckWeekNo
        }
        if (intent.hasExtra("VehCheckLmId")) {
            VehCheckLmId = intent.getIntExtra("VehCheckLmId", 0).toString()
            Prefs.getInstance(App.instance).VehCheckLmId = VehCheckLmId
        }
        if (intent.hasExtra("VdhCheckYearNo")) {
            VdhCheckYearNo = intent.getIntExtra("VdhCheckYearNo", 0).toString()
            Prefs.getInstance(App.instance).VdhCheckYearNo = VdhCheckYearNo
        }














        if (intent.hasExtra("regno")) {

            inspectionreg = intent?.getStringExtra("regno")?.replace(" ", "")
            Prefs.getInstance(App.instance).vehinspection = inspectionreg.toString()
        }
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
                        regNumber = inspectionreg?.replace(" ", ""), //if sent, user can't edit
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
                    Prefs.getInstance(App.instance).vdhCheckId.toInt(),
                    Prefs.getInstance(
                        App.instance
                    ).vehinspectionUniqueID.replace(" ", ""),
                    Prefs.getInstance(App.instance).VdhCheckDaId.toInt(),
                    Prefs.getInstance(App.instance).VdhCheckVmId.toInt(),
                    Prefs.getInstance(App.instance).VdhCheckWeekNo.toInt(),
                    Prefs.getInstance(App.instance).VdhCheckYearNo.toInt(),
                    Prefs.getInstance(App.instance).VehCheckLmId.toInt()
                )
            )


            vm.saveinspectionlivedata.observe(this, Observer {
                if (it?.Status == "200") {
                    vm.GetVehWeeklyDefectSheetInspectionInfo(
                        Prefs.getInstance(App.instance).vdhCheckId.toString().toInt()
                    )
                    Toast.makeText(this, "Inspection saved", Toast.LENGTH_SHORT).show()
                    vm.isinspectiondonelivedata.observe(this, Observer {
                        if (it!=null){

                            if (it.isInspectionDone){

                                binding.llmain.visibility = View.VISIBLE
                                binding.llstart.visibility = View.VISIBLE
                                binding.tvInspection.setText("OSM Vehicle Inspection Completed")
                                binding.llstart.strokeColor = ContextCompat.getColor(this,R.color.green)
                                binding.tvInspection.setTextColor(ContextCompat.getColor(this,R.color.green))
                                binding.btStart.visibility = View.GONE
                                binding.done.visibility = View.VISIBLE
                                Glide.with(this).load(R.raw.dones).into(binding.done)
                            }
                        }
                        else{
                            binding.llmain.visibility = View.GONE
                            binding.btStart.visibility = View.VISIBLE
                            binding.llstart.setStrokeColor(ContextCompat.getColor(this,R.color.very_very_light_red))
                            binding.tvInspection.setTextColor(ContextCompat.getColor(this,R.color.text_color))
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

}
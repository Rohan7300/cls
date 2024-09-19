package com.clebs.celerity.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityBreakdownInspectionBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.models.CompleteDriverVehicleBreakDownInspectionRequest
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.breakDownInspectionImageStage
import com.clebs.celerity.utils.DependencyProvider.currentBreakDownItemforInspection
import com.clebs.celerity.utils.DependencyProvider.isBreakDownItemInitialize
import com.clebs.celerity.utils.DependencyProvider.isComingBackFromBreakDownActivity
import com.clebs.celerity.utils.ImageTakerActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.clientUniqueIDForBreakDown
import com.clebs.celerity.utils.dateOnFullFormat
import com.clebs.celerity.utils.dateToday
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.startUploadWithWorkManager
import com.clebs.celerity.utils.visible
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import io.clearquote.assessment.cq_sdk.singletons.PublicConstants
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Date

class BreakDownInspectionActivity : AppCompatActivity() {
    lateinit var binding: ActivityBreakdownInspectionBinding
    lateinit var vm: MainViewModel
    lateinit var loadingDialog: LoadingDialog
    lateinit var prefs: Prefs
    private var cqOpened = false
    private var crrRegNo: String = ""
    private var isVehInspectionDone = false
    private var breakDownFuelLevelId = -1
    private var breakDownVehOilLevelId = -1
    private var sdkKey = ""
    private lateinit var cqSDKInitializer: CQSDKInitializer

    companion object {
        var TAG = "BreakInspectionActivity"

    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sdkKey = ContextCompat.getString(this, R.string.cqsdk_osm_key)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_breakdown_inspection)
        breakDownInspectionImageStage = 0
        vm = DependencyProvider.getMainVM(this)
        prefs = Prefs.getInstance(this)
        cqSDKInitializer()
        loadingDialog = LoadingDialog(this)
        crrRegNo = prefs.scannedVmRegNo
        prefs.isBreakDownGenerated = true
        if (!isBreakDownItemInitialize()) {
            showToast("BreakDown Data is not received", this)
        } else {
            if (currentBreakDownItemforInspection.VmRegNo != prefs.scannedVmRegNo) {
                showToast(
                    "Scanned RegNo is different from BreakDown Vehicle Inspection RegNo",
                    this
                )
                crrRegNo = currentBreakDownItemforInspection.VmRegNo
            }
        }
        cqSDKInitializer = CQSDKInitializer(this)
        uiOne()
        clickListeners()
        prefs.currBreakDownInspectionId = currentBreakDownItemforInspection.VehInspId
        loadingDialog.show()
        vm.GetVehiclefuelListing().observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                val fuelIds = arrayListOf<Int>()
                val fuelTypes = arrayListOf<String>()
                it.map { fuel ->
                    if (fuel != null) {
                        if (fuel.vehFuelLevelId != null && fuel.vehFuelLevelName != null) {
                            fuelIds.add(fuel.vehFuelLevelId)
                            fuelTypes.add(fuel.vehFuelLevelName)
                        }
                    }
                }
                setSpinner(
                    binding.spinnerVehicleFuelLevel,
                    fuelTypes,
                    fuelIds
                )
            }
        }

        vm.GetVehicleOilListing().observe(this) {
            if (it != null) {
                val oilIds = arrayListOf<Int>()
                val oilNames = arrayListOf<String>()
                it.map { oils ->
                    if (oils.vehOilLevelId != null && oils.vehOilLevelName != null) {
                        oilIds.add(oils.vehOilLevelId)
                        oilNames.add(oils.vehOilLevelName)
                    }
                }
                setSpinner(
                    binding.spinnerVehicleOilLevel, oilNames, oilIds
                )
            }
        }
    }

    fun uiOne() {
        binding.bodyVehicleInfo.visibility = View.VISIBLE
        binding.submitBtn.visibility = View.GONE
        binding.secondLayout.visibility = View.GONE
        binding.secondCard.visibility = View.GONE
    }

    fun uiSecond() {
        binding.bodyVehicleInfo.visibility = View.GONE
        binding.submitBtn.visibility = View.VISIBLE
        binding.secondCard.visibility = View.VISIBLE
        binding.secondLayout.visibility = View.VISIBLE
    }

    fun clickListeners() {
        binding.startinspectionBtn.setOnClickListener {
            isVehInspectionDone = true
            prefs.isBreakDownInspectionDone = true
            startInspection()
        }
        binding.addImagesBtn.setOnClickListener {
            openCamera()
        }
        binding.submitBtn.setOnClickListener {
            if (validate()) {
                loadingDialog.show()
                val request = CompleteDriverVehicleBreakDownInspectionRequest(
                    AddBlueMileage = binding.atvAddBlueMileage.text.toString(),
                    DaVehImgId = 0,
                    DriverId = prefs.clebUserId.toInt(),
                    FuelLevelId = breakDownFuelLevelId,
                    InspectionId = currentBreakDownItemforInspection.VehInspId,
                    IsVehBreakDownInspectionDone = true,
                    OilLevelId = breakDownVehOilLevelId,
                    SupervisorId = 0,
                    VehCurrentMileage = binding.atvVehicleCurrentMileage.text.toString(),
                    VehInspectionDoneById = prefs.clebUserId.toInt(),
                    VehInspectionDoneOn = dateOnFullFormat(),
                    ClientRefId = prefs.inspectionIDForBreakDown,
                    VmId = currentBreakDownItemforInspection.VehInspVmId
                )
                vm.CompleteDriverVehicleBreakDownInspection(request).observe(this) {
                    if (it != null) {
                        showToast("Inspection Completed", this@BreakDownInspectionActivity)
                        prefs.storeBreakDownInspectionTime()
                        startUploadWithWorkManager(4, prefs, this@BreakDownInspectionActivity)
                        lifecycleScope.launch {
                            delay(20000)

                            loadingDialog.dismiss()
                            isComingBackFromBreakDownActivity = true
                            finish()
                        }

                    } else {
                        loadingDialog.dismiss()
                        showToast("Inspection Failed to submit", this@BreakDownInspectionActivity)
                    }
                }
            }
        }
        binding.headVehicleInformation.setOnClickListener {
            binding.bodyVehicleInfo.isVisible = !binding.bodyVehicleInfo.isVisible
            if (binding.bodyVehicleInfo.isVisible) {
                binding.headerOneIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.dropup
                    )
                )
            } else {
                binding.headerOneIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.dropdown
                    )
                )
            }
        }
        binding.headerAddInspectionImages.setOnClickListener {
            binding.secondLayout.isVisible = !binding.secondLayout.isVisible
            if (binding.secondLayout.isVisible) {
                binding.headerTwoIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.dropup
                    )
                )
            } else {
                binding.headerTwoIcon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.dropdown
                    )
                )
            }
        }
        binding.nextBtn.setOnClickListener {
            if (validateOne())
                uiSecond()

        }
    }

    private fun validate(): Boolean {

        if (binding.atvVehicleCurrentMileage.text.isNullOrBlank()) {
            showToast("Add Vehicle Mileage Before Submitting!!", this)
            return false
        }
        if (binding.atvAddBlueMileage.text.isNullOrBlank()) {
            showToast("Add Vehicle Add Blue Mileage Before Submitting", this)
            return false
        }
        if (breakDownFuelLevelId == -1) {
            showToast("Select Veh Fuel Level Before Submitting!!", this)
            return false
        }
        if (breakDownVehOilLevelId == -1) {
            showToast("Select Veh Oil Level Before Submitting!!", this)
            return false
        }
        if (!isVehInspectionDone) {
            showToast("Please Complete Vehicle Inspection!!", this)
            return false
        }
        listOf(
            prefs.breakDownSpareWheelUri,
            prefs.breakDownLoadingInteriorUri,
            prefs.breakDownLoadingInteriorUri,
            prefs.breakDownToolsPictureUri,
            prefs.breakDownToolsPictureUri
        ).forEach {
            if (it.isNullOrBlank()) {
                showToast("Please add all the Vehicle Pictures", this)
                return false
            }
        }

        return true
    }

    private fun validateOne(): Boolean {

        if (binding.atvVehicleCurrentMileage.text.isNullOrBlank()) {
            showToast("Add Vehicle Mileage Before Next Step!!", this)
            return false
        }
        if (binding.atvAddBlueMileage.text.isNullOrBlank()) {
            showToast("Add Vehicle Add Blue Mileage Before Next Step", this)
            return false
        }
        if (breakDownFuelLevelId == -1) {
            showToast("Select Veh Fuel Level Before Next Step!!", this)
            return false
        }
        if (breakDownVehOilLevelId == -1) {
            showToast("Select Veh Oil Level Before Next Step!!", this)
            return false
        }

        return true
    }

    private fun setSpinner(
        spinner: AutoCompleteTextView,
        items: List<String>,
        ids: List<Int>
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
                            binding.spinnerVehicleFuelLevel -> {
                                breakDownFuelLevelId = ids[position]
                            }

                            binding.spinnerVehicleOilLevel -> {
                                breakDownVehOilLevelId = ids[position]
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startInspection() {
        cqOpened = true
        Log.d("ISOFFLine", "${!Prefs.getInstance(App.instance).returnInspectionFirstTime!!}")
        if (crrRegNo.isNotBlank()) {
            clientUniqueIDForBreakDown()
            if (cqSDKInitializer.isCQSDKInitialized()) {
                try {

                    loadingDialog.show()
                    cqSDKInitializer.startInspection(activity = this, clientAttrs = ClientAttrs(
                        userName = " ",
                        dealer = " ",
                        dealerIdentifier = " ",
                        client_unique_id = App.prefs!!.inspectionIDForBreakDown

                        //drivers ID +vechile iD + TOdays date dd// mm //yy::tt,mm
                    ), inputDetails = InputDetails(
                        vehicleDetails = VehicleDetails(
                            /*           regNumber = crrRegNo.replace(
                                           " ", ""
                                       ),*/ //if sent, user can't edit
                            regNumber = "ND22YFL",
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
                        isOffline = !Prefs.getInstance(App.instance).returnInspectionFirstTime!!,
                        skipInputPage = true, // true, Inspection will be started with camera page | false, Inspection will be started
                    ),

                        result = { isStarted, msg, code ->
                            loadingDialog.dismiss()
                            Log.e(TAG, "startInspection: $msg $code")
                            if (isStarted) {
                                Prefs.getInstance(App.instance).returnInspectionFirstTime = false
                                Log.d(TAG, "isStarted $msg")
                            } else {

                                if (msg == "Online quote can not be created without internet") {
                                    showToast(
                                        "Please Turn on the internet", this
                                    )
                                    Log.d(TAG, "CQ: Not isStarted1  $msg")
                                } else if (msg == "Sufficient data not available to create an offline quote") {
                                    Prefs.getInstance(App.instance).returnInspectionFirstTime = true
                                    showToast(
                                        "Please Turn on the internet & grant required permissions.",
                                        this
                                    )
                                    Log.d(TAG, "CQSDKXX : Not isStarted2  $msg")
                                } else if (msg == "Unable to download setting updates, Please check internet") {
                                    showToast(
                                        "Please Turn on the internet", this
                                    )
                                    Log.d(TAG, "CQSDKXX : Not isStarted3  $msg")
                                } else if (msg == "Vehicle not in fleet list") {
                                    showToast(
                                        "Vehicle not in fleet list", this
                                    )
                                    Log.d(TAG, "CQSDKXX : Not isStarted4  $msg")
                                }
                                Log.d(TAG, "CQSDKXX : Not isStarted5 $msg")
                            }
                            if (msg == "Success") {
                                Log.d(TAG, "CQSDKXX : Success $msg")
                            } else {

                                Log.d(TAG, "CQSDKXX : Not Success $msg")
                            }
                            if (!isStarted) {
                                Log.e(TAG, "started inspection : onCreateView: $msg $isStarted")
                            }
                        })
                } catch (_: Exception) {
                    loadingDialog.dismiss()
                }
            }
        } else {
            Toast.makeText(this, "Vehicle RegNo not found!!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera() {
        if (allPermissionsGranted()) {
            launchCamera()
        } else {
            requestPermissions()
        }
    }

    private fun launchCamera() {
        val imageTakerActivityIntent = Intent(this, ImageTakerActivity::class.java)
        resultLauncher.launch(imageTakerActivityIntent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val outputUri = data?.getStringExtra("outputUri")
                if (outputUri != null) {
                    when (breakDownInspectionImageStage) {
                        0 -> {
                            prefs.breakDownSpareWheelUri = outputUri
                            breakDownInspectionImageStage = 1
                            updateImageUi()
                        }

                        1 -> {
                            prefs.breakDownVehicleInteriorUri = outputUri
                            breakDownInspectionImageStage = 2
                            updateImageUi()
                        }

                        2 -> {
                            prefs.breakDownLoadingInteriorUri = outputUri
                            breakDownInspectionImageStage = 3
                            updateImageUi()
                        }

                        3 -> {
                            prefs.breakDownToolsPictureUri = outputUri
                            breakDownInspectionImageStage = 4
                            updateImageUi()
                        }

                        4 -> {
                            prefs.breakDownVinNumberPictureUri = outputUri
                            breakDownInspectionImageStage = 5
                            updateImageUi()
                        }

                    }
                } else {
                    showToast("Failed to fetch image!!", this)
                }
            } else {
                showToast("Failed!!", this)
            }
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
                launchCamera()
            }
        }

    private fun updateImageUi() {
        val drawable =
            ContextCompat.getDrawable(this@BreakDownInspectionActivity, R.drawable.warning2)
        val yesDrawable =
            ContextCompat.getDrawable(this@BreakDownInspectionActivity, R.drawable.ic_yes3)

        binding.run {
            listOf(
                indicatorLoadingInterior,
                indicatorSpareWheel,
                indicatorToolsPicture,
                indicatorVinNumber,
                indicatorVehicleInterior
            ).onEach {
                it.setImageDrawable(
                    drawable
                )
            }
            when (breakDownInspectionImageStage) {
                1 -> {
                    indicatorSpareWheel.setImageDrawable(
                        yesDrawable
                    )
                }

                2 -> {
                    listOf(
                        indicatorSpareWheel, indicatorVehicleInterior
                    ).onEach { it.setImageDrawable(yesDrawable) }
                }

                3 -> {
                    listOf(
                        indicatorSpareWheel, indicatorVehicleInterior, indicatorLoadingInterior
                    ).onEach { it.setImageDrawable(yesDrawable) }
                }

                4 -> {
                    listOf(
                        indicatorSpareWheel,
                        indicatorVehicleInterior,
                        indicatorLoadingInterior,
                        indicatorToolsPicture
                    ).onEach { it.setImageDrawable(yesDrawable) }
                }

                5 -> {
                    listOf(
                        indicatorSpareWheel,
                        indicatorVehicleInterior,
                        indicatorLoadingInterior,
                        indicatorToolsPicture,
                        indicatorVinNumber
                    ).onEach { it.setImageDrawable(yesDrawable) }
                }

                else -> {

                }
            }
        }

    }

    private fun cqSDKInitializer() {
        cqSDKInitializer = CQSDKInitializer(this)
        prefs.Isfirst = true
        cqSDKInitializer.triggerOfflineSync()

        Log.e("OSMSDK intialized", "cqSDKInitializer: ")
        cqSDKInitializer.initSDK(
            sdkKey = sdkKey, result = { isInitialized, code, _ ->
                if (isInitialized && code == PublicConstants.sdkInitializationSuccessCode) {
                    Prefs.getInstance(applicationContext).saveOSMCQSdkKey(sdkKey)
                } else {
                    showToast("OSMSDK: Error initializing SDK", this)
                }
            })
    }
}
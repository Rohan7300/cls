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
import com.clebs.celerity_admin.databinding.ActivitySubmitWeeklyDefectBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DependencyClass.currentWeeklyDefectItem
import com.clebs.celerity_admin.utils.getMimeType
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.viewModels.MainViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class SubmitWeeklyDefectActivity : AppCompatActivity() {
    lateinit var binding: ActivitySubmitWeeklyDefectBinding
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var regexPattern: Regex
    private lateinit var inspectionID: String
    private var startonetime: Boolean? = false
    private var inspectionreg: String? = null

    private var isfirst: Boolean? = false
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
                        " ",
                        ""
                    ),
                    InspectionClientUniqueID = Prefs.getInstance(App.instance).vehinspectionUniqueID.replace(
                        " ",
                        ""
                    )
                )
            )

        } else {

            Log.d("hdhsdshdsdjshhsds", "else $tempCode $message")
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubmitWeeklyDefectBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cqSDKInitializer = CQSDKInitializer(this)

        if (intent.hasExtra("regno")) {

            inspectionreg = intent?.getStringExtra("regno")?.replace(" ", "")
            Prefs.getInstance(App.instance).vehinspection = inspectionreg.toString()
        }
        val inspectionInfo = App.offlineSyncDB!!.getInspectionInfo()
        Log.e("result4", "onCreate: " + inspectionInfo)
        if (!App.offlineSyncDB!!.isInspectionTableEmpty()) {


            inspectionInfo.forEach {
                if (Prefs.getInstance(App.instance).vehinspection == it.InspectionDoneRegNo) {
                    binding.llmain.visibility = View.VISIBLE
                    binding.llstart.visibility = View.VISIBLE
                    binding.tvInspection.setText("OSM Vehicle Inspection Completed")
                    binding.btStart.visibility = View.GONE
                    binding.done.visibility = View.VISIBLE
                    Glide.with(this).load(R.raw.dones).into(binding.done)

                } else {
                    Log.e("result3", "onCreate: ")
                    binding.llmain.visibility = View.GONE
                    binding.btStart.visibility = View.VISIBLE
                    binding.tvInspection.setText("Start OSM Inspection *")
                    binding.done.visibility = View.GONE
                    binding.llstart.visibility = View.VISIBLE
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

        isfirst = Prefs.getInstance(this).Isfirst
        startonetime = isfirst

        Log.e("newinspection", "onCreate: " + Prefs.getInstance(App.instance).vehinspection)


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

                        }
                        if (!isStarted) {
                            Log.e("startedinspection", "onCreateView: $msg$isStarted")
                        }
                    })
            } catch (_: Exception) {


            }
        }

    }


    fun clientUniqueID(): String {
        val x = "123456"
        val y = "123456"
        // example string
        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))

        regexPattern = Regex("${x.take(3)}${y.take(3)}${formattedDate}")

                    val fileExtension = getMimeType(selectedFileUri!!)?.let { mimeType ->
                        MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                    }

        inspectionID = regexPattern.toString()
        Prefs.getInstance(App.instance).vehinspectionUniqueID = inspectionID
        return regexPattern.toString()
        Log.e("resistrationvrnpatterhn", "clientUniqueID: " + inspectionID)
    }
}
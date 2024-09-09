package com.clebs.celerity.ui

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityBreakdownInspectionBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.currentBreakDownItemforInspection
import com.clebs.celerity.utils.DependencyProvider.isBreakDownItemInitialize
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.clientUniqueIDForBreakDown
import com.clebs.celerity.utils.showToast
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails

class BreakDownInspectionActivity: AppCompatActivity() {
    lateinit var binding:ActivityBreakdownInspectionBinding
    lateinit var vm: MainViewModel
    lateinit var loadingDialog: LoadingDialog
    lateinit var prefs: Prefs
    private var cqOpened = false
    private var crrRegNo: String = ""

    var selectedFuelId = -1
    private lateinit var cqSDKInitializer: CQSDKInitializer
    companion object{
        var TAG="BREAKDOWNINSPECTIONACTIVITY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_breakdown_inspection)
        if(!isBreakDownItemInitialize()){
            showToast("BreakDown Data is not received",this)
        }
        vm = DependencyProvider.getMainVM(this)
        prefs = Prefs.getInstance(this)
        loadingDialog = LoadingDialog(this)
        cqSDKInitializer = CQSDKInitializer(this)
        binding.addImagesBtn.setOnClickListener {
            if(validate())
            startInspection()
        }
        vm.GetVehiclefuelListing().observe(this) {
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

    private fun validate():Boolean{
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
                                selectedFuelId = ids[position]
                            }
                        }
                    }
                }
            }
        }
    }
    private fun startInspection() {
        cqOpened = true
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
                            regNumber = crrRegNo.replace(
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
                        isOffline = Prefs.getInstance(App.instance).returnInspectionFirstTime!!, // true, Offline quote will be created | false, online quote will be created | null, online

                        skipInputPage = true, // true, Inspection will be started with camera page | false, Inspection will be started

                    ),

                        result = { isStarted, msg, code ->

                            Log.e(TAG, "startInspection: $msg $code")
                            if (isStarted) {
                                Prefs.getInstance(App.instance).returnInspectionFirstTime = false
                                Log.d(TAG, "isStarted $msg")
                            } else {
                                loadingDialog.dismiss()
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
}
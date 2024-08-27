package com.clebs.celerity_admin.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.RequestTypeListAdapter
import com.clebs.celerity_admin.databinding.ActivityReturnToDaBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponseItem
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DependencyClass.addBlueMileage
import com.clebs.celerity_admin.utils.DependencyClass.crrMileage
import com.clebs.celerity_admin.utils.DependencyClass.crrSelectedVehicleType
import com.clebs.celerity_admin.utils.DependencyClass.selectedCompanyId
import com.clebs.celerity_admin.utils.DependencyClass.selectedRequestTypeId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleFuelId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleLocId
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleLocationName
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleOilLevelListId
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.clientUniqueID
import com.clebs.celerity_admin.utils.observeOnce
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.viewModels.MainViewModel
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails

class ReturnToDaActivity : AppCompatActivity() {
    lateinit var binding: ActivityReturnToDaBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mainViewModel: MainViewModel

    lateinit var prefs: Prefs
    private var isRbRoadWorthySelected: Boolean = false
    private var isRbNotRoadWorthy: Boolean = false
    private var vehicleValid: Boolean = false
    private var crrRegNo: String = ""
    private var cqOpened = false
    private lateinit var cqSDKInitializer: CQSDKInitializer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnToDaBinding.inflate(layoutInflater)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        window.statusBarColor = resources.getColor(R.color.commentbg, null)
        prefs = Prefs.getInstance(this)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        cqSDKInitializer = CQSDKInitializer(this)
        loadingDialog = LoadingDialog(this)
        setContentView(binding.root)
        clickListeners()
        //mainViewModel.GetAllVehicleInspectionList()
        loadingDialog.show()
        mainViewModel.GetReturnVehicleList()
        observers()
        updateCardLayout(-1)
    }

    private fun observers() {
        mainViewModel.LDGetReturnVehicleList.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                val vehicleNameList = arrayListOf<String>()
                val vehicleIdList = arrayListOf<Int>()
                val locationIdList = arrayListOf<Int>()
                val locationNameList = arrayListOf<String>()
                val vehicleRegNoList = arrayListOf<String>()
                it.map { vehicleList ->
                    if (vehicleList.VehicleName != null && vehicleList.VehicleId != null && vehicleList.VehicleRegNo != null) {
                        vehicleNameList.add(vehicleList.VehicleRegNo)
                        vehicleIdList.add(vehicleList.VehicleId)
                        vehicleRegNoList.add(vehicleList.VehicleType)
                        locationIdList.add(vehicleList.LocationId)
                        locationNameList.add(vehicleList.LocationName)
                    }
                }

                setSpinner(
                    binding.layoutSelectVehicleOptions.spinnerSelectVehicle,
                    vehicleNameList,
                    vehicleIdList,
                    vehicleRegNoList,
                    locationIdList,
                    locationNameList
                )
            }
        }
        mainViewModel.GetCompanyListing().observe(this) {
            if (it != null) {
                val companyNames = arrayListOf<String>()
                val companyIds = arrayListOf<Int>()
                it.map { company ->
                    if (company.name != null && company.id != null) {
                        companyNames.add(company.name)
                        companyIds.add(company.id)
                    }
                }
                setSpinner(
                    binding.layoutSelectVehicleOptions.spinnerSelectCompany,
                    companyNames,
                    companyIds
                )
            }
        }
        /*mainViewModel.VehicleInspectionListLiveData.observe(this) {
            if (it != null) {
                val vehicleNameList = arrayListOf<String>()
                val vehicleIdList = arrayListOf<Int>()
                val vehicleRegNoList = arrayListOf<String>()
                it.map { vehicleList ->
                    if (vehicleList.VehicleName != null && vehicleList.VehicleId != null && vehicleList.VehicleRegNo != null) {
                        vehicleNameList.add(vehicleList.VehicleRegNo)
                        vehicleIdList.add(vehicleList.VehicleId)
                        vehicleRegNoList.add(vehicleList.VehicleRegNo)
                    }
                }

                setSpinner(
                    binding.layoutSelectVehicleOptions.spinnerSelectVehicle,
                    vehicleNameList,
                    vehicleIdList,
                    vehicleRegNoList
                )
            }
        }*/
        /*        mainViewModel.GetVehicleLocationListing().observe(this) {
                    if (it != null) {
                        val locationIds = arrayListOf<Int>()
                        val locationNames = arrayListOf<String>()
                        it.map { locations ->
                            if (locations.locId != null && locations.locationName != null) {
                                locationIds.add(locations.locId)
                                locationNames.add(locations.locationName)
                            }
                        }
                        setSpinner(
                            binding.layoutSelectVehicleInformation.spinnerSelectVehicleLocation,
                            locationNames,
                            locationIds
                        )
                    }
                }*/
        mainViewModel.GetVehiclefuelListing().observeOnce(this) {
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
                    binding.layoutSelectVehicleInformation.spinnerVehicleFuelLevel,
                    fuelTypes,
                    fuelIds
                )
            }
        }
        mainViewModel.GetVehicleOilListing().observe(this) {
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
                    binding.layoutSelectVehicleInformation.spinnerVehicleOilLevel, oilNames, oilIds
                )
            }
        }
        mainViewModel.GetVehicleDamageWorkingStatus()
        mainViewModel.VehicleDamageWorkingStatusLD.observe(this) {
            if (it != null) {
                val workingStatusIds = arrayListOf<Int>()
                val workingStatusNames = arrayListOf<String>()
                it.map { requestTypes ->
                    if (requestTypes.Id != null && !requestTypes.Name.isNullOrEmpty()) {
                        workingStatusIds.add(requestTypes.Id)
                        workingStatusNames.add(requestTypes.Name)
                    }
                }
                setSpinner(
                    binding.layoutReturnVehicle.spinnerRequestType,
                    workingStatusNames, workingStatusIds
                )
            }
        }
        mainViewModel.GetCurrentAllocatedDaLD.observe(this) {
            loadingDialog.dismiss()
            vehicleValid = false
            if (it != null) {
                if (it.VehicleInfo.AllowReturnSupplier != null) {
                    binding.layoutSelectVehicleOptions.errorText.text =
                        it.VehicleInfo.AllowReturnSupplier!!
                    updateCardLayout(3)
                } else {
                    prefs.saveCurrentVehicleInfo(it.VehicleInfo)
                    vehicleValid = true
                    if (selectedCompanyId != -1) {
                        updateCardLayout(4)
                    }
                }
            }
        }
    }

    private fun setSpinner(
        spinner: AutoCompleteTextView,
        items: List<String>,
        ids: List<Int>,
        regNos: List<String>? = listOf(),
        locationIds: List<Int>? = listOf(),
        locationNames: List<String>? = listOf()
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
                            binding.layoutSelectVehicleOptions.spinnerSelectCompany -> {
                                selectedCompanyId = ids[position]
                                if (vehicleValid) updateCardLayout(4)
                            }

                            binding.layoutSelectVehicleOptions.spinnerSelectVehicle -> {
                                selectedVehicleId = ids[position]
                                loadingDialog.show()
                                mainViewModel.GetCurrentAllocatedDa(
                                    selectedVehicleId.toString(), true
                                )
                                selectedVehicleLocId = locationIds!![position]
                                selectedVehicleLocationName = locationNames!![position]
                                binding.layoutSelectVehicleInformation.vehicleLocation.text =
                                    locationNames[position]
                                crrRegNo = items[position]
                                if (!regNos.isNullOrEmpty())
                                    crrSelectedVehicleType = regNos[position]
                            }

                            /*            binding.layoutSelectVehicleInformation.spinnerSelectVehicleLocation -> {
                                            selectedVehicleLocId = ids[position]
                                            card2Update()
                                        }*/

                            binding.layoutSelectVehicleInformation.spinnerVehicleFuelLevel -> {
                                selectedVehicleFuelId = ids[position]
                                card2Update()
                            }

                            binding.layoutSelectVehicleInformation.spinnerVehicleOilLevel -> {
                                selectedVehicleOilLevelListId = ids[position]
                                card2Update()
                            }

                            binding.layoutReturnVehicle.spinnerRequestType -> {
                                selectedRequestTypeId = ids[position]
                            }
                        }
                    }
                }
            }
        }
    }

    private fun card2Update() {
        if (selectedVehicleLocId != -1 && selectedVehicleFuelId != -1 && selectedVehicleOilLevelListId != -1) {
            updateCardLayout(6)
        }
    }

    private fun clickListeners() {
        binding.back.setOnClickListener {
            finish()
        }
        binding.layoutSelectVehicleOptions.headerSelectVehicleOptions.setOnClickListener {
            updateCardLayout(0)
        }
        binding.layoutSelectVehicleInformation.headerVehicleInformation.setOnClickListener {
            updateCardLayout(1)
        }
        binding.layoutAddImages.headerAddInspectionImages.setOnClickListener {
            updateCardLayout(5)
        }
        binding.layoutAddImages.addImagesBtn.setOnClickListener {
            startInspection()
        }
        binding.layoutReturnVehicle.rbRoadWorthy.setOnClickListener {
            if (binding.layoutReturnVehicle.rbRoadWorthy.isChecked) {
                updateCardLayout(9)
                isRbRoadWorthySelected = true
                isRbNotRoadWorthy = false
            }
        }
        binding.layoutReturnVehicle.rbNotRoadWorthy.setOnClickListener {
            if (binding.layoutReturnVehicle.rbNotRoadWorthy.isChecked) {
                updateCardLayout(10)
                isRbNotRoadWorthy = true
                isRbRoadWorthySelected = false
            }
        }
        binding.layoutReturnVehicle.returnVehicleBtn.setOnClickListener {
            if (isRbRoadWorthySelected) {
                returnVehicle()
            } else if (isRbNotRoadWorthy) {
                if (selectedRequestTypeId == -1) {
                    showToast("Please select request type first!!", this@ReturnToDaActivity)
                } else {
                    returnVehicle()
                }
            } else {
                showToast("Please select road worthiness first!!", this@ReturnToDaActivity)
            }
        }
    }

    private fun returnVehicle() {
        if (!binding.layoutSelectVehicleInformation.atvAddBlueMileage.text.isNullOrEmpty())
            addBlueMileage =
                binding.layoutSelectVehicleInformation.atvAddBlueMileage.text.toString()
        else {
            showToast("Please add current Add Blue Mileage", this)
            return
        }
        if (!binding.layoutSelectVehicleInformation.atvVehicleCurrentMileage.text.isNullOrEmpty())
            crrMileage =
                binding.layoutSelectVehicleInformation.atvVehicleCurrentMileage.text.toString()
                    .toInt()
        else {
            showToast("Please add current Mileage", this)
            return
        }
        startActivity(Intent(this, VanHireReturnAgreementActivity::class.java))
    }

    private fun updateCardLayout(cardToShow: Int) {
        when (cardToShow) {
            -1 -> {
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = false
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )

                binding.layoutAddImages.bodyAddInspectionImages.isVisible = false
                binding.layoutAddImages.headerAddInspectionImages.isClickable = false
                binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )

                binding.layoutReturnVehicle.body.isVisible = false
                binding.layoutReturnVehicle.headerReturnVehicle.isClickable = false
                binding.layoutReturnVehicle.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
            }

            0 -> {
                if (binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible) {
                    binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = false
                    binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropdown)
                    )
                } else {
                    binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = true
                    binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropup)
                    )
                }
            }

            1 -> {
                if (binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible) {
                    binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                    binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropdown)
                    )
                } else {
                    binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = true
                    binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropup)
                    )
                }
            }

            3 -> {
                binding.layoutSelectVehicleOptions.errorText.visibility = View.VISIBLE
                binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = true
                binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropup)
                )
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = false
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutAddImages.headerAddInspectionImages.isClickable = false
                binding.layoutAddImages.bodyAddInspectionImages.isVisible = false
            }

            4 -> {
                binding.layoutSelectVehicleOptions.errorText.visibility = View.GONE
                binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = false
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = true
                binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = true
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropup)
                )
                binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                if (selectedVehicleLocId != -1 && selectedVehicleFuelId != -1 && selectedVehicleOilLevelListId != -1) {
                    binding.layoutAddImages.headerAddInspectionImages.isClickable = true
                    binding.layoutAddImages.bodyAddInspectionImages.isVisible = true
                } else {
                    binding.layoutAddImages.headerAddInspectionImages.isClickable = false
                    binding.layoutAddImages.bodyAddInspectionImages.isVisible = false
                }
            }

            5 -> {
                if (binding.layoutAddImages.bodyAddInspectionImages.isVisible) {
                    binding.layoutAddImages.bodyAddInspectionImages.isVisible = false
                    binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropdown)
                    )
                } else {
                    binding.layoutAddImages.bodyAddInspectionImages.isVisible = true
                    binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropup)
                    )
                }
            }

            6 -> {
                binding.layoutSelectVehicleOptions.errorText.visibility = View.GONE
                binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = false
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = true
                binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = true
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )

                binding.layoutAddImages.bodyAddInspectionImages.isVisible = true
                binding.layoutAddImages.headerAddInspectionImages.isClickable = true
            }

            7 -> {
                if (binding.layoutReturnVehicle.body.isVisible) {

                    binding.layoutReturnVehicle.body.isVisible = false
                    binding.layoutReturnVehicle.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropdown)
                    )
                } else {
                    binding.layoutReturnVehicle.body.isVisible = true
                    binding.layoutReturnVehicle.headerStatusIcon.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.dropup)
                    )
                }
            }

            8 -> {
                binding.layoutSelectVehicleOptions.errorText.visibility = View.GONE
                binding.layoutSelectVehicleOptions.bodyVehicleOptions.isVisible = false
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = true
                binding.layoutSelectVehicleOptions.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = true
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )

                binding.layoutAddImages.bodyAddInspectionImages.isVisible = false
                binding.layoutAddImages.headerAddInspectionImages.isClickable = true
                binding.layoutAddImages.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropdown)
                )

                binding.layoutReturnVehicle.headerReturnVehicle.isClickable = true
                binding.layoutReturnVehicle.body.isVisible = true
                binding.layoutReturnVehicle.headerStatusIcon.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.dropup)
                )
            }

            9 -> {
                updateCardLayout(8)
                binding.layoutReturnVehicle.tilSpinnerRequestType.visibility = View.GONE
                binding.layoutReturnVehicle.returnVehicleBtn.visibility = View.VISIBLE
            }

            10 -> {
                updateCardLayout(8)
                binding.layoutReturnVehicle.tilSpinnerRequestType.visibility = View.VISIBLE
                binding.layoutReturnVehicle.returnVehicleBtn.visibility = View.VISIBLE

            }
        }
    }

    companion object {
        const val TAG = "ReturnToDaActivity"
    }

    private fun startInspection() {
        cqOpened = true
        if (crrRegNo.isNotBlank()) {
            clientUniqueID()
            if (cqSDKInitializer.isCQSDKInitialized()) {
                try {

                    loadingDialog.show()
                    cqSDKInitializer.startInspection(activity = this, clientAttrs = ClientAttrs(
                        userName = " ",
                        dealer = " ",
                        dealerIdentifier = " ",
                        client_unique_id = App.prefs!!.vehinspectionUniqueID

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
                                        "Please Turn on the internet", this@ReturnToDaActivity
                                    )
                                    Log.d(TAG, "CQ: Not isStarted1  $msg")
                                } else if (msg == "Sufficient data not available to create an offline quote") {
                                    Prefs.getInstance(App.instance).returnInspectionFirstTime = true
                                    showToast(
                                        "Please Turn on the internet & grant required permissions.",
                                        this@ReturnToDaActivity
                                    )
                                    Log.d(TAG, "CQSDKXX : Not isStarted2  $msg")
                                } else if (msg == "Unable to download setting updates, Please check internet") {
                                    showToast(
                                        "Please Turn on the internet", this@ReturnToDaActivity
                                    )
                                    Log.d(TAG, "CQSDKXX : Not isStarted3  $msg")
                                } else if (msg == "Vehicle not in fleet list") {
                                    showToast(
                                        "Vehicle not in fleet list", this@ReturnToDaActivity
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

    override fun onResume() {
        super.onResume()
        loadingDialog.dismiss()
        if (cqOpened) {
            updateCardLayout(8)
            //cqOpened = false
        }
    }
}
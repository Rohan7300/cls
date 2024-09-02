package com.clebs.celerity_admin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.RequestTypeListAdapter
import com.clebs.celerity_admin.databinding.ActivityReturnToDaBinding

import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponseItem
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.observeOnce
import com.clebs.celerity_admin.viewModels.MainViewModel

class ReturnToDaActivity : AppCompatActivity() {
    lateinit var binding: ActivityReturnToDaBinding
//    private lateinit var loadingDialog: LoadingDialog
    private lateinit var mainViewModel: MainViewModel
    var selectedCompanyId: Int = -1
    var selectedVehicleId: Int = -1
    var selectedVehicleLocId: Int = -1
    var selectedVehicleFuelId: Int = -1
    var selectedVehicleOilLevelListId: Int = -1
    var vehicleValid: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReturnToDaBinding.inflate(layoutInflater)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
//        loadingDialog = LoadingDialog(this)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }
        clickListeners()
        mainViewModel.GetAllVehicleInspectionList()
        observers()
        updateCardLayout(-1)
    }

    private fun observers() {
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
        mainViewModel.VehicleInspectionListLiveData.observe(this) {
            if (it != null) {
                val vehicleNameList = arrayListOf<String>()
                val vehicleIdList = arrayListOf<Int>()
                it.map { vehicleList ->
                    if (vehicleList.VehicleName != null && vehicleList.VehicleId != null) {
                        vehicleNameList.add(vehicleList.VehicleName)
                        vehicleIdList.add(vehicleList.VehicleId)
                    }
                }

                setSpinner(
                    binding.layoutSelectVehicleOptions.spinnerSelectVehicle,
                    vehicleNameList,
                    vehicleIdList
                )
            }
        }
        mainViewModel.GetVehicleLocationListing().observe(this) {
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
        }
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

//        mainViewModel.GetCurrentAllocatedDaLD.observe(this) {
//
//            vehicleValid = false
//            if (it != null) {
//                if (it.VehicleInfo.AllowReturnSupplier != null) {
//                    binding.layoutSelectVehicleOptions.errorText.text =
//                        it.VehicleInfo.AllowReturnSupplier!!
//                    updateCardLayout(3)
//                } else {
//                    vehicleValid = true
//                    if (selectedCompanyId != -1) {
//                        updateCardLayout(4)
//                    }
//                }
//            }
//        }
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
                            binding.layoutSelectVehicleOptions.spinnerSelectCompany -> {
                                selectedCompanyId = ids[position]
                                if (vehicleValid)
                                    updateCardLayout(4)
                            }

                            binding.layoutSelectVehicleOptions.spinnerSelectVehicle -> {
                                selectedVehicleId = ids[position]

                                mainViewModel.GetCurrentAllocatedDa(
                                    selectedVehicleId.toString(), true
                                )
                            }

                            binding.layoutSelectVehicleInformation.spinnerSelectVehicleLocation -> {
                                selectedVehicleLocId = ids[position]
                                card2Update()
                            }

                            binding.layoutSelectVehicleInformation.spinnerVehicleFuelLevel -> {
                                selectedVehicleFuelId = ids[position]
                                card2Update()
                            }

                            binding.layoutSelectVehicleInformation.spinnerVehicleOilLevel -> {
                                selectedVehicleOilLevelListId = ids[position]
                                card2Update()
                            }
                        }
                    }
                }
            }
        }
    }
    fun card2Update(){
        if(selectedVehicleLocId!=-1&&selectedVehicleFuelId!=-1&&selectedVehicleOilLevelListId!=-1){
            updateCardLayout(5)
        }
    }

    private fun clickListeners() {
        binding.layoutSelectVehicleOptions.headerSelectVehicleOptions.setOnClickListener {
            updateCardLayout(0)
        }
        binding.layoutSelectVehicleInformation.headerVehicleInformation.setOnClickListener {
            updateCardLayout(1)
        }
    }

    private fun updateCardLayout(cardToShow: Int) {
        when (cardToShow) {
            -1 -> {
                binding.layoutSelectVehicleInformation.headerVehicleInformation.isClickable = false
                binding.layoutSelectVehicleInformation.bodyVehicleInfo.isVisible = false
                binding.layoutSelectVehicleInformation.headerStatusIcon.setImageDrawable(
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
            }
            5->{
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
            }
        }
    }
}
package com.clebs.celerity_admin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.DeleteCallback
import com.clebs.celerity_admin.adapters.RequestTypeListAdapter
import com.clebs.celerity_admin.databinding.ActivityBreakDownBinding
import com.clebs.celerity_admin.dialogs.LoadingDialog
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponse
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponseItem
import com.clebs.celerity_admin.models.SaveVehicleBreakDownInspectionRequest
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DependencyClass.VehInspectionDate
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.showDatePickerDialog
import com.clebs.celerity_admin.utils.showToast
import com.clebs.celerity_admin.viewModels.MainViewModel
import okhttp3.internal.notify

class BreakDownActivity : AppCompatActivity(), DeleteCallback {
    lateinit var binding: ActivityBreakDownBinding
    private lateinit var loadingDialog: LoadingDialog
    private var selectedVehicleId = -1
    private var selectedDriverId = -1
    lateinit var listAdapter: RequestTypeListAdapter
    private var selectedRequestId = -1
    private lateinit var mainViewModel: MainViewModel
    private var vehicleDamageWorkingStatusList =
        arrayListOf<GetVehicleDamageWorkingStatusResponseItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreakDownBinding.inflate(layoutInflater)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        loadingDialog = LoadingDialog(this)

        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        loadingDialog.show()
        mainViewModel.GetAllVehicleInspectionList()
        mainViewModel.GetAllDriversInspectionList()
        mainViewModel.GetVehicleDamageWorkingStatus()
        observers()
        listAdapter = RequestTypeListAdapter(this@BreakDownActivity)
        binding.rvvehicleDamageWorking.adapter = listAdapter
        binding.rvvehicleDamageWorking.layoutManager = LinearLayoutManager(this)
        binding.aTvInspectionDate.setOnClickListener {
            showDatePickerDialog(this, binding.aTvInspectionDate)
        }
        binding.tvInspectionDate.setOnClickListener {
            showDatePickerDialog(this, binding.aTvInspectionDate)
        }
        binding.flInspectionDate.setOnClickListener {
            showDatePickerDialog(this, binding.aTvInspectionDate)
        }
        binding.cancelBtn.setOnClickListener {
            finish()
        }
        binding.saveBtn.setOnClickListener {
            if (checkValidation()) {
                saveBreakDownRequest()
            }
        }
        setContentView(binding.root)
    }

    private fun saveBreakDownRequest() {
        var comment = " "
        if (!binding.comment.text.isNullOrEmpty())
            comment = binding.comment.text.toString()

        loadingDialog.show()
        mainViewModel.SaveVehicleBreakDownInspectionInfo(
            SaveVehicleBreakDownInspectionRequest(
                Comment = comment,
                DriverId = selectedDriverId,
                InspectionId = 0,
                SuperVisorId = Prefs.getInstance(this@BreakDownActivity).clebUserId.toInt(),
                VehInspectionDate = VehInspectionDate!!,
                VehRequestTypeIds = vehicleDamageWorkingStatusList.map { it.Id },
                VmId = selectedVehicleId
            )
        )
    }

    private fun checkValidation(): Boolean {
        return if (selectedDriverId == -1) {
            showToast("Please select Driver Name before submitting", this)
            false
        } else if (selectedVehicleId == -1) {
            showToast("Please select Vehicle before submitting", this)
            false
        } else if (selectedRequestId == -1) {
            showToast("Please select Request Type before submitting", this)
            false
        } else if (binding.aTvInspectionDate.text.isNullOrEmpty() || VehInspectionDate == null) {
            showToast("Please add Inspection Date before submitting", this)
            false
        } else
            true
    }

    private fun observers() {
        mainViewModel.VehicleInspectionListLiveData.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                val vehicleNameList = it.map { vehicleList ->
                    vehicleList.VehicleName
                }
                val vehicleIdList = it.map { vehicleList ->
                    vehicleList.VehicleId
                }
                setSpinner(
                    binding.vehicleListSpinner,
                    vehicleNameList,
                    vehicleIdList
                )
            }
        }

        mainViewModel.DriverInspectionListLiveData.observe(this) {
            if (it != null) {
                val driverNameList = it.map { driverList ->
                    driverList.Name
                }
                val driverIdList = it.map { driverIdList ->
                    driverIdList.Id
                }
                setSpinner(
                    binding.spinnerDriverList,
                    driverNameList,
                    driverIdList
                )
            }
        }

        mainViewModel.VehicleDamageWorkingStatusLD.observe(this) {
            if (it != null) {
                val damageStatusName = it.map { damages ->
                    damages.Name
                }
                val damageStatusID = it.map { damages ->
                    damages.Id
                }
                setSpinner(
                    binding.selectedRequestType,
                    damageStatusName,
                    damageStatusID
                )
            }
        }

        mainViewModel.SaveVehicleBreakDownInspectionLD.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                if (it.Status == "200") {
                    showToast("Data saved successfully", this@BreakDownActivity)
                    finish()
                }
            } else {
                showToast("Failed to save Data!!", this@BreakDownActivity)
            }
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
                            binding.vehicleListSpinner -> {
                                selectedVehicleId = ids[position]
                            }

                            binding.spinnerDriverList -> {
                                selectedDriverId = ids[position]
                            }

                            binding.selectedRequestType -> {
                                vehicleDamageWorkingStatusList.add(
                                    GetVehicleDamageWorkingStatusResponseItem(
                                        ids[position],
                                        items[position]
                                    )
                                )
                                listAdapter.notifyItemInserted(listAdapter.itemCount)
                                listAdapter.saveData(vehicleDamageWorkingStatusList)
                                selectedRequestId = ids[position]
                                binding.selectedRequestType.text = null

                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDelete(item: GetVehicleDamageWorkingStatusResponseItem,position:Int) {
        vehicleDamageWorkingStatusList.remove(item)
        listAdapter.notifyItemRemoved(position)
        listAdapter.saveData(vehicleDamageWorkingStatusList)
    }
}
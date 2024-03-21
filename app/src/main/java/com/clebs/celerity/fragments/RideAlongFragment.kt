package com.clebs.celerity.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentRideAlongBinding
import com.clebs.celerity.models.requests.AddOnRideAlongRouteInfoRequest
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.showToast


class RideAlongFragment : Fragment() {
    lateinit var binding: FragmentRideAlongBinding
    lateinit var viewModel: MainViewModel
    var selectedDriverId: Int? = null
    var selectedDriverName = ""
    var selectedVehicleId: Int? = null
    var selectedVehicleName = ""
    var selectedRouteId: Int? = null
    var selectedLocId: Int? = null
    private var rtType: Int? = null
    var routeName: String? = null
    var routeComment: String? = null
    private var training: Boolean? = null
    private var retraining: Boolean? = null
    private var leadDriverID: Int? = null
    private var trainingDays: Int? = null
    private var rtFinishMileage: Int? = null
    private var rtNoOfParcelsDelivered: Int? = null
    private var rtNoParcelsbroughtback: Int? = null
    lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentRideAlongBinding.inflate(inflater, container, false)
        }
        viewModel = (activity as HomeActivity).viewModel
        loadingDialog = (activity as HomeActivity).loadingDialog
        clickListeners()
        setInputListener(binding.edtParcels)
        setInputListener(binding.edtRouteComment)
        observers()
        viewModel.GetRideAlongDriversList()

        return binding.root
    }

    private fun observers() {
        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            binding.headerTop.dxLoc.text = it?.locationName ?: ""
            binding.headerTop.dxReg.text = it?.vmRegNo ?: ""
            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                binding.headerTop.anaCarolin.text = name
            }
            binding.headerTop.dxm5.text = (activity as HomeActivity).date
            leadDriverID = (activity as HomeActivity).userId
        }
        viewModel.livedataGetRideAlongVehicleLists.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            it?.let { vehicleLists ->
                val vehIds = vehicleLists.mapNotNull { vehicleList -> vehicleList.VehicleId }
                val vehNames = vehicleLists.mapNotNull { vehicleList -> vehicleList.VehicleName }

                if (vehNames.isNotEmpty() && vehIds.isNotEmpty()) {
                    setSpinners(binding.spinnerSelectVehicle, binding.editText, vehNames, vehIds)
                }
            }
        }
        viewModel.livedataGetRideAlongDriversList.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                val driverId = it.map { drivers -> drivers.Id }
                val driverName = it.map { drivers -> drivers.Name }

                if (driverId.isNotEmpty() && driverName.isNotEmpty()) setSpinners(
                    binding.spinnerSelectDriver, binding.edtRoutes, driverName, driverId
                )
                loadingDialog.cancel()
                viewModel.GetRideAlongVehicleLists()
            }
        }

        viewModel.liveDataRideAlongRouteTypeInfo.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                val typeName = it.map { type -> type.RtName }
                val typeId = it.map { type -> type.RtId }

                if (typeName.isNotEmpty() && typeId.isNotEmpty()) setSpinners(
                    binding.SpinnerRouteType, binding.tvRouteType, typeName, typeId
                )
            }
        }

        viewModel.livedataGetRouteInfoById.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                loadingDialog.show()
                try {
                    viewModel.GetRouteLocationInfo(it.RtLocationId)
                    viewModel.GetRideAlongRouteInfoById(it.RtId, selectedDriverId!!)
                } catch (_: Exception) {

                }
            }
        }
        viewModel.livedataRideAlongRouteInfoById.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                rtType = it.RtType
                trainingDays = it.TrainingDays
                rtFinishMileage = it.RtFinishMileage
                rtNoOfParcelsDelivered = it.RtNoOfParcelsDelivered
                rtNoParcelsbroughtback = it.RtNoParcelsbroughtback
            }
        }
        viewModel.liveDataRouteLocationResponse.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                val locationNames = it.map { loc -> loc.LocationName }
                val locationId = it.map { loc -> loc.LocId }

                if (locationNames.isNotEmpty() && locationId.isNotEmpty()) setSpinners(
                    binding.spinnerRouteLocation,
                    binding.editTextSelectRouteLocation,
                    locationNames,
                    locationId
                )
            }
        }

        viewModel.livedataRideAlongSubmitApiRes.observe(viewLifecycleOwner) {
            if (it != null) {
                findNavController().navigate(R.id.completeTaskFragment)
                findNavController().clearBackStack(R.id.completeTaskFragment)

            } else {
                showToast("Please!! try again.", requireContext())
            }

        }
    }


    private fun clickListeners() {
        var isReTrainingSelected = false
        var isTrainingSelected = false
        binding.run {
            rideAlongCancel.setOnClickListener {
                findNavController().navigate(R.id.completeTaskFragment)
                findNavController().clearBackStack(R.id.completeTaskFragment)
            }
            saveBT.setOnClickListener {
                if (chkNull()) showToast("Please fill all fields!!", requireContext())
                else rideAlongApi()
            }
            rbReTraining.setOnClickListener {
                isReTrainingSelected = !isReTrainingSelected
                rbReTraining.isChecked = isReTrainingSelected
                retraining = true
                training = false
                rbTraining.isChecked = false
            }

            rbTraining.setOnClickListener {
                isTrainingSelected = !isTrainingSelected
                isReTrainingSelected = false
                retraining = false
                training = true
                rbReTraining.isChecked = false
                rbTraining.isChecked = isTrainingSelected
            }
        }
    }

    private fun rideAlongApi() {
        loadingDialog.show()
        viewModel.AddOnRideAlongRouteInfo(
            AddOnRideAlongRouteInfoRequest(
                IsReTraining = retraining!!,
                LeadDriverId = leadDriverID!!,
                RtAddMode = "A",
                RtComment = routeComment!!,
                RtFinishMileage = rtFinishMileage!!,
                RtId = selectedRouteId!!,
                RtLocationId = selectedLocId!!,
                RtName = routeName!!,
                RtNoOfParcelsDelivered = rtNoOfParcelsDelivered!!,
                RtNoParcelsbroughtback = rtNoParcelsbroughtback!!,
                RtType = rtType!!,
                RtUsrId = selectedDriverId!!,
                TrainingDays = 0,
                VehicleId = selectedVehicleId!!
            )
        )
    }

    private fun chkNull(): Boolean {
        return listOf(
            selectedDriverId,
            selectedVehicleId,
            selectedRouteId,
            routeName,
            routeComment,
            retraining,
            training,
            rtType,
            trainingDays,
            rtFinishMileage,
            rtNoOfParcelsDelivered,
            rtNoParcelsbroughtback
        ).any { it == null }
    }

    private fun setSpinners(spinner: Spinner, tv: TextView, items: List<String>, ids: List<Int>) {

        val dummyItem = "Select Item"
        val itemsList = mutableListOf(dummyItem)
        itemsList.addAll(items)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //adapter.addAll(itemsList)

        spinner.adapter = adapter

        spinner.setSelection(0)

        try {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    parent?.let { nonNullParent ->
                        if (position != 0) { // Skip the dummy item
                            val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                            selectedItem.let { nonNullSelectedItem ->
                                tv.text = nonNullSelectedItem
                                when (spinner) {
                                    binding.spinnerSelectDriver -> {
                                        selectedDriverId =
                                            ids[position - 1] // Adjust the index for ids list
                                        selectedDriverName = nonNullSelectedItem
                                        loadingDialog.show()
                                        viewModel.GetRideAlongRouteTypeInfo(selectedDriverId!!)
                                    }

                                    binding.spinnerSelectVehicle -> {
                                        selectedVehicleName = nonNullSelectedItem
                                        selectedVehicleId =
                                            ids[position - 1] // Adjust the index for ids list
                                    }

                                    binding.SpinnerRouteType -> {
                                        selectedRouteId =
                                            ids[position - 1] // Adjust the index for ids list
                                        loadingDialog.show()
                                        viewModel.GetRouteInfoById(selectedRouteId!!)
                                    }

                                    binding.spinnerRouteLocation -> {
                                        selectedLocId =
                                            ids[position - 1] // Adjust the index for ids list
                                    }
                                }
                            }
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        } catch (_: Exception) {
        }
    }

    private fun setInputListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s?.toString()
                when (editText) {
                    binding.edtParcels -> routeName = value
                    binding.edtRouteComment -> routeComment = value
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
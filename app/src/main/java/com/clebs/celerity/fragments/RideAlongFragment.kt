package com.clebs.celerity.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentRideAlongBinding
import com.clebs.celerity.ui.HomeActivity


class RideAlongFragment : Fragment() {
    lateinit var binding: FragmentRideAlongBinding
    lateinit var viewModel: MainViewModel
    var selectedDriverId = 0
    var selectedDriverName = ""
    var selectedVehicleId = 0
    var selectedVehicleName = ""
    var selectedRouteId = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentRideAlongBinding.inflate(inflater, container, false)
        }
        viewModel = (activity as HomeActivity).viewModel

        clickListeners()
        observers()
        viewModel.GetRideAlongDriversList()

        return binding.root
    }

    private fun observers() {
        viewModel.livedataGetRideAlongVehicleLists.observe(viewLifecycleOwner) {
            it?.let { vehicleLists ->
                val vehIds = vehicleLists.mapNotNull { vehicleList -> vehicleList.VehicleId }
                val vehNames = vehicleLists.mapNotNull { vehicleList -> vehicleList.VehicleName }

                if (vehNames.isNotEmpty() && vehIds.isNotEmpty()) {
                    setSpinners(binding.spinnerSelectVehicle, binding.editText, vehNames, vehIds)
                }
            }
        }
        viewModel.livedataGetRideAlongDriversList.observe(viewLifecycleOwner) {
            if (it != null) {
                val driverId = it.map { drivers -> drivers.Id }
                val driverName = it.map { drivers -> drivers.Name }

                if (driverId.isNotEmpty() && driverName.isNotEmpty()) setSpinners(
                    binding.spinnerSelectDriver, binding.edtRoutes, driverName, driverId
                )
                viewModel.GetRideAlongVehicleLists()
            }

        }

        viewModel.liveDataRideAlongRouteTypeInfo.observe(viewLifecycleOwner) {
            if (it != null) {
                val typeName = it.map { type -> type.RtName }
                val typeId = it.map { type -> type.RtId }

                if (typeName.isNotEmpty() && typeId.isNotEmpty()) setSpinners(
                    binding.SpinnerRouteType, binding.tvRouteType, typeName, typeId
                )
            }
        }

        viewModel.livedataGetRouteInfoById.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.GetRouteLocationInfo(it.RtLocationId)
            }
        }

        viewModel.liveDataRouteLocationResponse.observe(viewLifecycleOwner) {
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
    }


    private fun clickListeners() {
        binding.run {
            rideAlongCancel.setOnClickListener {
                findNavController().navigate(R.id.completeTaskFragment)
                findNavController().clearBackStack(R.id.completeTaskFragment)
            }
        }
    }

    private fun setSpinners(spinner: Spinner, tv: TextView, items: List<String>, ids: List<Int>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        try {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {

                    parent?.let { nonNullParent ->
                        val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                        selectedItem.let { nonNullSelectedItem ->
                            tv.text = nonNullSelectedItem
                            when (spinner) {
                                binding.spinnerSelectDriver -> {
                                    selectedDriverId = ids[position]
                                    selectedDriverName = nonNullSelectedItem
                                    viewModel.GetRideAlongRouteTypeInfo(selectedDriverId)
                                }

                                binding.spinnerSelectVehicle -> {
                                    selectedVehicleName = nonNullSelectedItem
                                    selectedVehicleId = ids[position]
                                }

                                binding.SpinnerRouteType -> {
                                    selectedRouteId = ids[position]
                                    viewModel.GetRouteInfoById(selectedRouteId)
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
}
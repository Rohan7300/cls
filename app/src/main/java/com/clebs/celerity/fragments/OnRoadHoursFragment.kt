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
import androidx.core.view.isVisible
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentOnRoadHoursBinding
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs

class OnRoadHoursFragment : Fragment() {
    lateinit var binding: FragmentOnRoadHoursBinding
    lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    var locID: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::binding.isInitialized) {
            binding = FragmentOnRoadHoursBinding.inflate(inflater, container, false)
        }
        init()
        return binding.root

    }

    private fun init() {
        viewModel = (activity as HomeActivity).viewModel
        prefs = Prefs.getInstance(requireContext())
        viewModel.livedataDailyWorkInfoByIdResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                vehicleInfoSection()
            }
        }
        viewModel.GetDailyWorkInfoById(prefs.userID.toInt())
    }

    fun vehicleInfoSection() {
        if (prefs.getLocationID() == 0) {
            viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    prefs.saveLocationID(it.vmLocId)
                    locID = it.vmLocId

                    locationSection()
                }
            }
            viewModel.GetVehicleInformation(
                prefs.userID.toInt(),
                "YE23MUU"
            )
        } else {
            locID = prefs.getLocationID()
            locationSection()
        }
    }

    fun locationSection() {
        viewModel.liveDataRouteLocationResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                rideAlongApiCall()
                val locNames = it.map { it.LocationName }
                setSpinners(binding.spinnerLocation,binding.editTextSelectRouteLocation,locNames)
            }
        }
        viewModel.GetRouteLocationInfo(locID)
    }

    fun rideAlongApiCall() {
        viewModel.liveDataRideAlongRouteTypeInfo.observe(viewLifecycleOwner) {
            if (it != null) {
                val routeNames = it.map { it.RtName }
                setSpinners(binding.spinnerRouteType, binding.editText, routeNames)
            }
        }
        viewModel.GetRideAlongRouteTypeInfo(prefs.userID.toInt())
    }

    private fun setSpinners(spinner: Spinner, tv: TextView, items: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position) as String
                tv.text = selectedItem
                spinner.isVisible = false
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        tv.setOnClickListener {
            spinner.isVisible = !spinner.isVisible
        }
    }
}
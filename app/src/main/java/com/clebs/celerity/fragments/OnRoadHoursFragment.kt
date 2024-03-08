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
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentOnRoadHoursBinding
import com.clebs.celerity.models.requests.AddOnRouteInfoRequest
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast

class OnRoadHoursFragment : Fragment() {
    private var selectedLocId: Int = 0
    private var selectedRouteId: Int = 0
    lateinit var binding: FragmentOnRoadHoursBinding
    lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    private var locID: Int = 0
    private var selectedLocation: String? = null
    private var selectedRouteType: String? = null
    private var routeName: String? = null
    private var parcelsDelivered: String? = null
    private var totalMileage: String? = null
    private var routeComment: String? = null
    private var dwID:Int = 0
    private var vehID:Int = 0
    private lateinit var loadingDialog: LoadingDialog
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentOnRoadHoursBinding.inflate(inflater, container, false)
        }
        init()
        return binding.root

    }

    private fun chkNotNullInputs(): Boolean {
        return selectedLocation == null ||
                selectedRouteType == null ||
                routeName == null ||
                parcelsDelivered == null
                || totalMileage == null
    }

    private fun init() {
        viewModel = (activity as HomeActivity).viewModel
        prefs = Prefs.getInstance(requireContext())
        loadingDialog = (activity as HomeActivity).loadingDialog
        
        viewModel.livedataDailyWorkInfoByIdResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                dwID = it.DailyWorkId
                vehicleInfoSection()
            }
        }
        inputListeners()
        viewModel.GetDailyWorkInfoById(prefs.userID.toInt())
        binding.onRoadHoursSave.setOnClickListener {
            parcelsDelivered = binding.edtParcels.text.toString()
            totalMileage = binding.edtMileage.text.toString()
            if (chkNotNullInputs()) {
                showToast("Pls!!Complete the form first", requireContext())
            } else {
                sendData()
            }
        }
    }

    private fun sendData() {
        loadingDialog.show()
        viewModel.livedataAddOnRouteInfo.observe(viewLifecycleOwner){
            loadingDialog.cancel()
            if(it!=null){
                findNavController().navigate(R.id.completeTaskFragment)
            }
        }
        viewModel.AddOnRouteInfo(AddOnRouteInfoRequest(
            RtAddMode = "" ,
            RtComment = "$routeComment",
            RtType = selectedRouteId,
            RtDwId = dwID,
            RtId = 0,
            RtFinishMileage = totalMileage?.toInt()?:0,
            RtLocationId = selectedLocId,
            RtName = routeName!!,
            RtNoOfParcelsDelivered = parcelsDelivered?.toInt()?:0,
            RtNoParcelsbroughtback = binding.parcelsBroughtBack.text.toString().toInt(),
            RtUsrId = prefs.userID.toInt(),
            VehicleId =vehID
        ))
    }

    private fun setInputListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s?.toString()
                when (editText) {
                    binding.edtRoutes -> routeName = value
                    binding.parcelDeliver -> parcelsDelivered = value
                    binding.totalMileage -> totalMileage = value
                    binding.edtRouteComment -> routeComment = value
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun inputListeners() {
        setInputListener(binding.edtRoutes)
        setInputListener(binding.edtParcels)
        setInputListener(binding.edtMileage)
        setInputListener(binding.edtRouteComment)
    }


    private fun vehicleInfoSection() {
        if (prefs.getLocationID() == 0) {
            viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
                if (it != null) {
                    prefs.saveLocationID(it.vmLocId)
                    locID = it.vmLocId
                    vehID = it.vmId
                    locationSection()
                }
            }
            viewModel.GetDriversBasicInformation(
                Prefs.getInstance(App.instance).userID.toDouble()).observe(viewLifecycleOwner) {
                if (it!=null){
                    if(it.vmRegNo!=null){
                        viewModel.GetVehicleInformation(prefs.userID.toInt(),it.vmRegNo)
                    }
                }
            }
            /*
            viewModel.GetVehicleInformation(
                prefs.userID.toInt(),
                "YE23MUU"
            )*/
        } else {
            locID = prefs.getLocationID()
            locationSection()
        }
    }

    private fun locationSection() {
        viewModel.liveDataRouteLocationResponse.observe(viewLifecycleOwner) { locationData ->
            if (locationData != null) {
                rideAlongApiCall()
                val locNames = locationData.map { it.LocationName }
                val locIds = locationData.map { it.LocId }
                setSpinners(binding.spinnerLocation, binding.editTextSelectRouteLocation, locNames,locIds)
            }
        }
        viewModel.GetRouteLocationInfo(locID)
    }

    private fun rideAlongApiCall() {
        viewModel.liveDataRideAlongRouteTypeInfo.observe(viewLifecycleOwner) { routeData ->
            if (routeData != null) {
                val routeNames = routeData.map { it.RtName }
                val routeIDs = routeData.map { it.RtId }
                setSpinners(binding.spinnerRouteType, binding.editText, routeNames,routeIDs)
            }
        }
        viewModel.GetRideAlongRouteTypeInfo(prefs.userID.toInt())
    }

    private fun setSpinners(spinner: Spinner, tv: TextView, items: List<String>,ids:List<Int>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position) as String
                tv.text = selectedItem
                spinner.isVisible = false

                when (spinner) {
                    binding.spinnerLocation -> {
                        selectedLocId = ids[position]
                        selectedLocation = selectedItem
                    }
                    binding.spinnerRouteType -> {
                        selectedRouteType = selectedItem
                        selectedRouteId = ids[position]
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        tv.setOnClickListener {
            spinner.isVisible = !spinner.isVisible
        }
    }

}
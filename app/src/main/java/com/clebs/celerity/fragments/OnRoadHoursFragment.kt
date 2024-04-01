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
import androidx.databinding.DataBindingUtil
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
    private var parcelsDelivered: String = "0"
    private var totalMileage: String = "0"
    private var routeComment: String? = null
    private var dwID: Int = 0
    private var vehID: Int = 0
    private var parcelBack = 0
    lateinit var edtRoutes: EditText
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_on_road_hours, container, false)
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun chkNotNullInputs(): Boolean {
        return selectedLocation.isNullOrEmpty() ||
                selectedRouteType.isNullOrEmpty() ||
                routeName.isNullOrEmpty()||
                parcelsDelivered.isEmpty()
                || totalMileage.isEmpty()
                || parcelsDelivered.isEmpty()
    }

    private fun init() {
        viewModel = (activity as HomeActivity).viewModel
        prefs = Prefs.getInstance(requireContext())
        loadingDialog = (activity as HomeActivity).loadingDialog
        parcelBack = binding.parcelsBroughtBack.text.toString().toInt()


        binding.pbbPlus.setOnClickListener {
            parcelBack += 1
            binding.parcelsBroughtBack.text = parcelBack.toString()
        }

        binding.pbbMinus.setOnClickListener {
            if (parcelBack > 0) {
                parcelBack -= 1
                binding.parcelsBroughtBack.text = parcelBack.toString()
            }
        }

        viewModel.livedataDailyWorkInfoByIdResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                dwID = it.DailyWorkId
                vehicleInfoSection()
            }
        }

        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            binding.headerTop.dxLoc.text = it?.locationName ?: ""
            binding.headerTop.dxReg.text = it?.vmRegNo ?: ""
            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                binding.headerTop.anaCarolin.text = name
            }
            binding.headerTop.dxm5.text = (activity as HomeActivity).date
        }
        inputListeners()
        viewModel.GetDailyWorkInfoById(prefs.userID.toInt())
        binding.onRoadHoursSave.setOnClickListener {
            parcelsDelivered = binding.edtParcels.text.toString()
            totalMileage = binding.edtMileage.text.toString()
            if(parcelsDelivered.isEmpty())
                parcelsDelivered = "0"
            if(totalMileage.isEmpty())
                totalMileage = "0"
            if (chkNotNullInputs()) {
                showToast("Please!!Complete the form first", requireContext())
            } else {
                sendData()
            }
        }
    }

    private fun sendData() {
        loadingDialog.show()
        viewModel.livedataAddOnRouteInfo.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                findNavController().navigate(R.id.completeTaskFragment)
            }
        }
        viewModel.AddOnRouteInfo(
            AddOnRouteInfoRequest(
                RtAddMode = "A",
                RtComment = "$routeComment",
                RtTypeId = selectedRouteId,
                RtDwId = dwID,
                RtFinishMileage = totalMileage?.toInt() ?: 0,
                RtLocationId = selectedLocId,
                RtName = routeName!!,
                RtNoOfParcelsDelivered = parcelsDelivered?.toInt() ?: 0,
                RtNoParcelsbroughtback = binding.parcelsBroughtBack.text.toString().toInt(),
                RtUsrId = prefs.userID.toInt(),
                VehicleId = vehID
            )
        )
    }

    private fun setInputListener(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val value = s?.toString()
                when (editText.id) {
                    R.id.edt_routesORH -> routeName = value
                    R.id.edt_parcels -> parcelsDelivered = value.toString()
                    R.id.edt_mileage -> totalMileage = value.toString()
                    R.id.edt_route_comment -> routeComment = value
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun inputListeners() {
        setInputListener(binding.edtRoutesORH)
        binding.edtRoutesORH.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS)

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
                Prefs.getInstance(App.instance).userID.toDouble()
            ).observe(viewLifecycleOwner) {
                if (it != null) {
                    if (it.vmRegNo != null) {
                        viewModel.GetVehicleInformation(prefs.userID.toInt(), it.vmRegNo)
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
                loadingDialog.show()
                rideAlongApiCall()
                val locNames = locationData.map { it.LocationName }
                val locIds = locationData.map { it.LocId }
                setSpinners(
                    binding.spinnerLocation,
                    locNames,
                    locIds
                )
            }
        }
        viewModel.GetRouteLocationInfo(locID)
    }

    private fun rideAlongApiCall() {
        viewModel.liveDataRideAlongRouteTypeInfo.observe(viewLifecycleOwner) { routeData ->
            loadingDialog.cancel()
            if (routeData != null) {
                val routeNames = routeData.map { it.RtName }
                val routeIDs = routeData.map { it.RtId }
                setSpinners(binding.spinnerRouteType, routeNames, routeIDs)
            }
        }
        viewModel.GetRideAlongRouteTypeInfo(prefs.userID.toInt())
    }

    private fun setSpinners(spinner: Spinner, items: List<String>, ids: List<Int>) {

        val dummyItem = "Select Item"
        val itemsList = mutableListOf(dummyItem)
        itemsList.addAll(items)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //adapter.addAll(itemsList)

        spinner.adapter = adapter

        spinner.setSelection(0)


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
                        selectedItem.let {
                            when (spinner) {
                                binding.spinnerLocation -> {
                                    selectedLocId = ids[position - 1]
                                    selectedLocation = selectedItem
                                }

                                binding.spinnerRouteType -> {
                                    selectedRouteType = selectedItem
                                    selectedRouteId = ids[position - 1]
                                }
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}
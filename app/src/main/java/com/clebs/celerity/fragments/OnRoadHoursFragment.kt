package com.clebs.celerity.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentOnRoadHoursBinding
import com.clebs.celerity.models.requests.AddOnRouteInfoRequest
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getLoc
import com.clebs.celerity.utils.getVRegNo
import com.clebs.celerity.utils.showToast


class OnRoadHoursFragment : Fragment() {
    private var selectedLocId: Int = 0
    private var selectedRouteId: Int = 0
    lateinit var binding: FragmentOnRoadHoursBinding
    lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    private var locID: Int = 0

    private var selectedRouteType: String? = null
    private var routeName: String? = null
    private var parcelsDelivered: String = "0"
    private var numberbroghtbac: String = "0"
    private var totalMileage: String = "0"
    private var routeComment: String? = null
    private var dwID: Int = 0
    private var parcelBack = 0
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

    private fun chkNotNullInputs(): Int {
        if (selectedRouteType.isNullOrEmpty() ||
            routeName.isNullOrEmpty() ||
            parcelsDelivered.isEmpty()
            || totalMileage.isEmpty()
        ) {
            if (routeName.isNullOrBlank())
                showToast("Please add route name.", requireContext())
            else if (selectedRouteType.isNullOrEmpty())
                showToast("Please select route type.", requireContext())
            else if(parcelsDelivered.isEmpty())
                showToast("Please add parcels delivered.", requireContext())
            else if(totalMileage.isEmpty())
                showToast("Please add total mileage.",requireContext())
            else
                showToast("Please fill all fields.",requireContext())

            return -1
        }
        return 1
    }

    private fun init() {
        viewModel = (activity as HomeActivity).viewModel
        prefs = Prefs.getInstance(requireContext())
        loadingDialog = (activity as HomeActivity).loadingDialog
//        parcelBack = binding.parcelsBroughtBack.text.toString().toInt()

//        binding.pbbPlus.setOnClickListener {
//            parcelBack += 1
//            binding.parcelsBroughtBack.text = parcelBack.toString()
//        }
//
//        binding.pbbMinus.setOnClickListener {
//
//            if (parcelBack > 0) {
//                parcelBack -= 1
//                binding.parcelsBroughtBack.text = parcelBack.toString()
//            }
//        }

        viewModel.livedataDailyWorkInfoByIdResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                dwID = it.DailyWorkId
                vehicleInfoSection()
            }
        }
        rideAlongApiCall()
        "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
            binding.headerTop.anaCarolin.text = name
        }
        binding.headerTop.dxLoc.text = getLoc(prefs = Prefs.getInstance(requireContext()))
        binding.headerTop.dxReg.text = getVRegNo(prefs = Prefs.getInstance(requireContext()))

        if (binding.headerTop.dxReg.text.isEmpty() || binding.headerTop.dxReg.text == "")
            binding.headerTop.strikedxRegNo.visibility = View.VISIBLE
        else
            binding.headerTop.strikedxRegNo.visibility = View.GONE

        if (binding.headerTop.dxLoc.text.isEmpty() || binding.headerTop.dxLoc.text == "" || binding.headerTop.dxLoc.text == "Not Allocated")
            binding.headerTop.strikedxLoc.visibility = View.VISIBLE
        else
            binding.headerTop.strikedxLoc.visibility = View.GONE
        binding.headerTop.dxm5.text = (activity as HomeActivity).date

        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (Prefs.getInstance(requireContext()).currLocationName.isNotEmpty()) {
                binding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).currLocationName ?: ""
            } else if (Prefs.getInstance(requireContext()).workLocationName.isNotEmpty()) {
                binding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).workLocationName ?: ""
            } else {
                if (it != null) {
                    binding.headerTop.dxLoc.text = it.locationName ?: ""
                }
            }
            if (it != null) {
                prefs.vmRegNo = it.vmRegNo ?: ""
            }

            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                binding.headerTop.anaCarolin.text = name
            }
            binding.headerTop.dxm5.text = (activity as HomeActivity).date
            binding.headerTop.dxLoc.text = getLoc(prefs = Prefs.getInstance(requireContext()))
            binding.headerTop.dxReg.text = getVRegNo(prefs = Prefs.getInstance(requireContext()))


            if (binding.headerTop.dxReg.text.isEmpty() || binding.headerTop.dxReg.text == "")
                binding.headerTop.strikedxRegNo.visibility = View.VISIBLE
            else
                binding.headerTop.strikedxRegNo.visibility = View.GONE

            if (binding.headerTop.dxLoc.text.isEmpty() || binding.headerTop.dxLoc.text == "" || binding.headerTop.dxLoc.text == "Not Allocated")
                binding.headerTop.strikedxLoc.visibility = View.VISIBLE
            else
                binding.headerTop.strikedxLoc.visibility = View.GONE
            binding.headerTop.dxm5.text = (activity as HomeActivity).date
        }
        inputListeners()
        viewModel.GetDailyWorkInfoById(prefs.clebUserId.toInt())
        binding.cancel.setOnClickListener {
            findNavController().navigate(R.id.completeTaskFragment)
            findNavController().clearBackStack(R.id.completeTaskFragment)
        }
        binding.onRoadHoursSave.setOnClickListener {
            parcelsDelivered = binding.edtParcels.text.toString()
            numberbroghtbac = binding.parcelsBroughtBack.text.toString()
            totalMileage = binding.edtMileage.text.toString()
            if (numberbroghtbac.isEmpty()) {
                numberbroghtbac = "0"
            }
            if (parcelsDelivered.isEmpty())
                parcelsDelivered = "0"
            if (totalMileage.isEmpty())
                totalMileage = "0"
            if (chkNotNullInputs()==1)
                sendData()

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
        var locationID = 0

        locationID = if (prefs.workLocationId != 0)
            prefs.workLocationId
        else
            prefs.currLocationId


        viewModel.AddOnRouteInfo(
            AddOnRouteInfoRequest(
                RtAddMode = "A",
                RtComment = "$routeComment",
                RtTypeId = selectedRouteId,
                RtDwId = dwID,
                RtFinishMileage = totalMileage.toLongOrNull() ?: 0,
                RtLocationId = locationID,
                RtName = routeName!!,
                RtNoOfParcelsDelivered = parcelsDelivered?.toLongOrNull() ?: 0,

                RtNoParcelsbroughtback = numberbroghtbac.toInt(),
                RtUsrId = prefs.clebUserId.toInt(),
                VehicleId = prefs.vmId
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

                    locationSection()
                }
            }
            viewModel.GetDriversBasicInformation(
                Prefs.getInstance(App.instance).clebUserId.toDouble()
            ).observe(viewLifecycleOwner) {
                if (it != null) {
                    if (it.vmID != null && prefs.vmId == 0)
                        prefs.vmId = it.vmID
                    if (it.vmRegNo != null) {
                        prefs.vmRegNo = it.vmRegNo!!

                        if (it.workingLocationId != null)
                            prefs.workLocationId = it.workingLocationId
                        if (it.currentLocationId != null)
                            prefs.currLocationId = it.currentLocationId

                        try {
                            viewModel.GetVehicleInformation(
                                prefs.clebUserId.toInt(),
                                getVRegNo(prefs)
                            )
                        } catch (e: Exception) {
                            Log.e("GetVehicleInformation Exception", "$e")
                        }
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

                setSpinnerNew(
                    binding.spinnerLocation,
                    locNames,
                    locIds, "Select Route Type"
                )
            }
        }
        var locationID = 0

        locationID = if (prefs.workLocationId != 0)
            prefs.workLocationId
        else
            prefs.currLocationId

        viewModel.GetRouteLocationInfo(locationID)
    }

    private fun rideAlongApiCall() {
        viewModel.liveDataRouteTypeInfo.observe(viewLifecycleOwner) { routeData ->
            loadingDialog.cancel()
            if (routeData != null) {
                val routeNames = routeData.map { it.RtName }
                val routeIDs = routeData.map { it.RtId }
                setSpinnerNew(binding.spinnerRouteType, routeNames, routeIDs, "Select Route Type")
            }
        }
        viewModel.GetDriverRouteTypeInfo(prefs.clebUserId.toInt())
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

    private fun setSpinnerNew(
        spinner: AutoCompleteTextView,
        items: List<String>,
        ids: List<Int>, dummyItem: String,
    ) {
        val itemsList = mutableListOf<String>()
        itemsList.addAll(items)
        val adapter =
            ArrayAdapter(requireContext(), R.layout.dropdown_menu_popup_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.setAdapter(adapter)
        spinner.setOnItemClickListener { parent, view, position, id ->
            run {
                parent?.let { nonNullParent ->

                    val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                    selectedItem.let {
                        when (spinner) {
                            binding.spinnerLocation -> {
                                selectedLocId = ids[position]
                            }

                            binding.spinnerRouteType -> {
                                selectedRouteType = selectedItem
                                selectedRouteId = ids[position]
                            }
                        }
                    }

                }
            }
        }
    }
}
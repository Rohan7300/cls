package com.clebs.celerity.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentUpdateOnRoadHoursBinding
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponseItem
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getLoc
import com.clebs.celerity.utils.getVRegNo
import com.clebs.celerity.utils.showToast

class UpdateOnRoadHoursFragment : Fragment() {
    private var selectedLocId: Int = 0
    private var selectedRouteId: Int = 0
    lateinit var binding: FragmentUpdateOnRoadHoursBinding
    lateinit var viewModel: MainViewModel
    lateinit var prefs: Prefs
    private var locID: Int = 0
    private var selectedRouteType: String? = null
    private var routeName: String? = null
    private var parcelsDelivered: String = "0"
    private var totalMileage: String = "0"
    private var routeComment: String? = null
    private var dwID: Int = 0
    var routeTypeSelection: Int? = null
    private var vehID: Int = 0
    var rtID: Int = 0
    private var parcelBack = 0
    lateinit var edtRoutes: EditText
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.fragment_update_on_road_hours,
                    container,
                    false
                )
        }

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun chkNotNullInputs(): Boolean {
        return selectedRouteType.isNullOrEmpty() ||
                routeName.isNullOrEmpty() ||
                parcelsDelivered.isEmpty()
                || totalMileage.isEmpty()
    }

    private fun init() {
        viewModel = (activity as HomeActivity).viewModel
        prefs = Prefs.getInstance(requireContext())
        loadingDialog = (activity as HomeActivity).loadingDialog
        parcelBack = binding.parcelsBroughtBack.text.toString().toInt()

        var routeInfo = prefs.getDriverRouteInfoByDate()

        selectedLocId = prefs.getLocationID()

        if (routeInfo != null) {
            binding.parcelsBroughtBack.text = routeInfo.RtNoParcelsbroughtback.toString()
            binding.edtMileage.setText(routeInfo.RtFinishMileage.toString())
            //binding.edtRouteComment.setText(routeInfo.RtComment)
            binding.edtRoutesORH.setText(routeInfo.RtName)
            binding.edtParcels.setText(routeInfo.RtNoOfParcelsDelivered.toString())
            dwID = routeInfo.RtDwId
            vehID = routeInfo.VehicleId

            selectedRouteId = routeInfo.RtTypeId
            routeName = routeInfo.RtName
            if (routeInfo.RtComment == "null") {
                binding.edtRouteComment.setText(" ")

            }
            else{
                binding.edtRouteComment.setText(routeInfo.RtComment)
                routeComment =routeInfo.RtComment
            }

            rtID = routeInfo.RtId
        }

        rideAlongApiCall()

        binding.pbbPlus.setOnClickListener {
            parcelBack =  binding.parcelsBroughtBack.text.toString().toInt()
            parcelBack += 1
            binding.parcelsBroughtBack.text = parcelBack.toString()
        }

        binding.cancel.setOnClickListener {
            findNavController().navigate(R.id.completeTaskFragment)
            findNavController().clearBackStack(R.id.completeTaskFragment)
        }

        binding.pbbMinus.setOnClickListener {
            parcelBack =  binding.parcelsBroughtBack.text.toString().toInt()
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
                prefs.vmRegNo= it.vmRegNo ?: ""
                if(it.vmId!=0)
                    Prefs.getInstance(requireContext()).vmId = it.vmId
                if (binding.headerTop.dxReg.text.isEmpty() || binding.headerTop.dxReg.text == "")
                    binding.headerTop.strikedxRegNo.visibility = View.VISIBLE
                else
                    binding.headerTop.strikedxRegNo.visibility = View.GONE
                if (binding.headerTop.dxLoc.text.isEmpty() || binding.headerTop.dxLoc.text == "" || binding.headerTop.dxLoc.text == "Not Allocated")
                    binding.headerTop.strikedxLoc.visibility = View.VISIBLE
                else
                    binding.headerTop.strikedxLoc.visibility = View.GONE
            }
            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                binding.headerTop.anaCarolin.text = name
            }
            binding.headerTop.dxm5.text = (activity as HomeActivity).date
        }
        inputListeners()
        viewModel.GetDailyWorkInfoById(prefs.clebUserId.toInt())
        binding.onRoadHoursSave.setOnClickListener {
            parcelsDelivered = binding.edtParcels.text.toString()
            totalMileage = binding.edtMileage.text.toString()
            if (parcelsDelivered.isEmpty())
                parcelsDelivered = "0"
            if (totalMileage.isEmpty())
                totalMileage = "0"
            if (chkNotNullInputs()) {
                showToast("Please fill Route name.", requireContext())
            } else {
                sendData()
            }
        }
    }

    private fun sendData() {
        loadingDialog.show()
        viewModel.liveDataUpdateOnRouteInfo.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                showToast("Updated Successfully", requireContext())
                findNavController().navigate(R.id.completeTaskFragment)
            } else {
                showToast("Failed to Update", requireContext())
                findNavController().navigate(R.id.completeTaskFragment)
            }
        }

        var locationID = 0

        locationID = if (prefs.workLocationId != 0)
            prefs.workLocationId
        else
            prefs.currLocationId


        viewModel.UpdateOnRouteInfo(
            GetDriverRouteInfoByDateResponseItem(
                RtAddMode = "U",
                RtComment = "$routeComment",
                RtTypeId = selectedRouteId,
                RtDwId = dwID,
                RtFinishMileage = totalMileage?.toInt() ?: 0,
                RtLocationId = locationID,
                RtName = routeName!!,
                RtNoOfParcelsDelivered = parcelsDelivered?.toInt() ?: 0,
                RtNoParcelsbroughtback = binding.parcelsBroughtBack.text.toString().toInt(),
                RtUsrId = prefs.clebUserId.toInt(),
                VehicleId = prefs.vmId,
                RtId = rtID
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
                    R.id.edt_route_comment -> routeComment = value.toString()
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
                    if(it.vmId!=0)
                        Prefs.getInstance(requireContext()).vmId = it.vmId
                    prefs.saveLocationID(it.vmLocId)
                    locID = it.vmLocId
                    vehID = it.vmId
                    locationSection()
                }
            }
            viewModel.GetDriversBasicInformation(
                Prefs.getInstance(App.instance).clebUserId.toDouble()
            ).observe(viewLifecycleOwner) {
                if (it != null) {
                    if(it.vmID!=null && prefs.vmId==0)
                        prefs.vmId = it.vmID
                    it.vmRegNo?.let { it1 ->
                        prefs.vmRegNo = it1
                        viewModel.GetVehicleInformation(
                            Prefs.getInstance(requireContext()).clebUserId.toInt(),
                            getVRegNo(prefs)
                        )
                    }
                    prefs.workLocationName = it.workinglocation
                    prefs.currLocationName = it.currentlocation

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
                }
            }

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
                    locIds
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
                try {
                    //binding.selectDepartmentTIL.hint = routeNames[routeIDs.indexOf(selectedRouteId)]

                    binding.spinnerRouteType.setText(routeNames[routeIDs.indexOf(selectedRouteId)])
                    binding.spinnerRouteType.setSelection(routeIDs.indexOf(selectedRouteId))

                } catch (_: Exception) {

                }

                selectedRouteType = routeNames[routeIDs.indexOf(selectedRouteId)]
                setSpinnerNew(
                    binding.spinnerRouteType,
                    routeNames,
                    routeIDs,
                )
            }
        }
        viewModel.GetDriverRouteTypeInfo(prefs.clebUserId.toInt())
    }


    private fun setSpinnerNew(
        spinner: AutoCompleteTextView,
        items: List<String>,
        ids: List<Int>
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
                                binding.selectDepartmentTIL.hint = "Select route type"
                                selectedLocId = ids[position]
                            }

                            binding.spinnerRouteType -> {
                                binding.selectDepartmentTIL.hint = "Select route type"
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
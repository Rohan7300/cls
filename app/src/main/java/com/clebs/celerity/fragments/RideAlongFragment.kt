package com.clebs.celerity.fragments

import android.annotation.SuppressLint
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
import com.clebs.celerity.databinding.FragmentRideAlongBinding
import com.clebs.celerity.models.requests.AddOnRideAlongRouteInfoRequest
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast


class RideAlongFragment : Fragment() {
    lateinit var binding: FragmentRideAlongBinding
    lateinit var viewModel: MainViewModel
    var selectedDriverId: Int? = null
    var selectedDriverName = ""
    var selectedVehicleId: Int? = null
    var selectedVehicleName = ""
    var selectedRouteId: Int? = null
    private lateinit var pref: Prefs
    private var isSpinnerTouched = false

    var rtAddMode: String = "A"
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
    private var vehicleListCalled: Boolean = false
    private var rtNoParcelsbroughtback: Int? = null
    lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_ride_along, container, false)

        viewModel = (activity as HomeActivity).viewModel
        loadingDialog = (activity as HomeActivity).loadingDialog
        leadDriverID = (activity as HomeActivity).userId
        pref = Prefs.getInstance(requireContext())
        pref.submittedRideAlong = false

        clickListeners()
        observers()

        setInputListener(binding.edtParcels)
        setInputListener(binding.edtRouteComment)


        return binding.root
    }


    private fun observers() {
        "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
            binding.headerTop.anaCarolin.text = name
        }
        if (Prefs.getInstance(requireContext()).currLocationName != null) {
            binding.headerTop.dxLoc.text =
                Prefs.getInstance(requireContext()).currLocationName ?: ""
        } else if (Prefs.getInstance(requireContext()).workLocationName != null) {
            binding.headerTop.dxLoc.text =
                Prefs.getInstance(requireContext()).workLocationName ?: ""
        }
        binding.headerTop.dxReg.text = Prefs.getInstance(requireContext()).vmRegNo
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
            if (Prefs.getInstance(requireContext()).currLocationName != null) {
                binding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).currLocationName ?: ""
            } else if (Prefs.getInstance(requireContext()).workLocationName != null) {
                binding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).workLocationName ?: ""
            } else {
                if (it != null) {
                    binding.headerTop.dxLoc.text = it.locationName ?: ""
                }
            }
            if (it != null) {
                binding.headerTop.dxReg.text = it.vmRegNo ?: ""
            }
            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                binding.headerTop.anaCarolin.text = name
            }
            binding.headerTop.dxm5.text = (activity as HomeActivity).date

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

        viewModel.livedataGetRideAlongVehicleLists.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                val vehIds = it.mapNotNull { vehicleList -> vehicleList.VehicleId }
                val vehNames = it.mapNotNull { vehicleList -> vehicleList.VehicleName }

                if (vehNames.isNotEmpty() && vehIds.isNotEmpty()) {
                    //setSpinners(binding.spinnerSelectVehicle, vehNames, vehIds)
                    setSpinnerNew(binding.spinnerSelectVehicle, vehNames, vehIds, "Select Vehicle")
                }
            }
        }

        viewModel.livedataGetRideAlongDriversList.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                val driverId = it.map { drivers -> drivers.Id }
                val driverName = it.map { drivers -> drivers.Name }

                if (driverId.isNotEmpty() && driverName.isNotEmpty()) {
                    setSpinnerNew(
                        binding.spinnerSelectDriver, driverName, driverId, "Select Driver"
                    )
                }/*
                    setSpinners(
                    binding.spinnerSelectDriver, driverName, driverId
                )*/
                loadingDialog.cancel()

            }
        }

        viewModel.liveDataRideAlongRouteTypeInfo.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                val typeName = it.map { type -> type.RtName }
                val typeId = it.map { type -> type.RtId }

                if (typeName.isNotEmpty() && typeId.isNotEmpty()) {
                    setSpinnerNew(binding.SpinnerRouteType, typeName, typeId, "Select Route Type")
                }/*
                    setSpinners(
                        binding.SpinnerRouteType, typeName, typeId
                    )*/
            } else {
                Log.d("Exec", "NULL#1 ")
            }
        }

        viewModel.livedataGetRouteInfoById.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                loadingDialog.show()
                rtAddMode = it.RtAddMode
                viewModel.GetRouteLocationInfo(it.RtLocationId)
                if (selectedDriverId != null)
                    viewModel.GetRideAlongRouteInfoById(it.RtId, selectedDriverId!!)
            } else {
                Log.d("Exec", "NULL#2")
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
            } else {
                Log.d("Exec", "NULL#3")
            }
        }

        viewModel.liveDataRouteLocationResponse.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                val locationNames = it.map { loc -> loc.LocationName }
                val locationId = it.map { loc -> loc.LocId }

                if (locationNames.isNotEmpty() && locationId.isNotEmpty()) {
                    setSpinnerNew(
                        binding.spinnerRouteLocation,
                        locationNames,
                        locationId,
                        "Select Route Location"
                    )
                }
                /*                    setSpinners(
                                    binding.spinnerRouteLocation,
                                    locationNames,
                                    locationId
                                )*/

            } else {
                Log.d("Exec", "NULL#4")
            }
        }

        viewModel.livedataRideAlongSubmitApiRes.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (pref.submittedRideAlong) {
                if (it != null) {
                    findNavController().navigate(R.id.completeTaskFragment)
                } else {
                    showToast("Please!! try again.", requireContext())
                }
            }

        }
    }


    private fun clickListeners() {
        var isReTrainingSelected = false
        var isTrainingSelected = false

        viewModel.GetRideAlongDriversList()
        if (!vehicleListCalled) {

            viewModel.GetRideAlongVehicleLists()
        }

        binding.rideAlongCancel.setOnClickListener {
            findNavController().navigate(R.id.completeTaskFragment)
            findNavController().clearBackStack(R.id.completeTaskFragment)
        }
        binding.saveBT.setOnClickListener {
            if (chkNull()) showToast("Please fill all fields!!", requireContext())
            else rideAlongApi()
        }
        binding.rbReTraining.setOnClickListener {
            isReTrainingSelected = !isReTrainingSelected
            binding.rbReTraining.isChecked = isReTrainingSelected
            retraining = true
            training = false
            binding.rbTraining.isChecked = false
        }

        binding.rbTraining.setOnClickListener {
            isTrainingSelected = !isTrainingSelected
            isReTrainingSelected = false
            retraining = false
            training = true
            binding.rbReTraining.isChecked = false
            binding.rbTraining.isChecked = isTrainingSelected
        }
    }


    private fun rideAlongApi() {
        pref.submittedRideAlong = true
        loadingDialog.show()
        viewModel.AddOnRideAlongRouteInfo(
            AddOnRideAlongRouteInfoRequest(
                IsReTraining = retraining!!,
                LeadDriverId = leadDriverID!!,
                RtAddMode = rtAddMode,
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setSpinners(spinner: Spinner, items: List<String>, ids: List<Int>) {
        val dummyItem = "Select Item"
        val itemsList = mutableListOf<String>()
        itemsList.addAll(items)
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, itemsList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        spinner.setOnTouchListener { _, _ ->
            isSpinnerTouched = true
            false
        }

        // spinner.setSelection(0)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (!isSpinnerTouched) return
                parent?.let { nonNullParent ->
                    if (position != 0) {
                        val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                        selectedItem.let { nonNullSelectedItem ->
                            when (spinner) {
                                binding.spinnerSelectDriver -> {
                                    selectedDriverId =
                                        ids[position - 1]
                                    selectedDriverName = nonNullSelectedItem
                                    loadingDialog.show()
                                    Log.d("Exec", "SelectedDriverID $selectedDriverId")
                                    viewModel.GetRideAlongRouteTypeInfo(selectedDriverId!!)
                                }

                                binding.spinnerSelectVehicle -> {
                                    selectedVehicleName = nonNullSelectedItem
                                    selectedVehicleId =
                                        ids[position - 1]
                                }

                                binding.SpinnerRouteType -> {
                                    selectedRouteId =
                                        ids[position - 1]
                                    loadingDialog.show()
                                    viewModel.GetRouteInfoById(selectedRouteId!!)
                                }

                                binding.spinnerRouteLocation -> {
                                    selectedLocId =
                                        ids[position - 1]
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
                    selectedItem.let { nonNullSelectedItem ->
                        when (spinner) {
                            binding.spinnerSelectDriver -> {
                                selectedDriverId =
                                    ids[position]
                                selectedDriverName = nonNullSelectedItem
                                loadingDialog.show()
                                Log.d("Exec", "SelectedDriverID $selectedDriverId")
                                viewModel.GetRideAlongRouteTypeInfo(selectedDriverId!!)
                            }

                            binding.spinnerSelectVehicle -> {
                                selectedVehicleName = nonNullSelectedItem
                                selectedVehicleId =
                                    ids[position]
                            }

                            binding.SpinnerRouteType -> {
                                selectedRouteId =
                                    ids[position]
                                loadingDialog.show()
                                viewModel.GetRouteInfoById(selectedRouteId!!)
                            }

                            binding.spinnerRouteLocation -> {
                                selectedLocId =
                                    ids[position]
                            }
                        }
                    }

                }
            }
        }
    }
}
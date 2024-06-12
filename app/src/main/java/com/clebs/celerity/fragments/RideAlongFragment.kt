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
import android.widget.RadioButton
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentRideAlongBinding
import com.clebs.celerity.models.requests.AddOnRideAlongRouteInfoRequest
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getLoc
import com.clebs.celerity.utils.getLocID
import com.clebs.celerity.utils.getVRegNo
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
    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }
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
        leadDriverID = (activity as HomeActivity).clebuserID
        pref = Prefs.getInstance(requireContext())
        pref.submittedRideAlong = false

        clickListeners()

        observers()
        showDialog()
        viewModel.GetRideAlongDriversList()
        showDialog()
        viewModel.GetRouteLocationInfo(getLocID(Prefs.getInstance(requireContext())))
        showDialog()
        viewModel.GetRideAlongRouteTypeInfo(leadDriverID!!)


        binding.SpinnerRouteType.setText("")
        binding.SpinnerRouteType.setAdapter(null)
        binding.spinnerRouteLocation.setAdapter(null)
        binding.spinnerRouteLocation.setText("")
        setInputListener(binding.edtParcels)
        setInputListener(binding.edtRouteComment)

        return binding.root
    }


    private fun observers() {
        /*        "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
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
                    hideDialog()
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
                        pref.vmRegNo = it.vmRegNo ?: ""
                        if(it.vmId!=0)
                            Prefs.getInstance(requireContext()).vmId = it.vmId
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
                }*/



        viewModel.livedataGetRideAlongVehicleLists.observe(viewLifecycleOwner) {
            hideDialog()
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

            if (it != null) {
                val driversList = it.map { driver -> driver.Name to driver.Id }

                // Sort the list by driver name
                val sortedDriversList = driversList.sortedBy { pair -> pair.first }

                // Separate the sorted pairs into individual lists
                val sortedDriverNames = sortedDriversList.map { pair -> pair.first }
                val sortedDriverIds = sortedDriversList.map { pair -> pair.second }

                if (sortedDriverIds.isNotEmpty() && sortedDriverNames.isNotEmpty()) {
                    hideDialog()
                    /*       binding.SpinnerRouteType.setText("")
                           selectedRouteId = null
                           binding.SpinnerRouteType.setAdapter(null)
                           selectedLocId = null*/
                    setSpinnerNew(binding.spinnerSelectDriver, sortedDriverNames, sortedDriverIds, "Select Driver")
                }else{
                    hideDialog()
                }
            }else{
                hideDialog()
            }
        }

        viewModel.liveDataRideAlongRouteTypeInfo.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                val typeName = it.map { type -> type.RtName }
                val typeId = it.map { type -> type.RtId }

                if (typeName.isNotEmpty() && typeId.isNotEmpty()) {
                    setSpinnerNew(binding.SpinnerRouteType, typeName, typeId, "Select Route Type")
                }
            }
        }

        /*        viewModel.livedataGetRouteInfoById.observe(viewLifecycleOwner) {
                    hideDialog()
                    if (it != null) {
                        showDialog()
                        rtAddMode = it.RtAddMode

                        if (selectedDriverId != null)
                            viewModel.GetRideAlongRouteInfoById(it.RtId, selectedDriverId!!)
                    } else {
                        Log.d("Exec", "NULL#2")
                    }
                }*/

        viewModel.livedataRideAlongRouteInfoById.observe(viewLifecycleOwner) {
            hideDialog()
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
            hideDialog()
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

            } else {
                Log.d("Exec", "NULL#4")
                setSpinnerNew(
                    binding.spinnerRouteLocation,
                    listOf(),
                    listOf(),
                    "Select Route Location"
                )
            }
        }

        viewModel.livedataRideAlongSubmitApiRes.observe(viewLifecycleOwner) {
            hideDialog()
            if (pref.submittedRideAlong) {
                if (it != null) {
                    findNavController().navigate(R.id.newCompleteTaskFragment)
                } else {
                    showToast("Please try again!.", requireContext())
                }
            }
        }
    }


    private fun clickListeners() {
        /*        if (!vehicleListCalled) {
                    showDialog()
                    //viewModel.GetRideAlongVehicleLists()
                }*/

        binding.rideAlongCancel.setOnClickListener {
            findNavController().navigate(R.id.newCompleteTaskFragment)
            findNavController().clearBackStack(R.id.newCompleteTaskFragment)
        }
        binding.saveBT.setOnClickListener {
            if (chkNull() == 1)
                rideAlongApi()
        }

        binding.rbReTraining.setOnClickListener(::onRadioButtonClicked)
        binding.rbTraining.setOnClickListener(::onRadioButtonClicked)
    }


    private fun rideAlongApi() {
        pref.submittedRideAlong = true
        showDialog()
        viewModel.AddOnRideAlongRouteInfo(
            AddOnRideAlongRouteInfoRequest(
                IsReTraining = retraining!!,
                LeadDriverId = leadDriverID!!,
                RtAddMode = rtAddMode,
                RtComment = routeComment ?: " ",
                RtFinishMileage = rtFinishMileage!!,
                RtId = 0,
                RtLocationId = selectedLocId!!,
                RtName = routeName!!,
                RtNoOfParcelsDelivered = rtNoOfParcelsDelivered!!,
                RtNoParcelsbroughtback = rtNoParcelsbroughtback!!,
                RtType = selectedRouteId!!,
                RtUsrId = selectedDriverId!!,
                TrainingDays = 0,
                VehicleId = pref.vmId
            )
        )
    }

    private fun chkNull(): Int {
        if (listOf(
                selectedDriverId,
                selectedRouteId,
                selectedLocId,
                routeName,
                retraining,
                training,
                rtType,
                trainingDays,
                rtFinishMileage,
                rtNoOfParcelsDelivered,
                rtNoParcelsbroughtback
            ).any { it == null }
        ) {
            if(selectedDriverId == null)
                showToast("Please select driver.",requireContext())
            else if(selectedRouteId == null)
                showToast("Please select route type",requireContext())
            else if(selectedLocId == null)
                showToast("Please select route location",requireContext())
            else if(routeName.isNullOrBlank())
                showToast("Please add route name",requireContext())
            else if(retraining==null&&training==null)
                showToast("Select trainee type",requireContext())
            else
                showToast("Please fill all the fields",requireContext())
            return -1
        }
        return 1
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
        val itemIdMap = mutableMapOf<String, Int>()
        if (items.isEmpty() && ids.isEmpty()) {
            itemsList.add(dummyItem)
        } else {
            itemsList.addAll(items)
            for (i in items.indices) {
                itemIdMap[items[i]] = ids[i]
            }
        }
/*        if (items.isEmpty() && ids.isEmpty()) {
            itemsList.add(dummyItem)
        } else {
            itemsList.addAll(items) // Add items to the list
        }*/

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_menu_popup_item,
            itemsList
        )
        spinner.setAdapter(adapter)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        spinner.setAdapter(adapter)
        spinner.setOnItemClickListener { parent, view, position, id ->
            run {
                parent?.let { nonNullParent ->
                    val selectedItem = "${nonNullParent.getItemAtPosition(position) ?: ""}"
                    selectedItem.let { nonNullSelectedItem ->
                        val selectedId = itemIdMap[nonNullSelectedItem]
                        when (spinner) {
                            binding.spinnerSelectDriver -> {
                                selectedDriverId = selectedId ?: -1
                                selectedDriverName = nonNullSelectedItem
                                Log.d("Exec", "SelectedDriverID $selectedDriverId")
                            }

                            binding.spinnerSelectVehicle -> {
                                selectedVehicleName = nonNullSelectedItem
                                selectedVehicleId =
                                    ids[position]
                            }

                            binding.SpinnerRouteType -> {
                                selectedRouteId =
                                    ids[position]
                                showDialog()
                                viewModel.GetRideAlongRouteInfoById(
                                    selectedRouteId!!,
                                    pref.clebUserId.toInt()!!
                                )
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

    fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked

        when (view.id) {
            R.id.rbReTraining -> {
                if (checked) {
                    binding.rbTraining.isChecked = false
                    retraining = true
                    training = false

                }
            }

            R.id.rbTraining -> {
                if (checked) {
                    retraining = false
                    training = true
                    binding.rbReTraining.isChecked = false
                }
            }
        }
    }


}
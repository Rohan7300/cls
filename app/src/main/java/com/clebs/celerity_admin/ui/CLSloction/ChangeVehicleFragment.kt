package com.clebs.celerity_admin.ui.CLSloction

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.MainActivityTwo
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.CompanyListAdapter
import com.clebs.celerity_admin.adapters.DriverListAdapter
import com.clebs.celerity_admin.adapters.ReturnVehicleAdapter
import com.clebs.celerity_admin.adapters.SelectVehicleLocationAdapter
import com.clebs.celerity_admin.databinding.FragmentGalleryBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.CompanyListResponseItem
import com.clebs.celerity_admin.models.DriverListResponseModelItem
import com.clebs.celerity_admin.models.VehicleReturnModelListItem
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.FabClick
import com.clebs.celerity_admin.utils.OnItemClickRecyclerView
import com.clebs.celerity_admin.utils.OnReturnVehicle
import com.clebs.celerity_admin.utils.Onclick
import com.clebs.celerity_admin.utils.OnclickDriver
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.setupFullHeight
import com.clebs.celerity_admin.utils.setupHalfHeight
import com.clebs.celerity_admin.viewModels.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import de.hdodenhof.circleimageview.CircleImageView
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ChangeVehicleFragment : Fragment(), Onclick, OnclickDriver, FabClick, OnReturnVehicle,
    OnItemClickRecyclerView {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var inspectionID: String
    private lateinit var regexPattern: Regex
   var companynames: String?=null
    private var datextinputlayout: TextInputLayout? = null
    private var returntextinputlayout: TextInputLayout? = null
    private var edt_layout:TextInputLayout?=null
    private var requesttype: TextInputLayout? = null
    lateinit var deleteDialog: AlertDialog
    lateinit var deleteDialogtwo: AlertDialog
    lateinit var deleteDialogthree: AlertDialog
    lateinit var DriverListAdapter: DriverListAdapter
    lateinit var ReturnVehicleAdapter: ReturnVehicleAdapter
    var spinner: Spinner? = null

    private var isfirst: Boolean? = false
    lateinit var Activity: MainActivityTwo
    private var startonetime: Boolean? = false
    private var companyname: EditText? = null
    lateinit var selectVehcilelocationadapter: SelectVehicleLocationAdapter
    private var returnvehiclename: EditText? = null
    lateinit var llms: LinearLayout
    lateinit var rv_select_type: RecyclerView
    lateinit var llmtwo: LinearLayout
    lateinit var llmthree: LinearLayout
    private var list = ArrayList<CompanyListResponseItem>()
    lateinit var textView: TextView
    private var radioButtonDA: RadioButton? = null
    private var radioButtonreturn: RadioButton? = null
    private var radioButtonworthy: RadioButton? = null
    private var radioButtonnotworthy: RadioButton? = null
    private var daname: EditText? = null
    private var edt: EditText? = null
    private var rv_locatio: RecyclerView? = null
    private var returnname: EditText? = null
    private val items = ArrayList<DriverListResponseModelItem>()
    private val itemstwo = ArrayList<VehicleReturnModelListItem>()
    lateinit var mainViewModel: MainViewModel
    lateinit var attachmentAdapter: CompanyListAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        cqSDKInitializer = CQSDKInitializer(requireContext())
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)


        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        startonetime = isfirst!!
        Prefs.getInstance(requireContext()).Isfirst = true
        attachmentAdapter = CompanyListAdapter(ArrayList(), this)
        DriverListAdapter = DriverListAdapter(ArrayList(), this)
        ReturnVehicleAdapter = ReturnVehicleAdapter(ArrayList(), this)
        selectVehcilelocationadapter = SelectVehicleLocationAdapter(ArrayList(), this)

        isfirst = Prefs.getInstance(requireContext()).Isfirst


        binding.pb2.visibility = View.VISIBLE
        binding.constmain.alpha = 0.5f
        Observers()






        cqSDKInitializer.checkOfflineQuoteSyncCompleteStatus() { isSyncCompletedForAllQuotes ->
            if (isSyncCompletedForAllQuotes) {
//                Toast.makeText(
//                    requireContext(),
//                    "Vehicle pictures are uploaded",
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        }

        clientUniqueID()
        val textView3: CircleImageView = binding.textView3
        binding.textView4.setOnClickListener {
            selectVehicleInformation()

        }
        binding.textView5.setOnClickListener {

            uploadVehiclePicture()
        }
        binding.textView6.setOnClickListener {
            bottom_sheetVanHire()
        }
        textView3.setOnClickListener {
            selectVehicleOptions()
        }


        return root
    }

    fun Observers() {

        mainViewModel.GetCompanyListing().observe(viewLifecycleOwner, Observer {
            Log.e("dkfdjkfjkdfjdresponse", "onCreateView: " + it)
            if (it != null) {
                binding.pb2.visibility = View.GONE
                binding.constmain.alpha = 1f
                attachmentAdapter.data.addAll(it)

                list.addAll(it)

                attachmentAdapter.notifyDataSetChanged()
            } else {
                binding.pb2.visibility = View.GONE
                binding.constmain.alpha = 1f
            }


        })
        mainViewModel.GetDriverListing().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.pb2.visibility = View.GONE
                binding.constmain.alpha = 1f
                items.addAll(it)
                DriverListAdapter.data.addAll(it)
                DriverListAdapter.notifyDataSetChanged()
            } else {
                binding.pb2.visibility = View.GONE
                binding.constmain.alpha = 1f
            }
        })
        mainViewModel.GetVehicleListing().observe(viewLifecycleOwner, Observer {

            if (it != null) {
                binding.pb2.visibility = View.GONE
                itemstwo.addAll(it)
                binding.constmain.alpha = 1f
                ReturnVehicleAdapter.data.addAll(it)
                ReturnVehicleAdapter.notifyDataSetChanged()
            } else {

            }
        })

        mainViewModel.GetVehicleLocationListing().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.pb2.visibility = View.GONE

                binding.constmain.alpha = 1f
                selectVehcilelocationadapter.data.addAll(it)
                selectVehcilelocationadapter.notifyDataSetChanged()
            } else {
                binding.pb2.visibility = View.GONE
                binding.constmain.alpha = 1f
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun selectVehicleOptions() {

        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val showSheet = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.bottom_sheet_layout,
                requireActivity().findViewById<View>(R.id.bottomSheetRealCL) as ConstraintLayout?
            )
        companyname = showSheet.findViewById(R.id.edt_company)
        daname = showSheet.findViewById(R.id.edt_change_da)

        spinner = showSheet.findViewById(R.id.sp1)

        llms = showSheet.findViewById(R.id.llm)
        llmtwo = showSheet.findViewById(R.id.llmtwo)
        llmthree = showSheet.findViewById(R.id.llmthree)

        textView = showSheet.findViewById(R.id.txt_info)
        returnvehiclename = showSheet.findViewById(R.id.edt_return_da)
        radioButtonDA = showSheet.findViewById(R.id.rbReTraining)
        radioButtonreturn = showSheet.findViewById(R.id.rbTraining)
        radioButtonworthy = showSheet.findViewById(R.id.roadworthy)
        radioButtonnotworthy = showSheet.findViewById(R.id.notworthys)


        datextinputlayout = showSheet.findViewById(R.id.edt_layout_two)
        edt_layout=showSheet.findViewById(R.id.edt_layout)
        returntextinputlayout = showSheet.findViewById(R.id.edt_layout_return)
        requesttype = showSheet.findViewById(R.id.edt_layout_three)
        rv_select_type = showSheet.findViewById(R.id.rv_select_type)





        radioButtonDA?.setOnClickListener(::onRadioButtonClicked)
        radioButtonreturn?.setOnClickListener(::onRadioButtonClicked)
        radioButtonworthy?.setOnClickListener(::onRadioButtonClicked)
        radioButtonnotworthy?.setOnClickListener(::onRadioButtonClicked)


        companyname?.isEnabled = true
        companyname?.isClickable = true
        companyname?.isFocusable = false
        companyname?.isFocusableInTouchMode = false

        returnvehiclename?.isEnabled = true
        returnvehiclename?.isClickable = true
        returnvehiclename?.isFocusable = false
        returnvehiclename?.isFocusableInTouchMode = false


        daname?.isEnabled = true
        daname?.isClickable = true
        daname?.isFocusable = false
        daname?.isFocusableInTouchMode = false
        val adapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_dropdown_item, list.map {

                it.name })
        spinner?.adapter = adapter

        companyname?.setOnClickListener {
//            showcompanylistdialog()


        spinner?.performClick()



        }

        spinner?.onItemSelectedListener=object :AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

//                companyname?.setText(parent?.selectedItem.toString())

                companynames=parent?.selectedItem.toString()
                spinner?.visibility=View.VISIBLE
                llms.visibility = View.VISIBLE
                Log.e("slevhdsd", "onItemSelected: "+companynames )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinner?.visibility=View.GONE
            }
        }

//        val spinnerPosition = adapter.getPosition(spinner?.selectedItem.toString())
//        companyname?.setText(list[spinnerPosition].name)



        daname?.setOnClickListener {
            ShowDriverListDialog()
        }
        returnvehiclename?.setOnClickListener {
            ShowReturnVehicleList()
        }
        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
        val textView: TextView = showSheet.findViewById(R.id.bt_change)
        textView.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.textView3.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_green
                )
            )
//            binding.down.setImageDrawable(
//                ContextCompat.getDrawable(
//                    requireContext(),
//                    R.drawable.baseline_check_24
//                )
//            )
        }
        imageView.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(showSheet)
        setupHalfHeight(bottomSheetDialog, requireContext())
//        setupFullHeight(bottomSheetDialog, requireContext())

        bottomSheetDialog.show()
    }

    private fun selectVehicleInformation() {
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val showSheet = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.bottom_sheet_vehicle_info,
                requireActivity().findViewById<View>(R.id.bottomSheetRealCL) as CoordinatorLayout?
            )

//        showSheet?.let {
//            val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(showSheet)
//            val layoutParams = showSheet.layoutParams
//            showSheet.layoutParams = layoutParams
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.isDraggable = false
//        }
        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
        edt = showSheet.findViewById(R.id.edt_company)

        rv_locatio = showSheet.findViewById(R.id.rv_vehicle_location)
        rv_locatio?.adapter = selectVehcilelocationadapter
        edt?.isEnabled = true
        edt?.isClickable = true
        edt?.isFocusable = false
        edt?.isFocusableInTouchMode = false

        edt?.setOnClickListener {

            rv_locatio?.visibility = View.VISIBLE
        }


        imageView.setOnClickListener {


            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(showSheet)
        setupFullHeight(bottomSheetDialog, requireContext())
//        setupFullHeight(bottomSheetDialog, requireContext())
        bottomSheetDialog.show()

    }

    private fun uploadVehiclePicture() {
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val showSheet = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.bottom_upload,
                requireActivity().findViewById<View>(R.id.bottomSheetRealCL) as ConstraintLayout?
            )
        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
        imageView.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.pb2.visibility = View.GONE
        }
        val textView: TextView = showSheet.findViewById(R.id.bt_upload)
        val progress: ProgressBar = showSheet.findViewById(R.id.pb1)
        progress.visibility = View.GONE
        textView.setOnClickListener {
            progress.visibility = View.VISIBLE
            binding.pb2.visibility = View.VISIBLE
            startInspection()
            bottomSheetDialog.dismiss()

        }

//        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
//        imageView.setOnClickListener {
//            bottomSheetDialog.dismiss()
//        }
        bottomSheetDialog.setContentView(showSheet)
//        setupFullHeight(bottomSheetDialog, requireContext())
//        setupFullHeight(bottomSheetDialog, requireContext())
        bottomSheetDialog.show()

    }

    private fun bottom_sheetVanHire() {
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val showSheet = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.bottom_sheet_van_hire,
                requireActivity().findViewById<View>(R.id.bottomSheetRealCL) as ConstraintLayout?
            )
        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
        imageView.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
//        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
//        imageView.setOnClickListener {
//            bottomSheetDialog.dismiss()
//        }
        bottomSheetDialog.setContentView(showSheet)
//        setupFullHeight(bottomSheetDialog, requireContext())
        setupFullHeight(bottomSheetDialog, requireContext())
        bottomSheetDialog.show()

    }

    private fun startInspection() {
//        if (isAllImageUploaded) {
//            mbinding.tvNext.visibility = View.VISIBLE
//        }

//      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {


        if (cqSDKInitializer.isCQSDKInitialized()) {

//            var vmReg = Prefs.getInstance(App.instance).scannedVmRegNo ?: ""
//            Log.e(
//                "totyototyotoytroitroi",
//                "startInspection: " + inspectionID + "VmReg ${Prefs.getInstance(App.instance).vmRegNo}"
//            )
//            if (vmReg.isEmpty()) {
//                vmReg = Prefs.getInstance(App.instance).vmRegNo
//            }
            Log.e("sdkskdkdkskdkskd", "onCreateView: ")

            try {
                cqSDKInitializer.startInspection(activity = requireActivity(),
                    clientAttrs = ClientAttrs(
                        userName = " ",
                        dealer = " ",
                        dealerIdentifier = " ",
                        client_unique_id = inspectionID

                        //drivers ID +vechile iD + TOdays date dd// mm //yy::tt,mm
                    ),
                    inputDetails = InputDetails(
                        vehicleDetails = VehicleDetails(
                            regNumber = "ND22YFL", //if sent, user can't edit
                            make = "Van", //if sent, user can't edit
                            model = "Any Model", //if sent, user can't edit
                            bodyStyle = "Van"  // if sent, user can't edit - Van, Boxvan, Sedan, SUV, Hatch, Pickup [case sensitive]
                        ),
                        customerDetails = CustomerDetails(
                            name = "", //if sent, user can't edit
                            email = "", //if sent, user can't edit
                            dialCode = "", //if sent, user can't edit
                            phoneNumber = "", //if sent, user can't edit
                        )
                    ),
                    userFlowParams = UserFlowParams(
                        isOffline = startonetime, // true, Offline quote will be created | false, online quote will be created | null, online

                        skipInputPage = true, // true, Inspection will be started with camera page | false, Inspection will be started

                    ),

                    result = { isStarted, msg, code ->

                        Log.e("messsagesss", "startInspection: " + msg + code)
                        if (isStarted) {
                            Log.e(
                                "kdfjdkfdkfjdkfdjfkddfkddinspetion",
                                "startInspection: " + inspectionID
                            )
                            binding.pb2.visibility = View.GONE
//                            Prefs.getInstance(App.instance).inspectionID = inspectionID
                        } else {

                            binding.pb2.visibility = View.GONE
//
                        }
                        if (msg == "Success") {
                            binding.pb2.visibility = View.GONE
                        } else {
                            binding.pb2.visibility = View.GONE
                        }
                        if (!isStarted) {
                            binding.pb2.visibility = View.GONE
                            Log.e("startedinspection", "onCreateView: " + msg + isStarted)
                        }
                    })
            } catch (_: Exception) {
                binding.pb2.visibility = View.GONE

            }
        }

    }

    fun clientUniqueID(): String {
        val x = "123456"
        val y = "123456"
        // example string
        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))

        regexPattern = Regex("${x.take(3)}${y.take(3)}${formattedDate}")
        inspectionID = regexPattern.toString()
        return regexPattern.toString()
        Log.e("resistrationvrnpatterhn", "clientUniqueID: " + inspectionID)
    }

    fun showcompanylistdialog() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.change_passwordninetydays, null)
        deleteDialog =
            AlertDialog.Builder(requireContext()).create()
        deleteDialog.setView(view)
        val list: RecyclerView = view.findViewById(R.id.rv_comapny)
        val close: ImageView = view.findViewById(R.id.iv_close)

        close.setOnClickListener {
            deleteDialog.dismiss()
        }
        list.adapter = attachmentAdapter



        deleteDialog.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                true
            } else {
                false
            }
        }


        deleteDialog.setCancelable(false)
        deleteDialog.setCanceledOnTouchOutside(false);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        deleteDialog.show();

    }

    fun ShowDriverListDialog() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.driver_list_dialog, null)
        deleteDialogtwo =
            AlertDialog.Builder(requireContext()).create()
        deleteDialogtwo.setView(view)
        val list: RecyclerView = view.findViewById(R.id.rv_driver)
        val close: ImageView = view.findViewById(R.id.iv_close)
        val searchView: SearchView = view.findViewById(R.id.IDsv)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterData(newText)
                return true
            }

        })


        close.setOnClickListener {
            deleteDialogtwo.dismiss()
        }
        list.adapter = DriverListAdapter



        deleteDialogtwo.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                true
            } else {
                false
            }
        }


        deleteDialogtwo.setCancelable(false)
        deleteDialogtwo.setCanceledOnTouchOutside(false);
        deleteDialogtwo.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        deleteDialogtwo.show();

    }

    override fun onItemClick(item: CompanyListResponseItem) {
        Log.e("dskmdsjfjd", "onItemClick: ")
        companyname?.setText(item.name)
        llms.visibility = View.VISIBLE
        deleteDialog.dismiss()
    }

    override fun onItemClick(item: DriverListResponseModelItem) {
        daname?.setText(item.name)
        textView.visibility = View.VISIBLE
        deleteDialogtwo.dismiss()
    }

    private fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked

        when (view.id) {
            R.id.rbReTraining -> {
                if (checked) {
                    radioButtonreturn?.isChecked = false
                    datextinputlayout?.visibility = View.VISIBLE
                    textView.visibility = View.GONE
                    llmtwo.visibility = View.GONE
                    llmthree.visibility = View.GONE

                    requesttype?.visibility=View.GONE

                    returnvehiclename?.setText("")
                    returntextinputlayout?.visibility = View.GONE

                }
            }

            R.id.rbTraining -> {
                if (checked) {
                    daname?.setText("")
                    radioButtonDA?.isChecked = false
                    datextinputlayout?.visibility = View.GONE
                    returntextinputlayout?.visibility = View.VISIBLE
                }
            }

            R.id.roadworthy -> {
                if (checked) {
                    radioButtonnotworthy?.isChecked = false
                    rv_select_type.visibility = View.GONE
                    requesttype?.visibility = View.GONE

                }
            }

            R.id.notworthys -> {
                if (checked) {
                    radioButtonworthy?.isChecked = false
                    rv_select_type.visibility = View.VISIBLE
                    requesttype?.visibility = View.VISIBLE
                }
            }
        }
    }

    fun ShowReturnVehicleList() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.vehicle_list_dialog, null)
        deleteDialogthree =
            AlertDialog.Builder(requireContext()).create()
        deleteDialogthree.setView(view)
        val list: RecyclerView = view.findViewById(R.id.rv_driver)
        val headings: TextView = view.findViewById(R.id.headings)
        headings.setText("Vehicle Listing")
        val close: ImageView = view.findViewById(R.id.iv_close)
        val searchView: SearchView = view.findViewById(R.id.IDsv)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterDatatwo(newText)
                return true
            }

        })


        close.setOnClickListener {
            deleteDialogthree.dismiss()
        }
        list.adapter = ReturnVehicleAdapter



        deleteDialogthree.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                true
            } else {
                false
            }
        }


        deleteDialogthree.setCancelable(false)
        deleteDialogthree.setCanceledOnTouchOutside(false);
        deleteDialogthree.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));

        deleteDialogthree.show();

    }

    private fun filterData(query: String?) {
        val filteredItems = if (query.isNullOrEmpty()) {
            items
        } else {

            items.filter {
                it.name.contains(
                    query,
                    ignoreCase = true
                )
            } // Replace 'name' with the appropriate property of your item
        }.toMutableList()


        DriverListAdapter.data = filteredItems as ArrayList<DriverListResponseModelItem>
        DriverListAdapter.notifyDataSetChanged()
    }

    private fun filterDatatwo(query: String?) {
        val filteredItems = if (query.isNullOrEmpty()) {
            itemstwo.toMutableList()
        } else {
            itemstwo.filter { item ->
                item.vehicleName?.contains(
                    query,
                    ignoreCase = true
                ) ?: false
            }.toMutableList()
        }

        // Update the adapter's data and notify the changes
        with(ReturnVehicleAdapter) {
            data = filteredItems as ArrayList<VehicleReturnModelListItem>
            notifyDataSetChanged()
        }
    }


    override fun onItemClick(item: VehicleReturnModelListItem) {
        returnvehiclename?.setText(item.vehicleName)
        textView.visibility = View.VISIBLE
        llmtwo.visibility = View.VISIBLE
        llmthree.visibility = View.VISIBLE
        deleteDialogthree.dismiss()
    }

    override fun onActivityViewClicked(view: View) {

    }

    override fun OnItemClickRecyclerViewClicks(
        recyclerViewId: Int,
        position: Int,
        selecteditemclicked: String
    ) {
        when (recyclerViewId) {
            R.id.rv_vehicle_location -> {
                edt?.setText(selecteditemclicked)
                rv_locatio?.visibility=View.GONE
                // Handle item click event for RecyclerView1
            }
//                 R.id.recyclerView2 -> {
//                     // Handle item click event for RecyclerView2
//                 }
//                 // Add more conditions for additional RecyclerViews
        }
    }


}
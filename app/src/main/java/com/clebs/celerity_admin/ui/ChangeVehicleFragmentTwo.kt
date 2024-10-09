package com.clebs.celerity_admin.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.compose.material.Snackbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.CompanyListAdapter
import com.clebs.celerity_admin.adapters.DriverListAdapter
import com.clebs.celerity_admin.adapters.FinalCompanyListAdapter
import com.clebs.celerity_admin.adapters.FuelLevelAdapter
import com.clebs.celerity_admin.adapters.ReturnVehicleAdapter
import com.clebs.celerity_admin.adapters.SelectVehicleLocationAdapter
import com.clebs.celerity_admin.adapters.VehicleOilLevelAdapter
import com.clebs.celerity_admin.databinding.FragmentChangeVehicleTwoBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.CompanyListResponseItem
import com.clebs.celerity_admin.models.DriverListResponseModelItem
import com.clebs.celerity_admin.models.GetVehicleRequestTypeItem
import com.clebs.celerity_admin.models.VehicleReturnModelListItem
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DependencyClass
import com.clebs.celerity_admin.utils.DependencyClass.VehicleAllocatedTODAID
import com.clebs.celerity_admin.utils.DependencyClass.crDASelectedVehicleType
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleIdDA
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleLocIdDA
import com.clebs.celerity_admin.utils.DependencyClass.selectedVehicleOilLevelListIdDA
import com.clebs.celerity_admin.utils.LottieDialog
import com.clebs.celerity_admin.utils.OnItemClickRecyclerView
import com.clebs.celerity_admin.utils.OnReturnVehicle
import com.clebs.celerity_admin.utils.Onclick
import com.clebs.celerity_admin.utils.OnclickDriver
import com.clebs.celerity_admin.utils.Prefs

import com.clebs.celerity_admin.viewModels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChangeVehicleFragmentTwo.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChangeVehicleFragmentTwo : Fragment(), Onclick, OnclickDriver, OnReturnVehicle,
    OnItemClickRecyclerView {
    lateinit var binding: FragmentChangeVehicleTwoBinding
    lateinit var mainViewModel: MainViewModel

    lateinit var attachmentAdapter: CompanyListAdapter
    lateinit var deleteDialog: AlertDialog
    private var isTextViewVisible: Boolean = false
    private var isTextViewVisibletwo: Boolean = false
    private var isTextViewVisibletwos: Boolean = false
    lateinit var deleteDialogtwo: AlertDialog
    private var errortext = String()
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private var vehLmId: Int? = null
    private lateinit var inspectionID: String
    lateinit var deleteDialogthree: AlertDialog
    private var checkChMessage: String? = null
    private var checkChMessagetwo: String? = null
    private var vehicleLocation: String? = null
    private var DriverHomeDepotId: Int? = null
    private var returbDA_wmID = String()
    var DOB = String()
    var vehicleType = String()
    var licensestart = String()
    var licenseEnd = String()
    var licenseNo = String()
    var vehiclelocation = String()
    private var startboolean: Boolean? = false
    var DA_id = String()
    var spinner: Spinner? = null
    private var list = ArrayList<CompanyListResponseItem>()
    private var listnew = ArrayList<GetVehicleRequestTypeItem>()
    private val items = ArrayList<DriverListResponseModelItem>()
    lateinit var DriverListAdapter: DriverListAdapter
    lateinit var ReturnVehicleAdapter: ReturnVehicleAdapter
    lateinit var selectVehcilelocationadapter: SelectVehicleLocationAdapter
    lateinit var FuellevelAdapter: FuelLevelAdapter
    lateinit var OillevelAdapter: VehicleOilLevelAdapter
    var companynames: String? = null
    var spinnerposition: Int? = -1
    private val itemstwo = ArrayList<VehicleReturnModelListItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChangeVehicleTwoBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        cqSDKInitializer = CQSDKInitializer(requireContext())
        binding.pb.visibility = View.VISIBLE
        mainViewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        attachmentAdapter = CompanyListAdapter(ArrayList(), this)
        DriverListAdapter = DriverListAdapter(ArrayList(), this)
        ReturnVehicleAdapter = ReturnVehicleAdapter(ArrayList(), this)
        selectVehcilelocationadapter = SelectVehicleLocationAdapter(ArrayList(), this)


        FuellevelAdapter = FuelLevelAdapter(ArrayList(), this)
        OillevelAdapter = VehicleOilLevelAdapter(ArrayList(), this)
        binding.rvVehicleFuellevel.adapter = FuellevelAdapter
        binding.rvVehicleOillevel.adapter = OillevelAdapter
        LottieDialog.show(requireActivity())
        CoroutineScope(Dispatchers.IO).async {
            withContext(Dispatchers.IO) {
                delay(3000)
                LottieDialog.dismiss()
            }
        }
        Observers()

        EditableFalse(binding.edtCompanys)
        EditableFalse(binding.spinnerSelectDA)
        EditableFalse(binding.spinnerSelectVehicle)
        EditableFalse(binding.spinnerVehicleFuelLevel)
        EditableFalse(binding.spinnerVehicleOilLevel)
        EditableFalse(binding.edtCompany)
        EditableFalse(binding.edtMileage)
        EditableFalse(binding.spinnerSelectVehicleLocation)
        EditableFalse(binding.edtFuel)
        binding.sp1.adapter = FinalCompanyListAdapter(
            list, this
        )
        if (startboolean!!) {
            binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
//            binding.constmain.visibility = View.VISIBLE
            binding.startVanHire.visibility = View.VISIBLE
        }
        binding.atvVehicleCurrentMileage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 0) {
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.GONE
                }
                if (binding.spinnerSelectVehicle.text.isNotEmpty() && binding.atvVehicleCurrentMileage.text.isNotEmpty() && binding.spinnerVehicleFuelLevel.text.isNotEmpty() && binding.atvAddBlueMileage.text.isNotEmpty() && binding.spinnerVehicleOilLevel.text.isNotEmpty()) {
                    binding.headeruploadVehicleInformation.visibility = View.VISIBLE
                    binding.llstartinspection.visibility = View.VISIBLE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 0) {
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.GONE
                }
                if (binding.spinnerSelectVehicle.text.isNotEmpty() && binding.atvVehicleCurrentMileage.text.isNotEmpty() && binding.spinnerVehicleFuelLevel.text.isNotEmpty() && binding.atvAddBlueMileage.text.isNotEmpty() && binding.spinnerVehicleOilLevel.text.isNotEmpty()) {

                    binding.llstartinspection.visibility = View.VISIBLE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
                }
            }

        })
        binding.atvAddBlueMileage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (count == 0) {
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.GONE
                }
                if (binding.spinnerSelectVehicle.text.isNotEmpty() && binding.atvVehicleCurrentMileage.text.isNotEmpty() && binding.spinnerVehicleFuelLevel.text.isNotEmpty() && binding.atvAddBlueMileage.text.isNotEmpty() && binding.spinnerVehicleOilLevel.text.isNotEmpty()) {

                    binding.llstartinspection.visibility = View.VISIBLE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 0) {
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.GONE
                }
                if (binding.spinnerSelectVehicle.text.isNotEmpty() && binding.atvVehicleCurrentMileage.text.isNotEmpty() && binding.spinnerVehicleFuelLevel.text.isNotEmpty() && binding.atvAddBlueMileage.text.isNotEmpty() && binding.spinnerVehicleOilLevel.text.isNotEmpty()) {

                    binding.llstartinspection.visibility = View.VISIBLE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
                }
            }

        })
        binding.startVanHire.setOnClickListener {
            if (binding.atvAddBlueMileage.text.toString()
                    .isNotEmpty() && binding.atvVehicleCurrentMileage.text.toString().isNotEmpty()
            ) {
                val intent = Intent(requireContext(), VanHireChangeDAActivity::class.java)
                intent.putExtra("DOB", DOB)
                intent.putExtra("LICENSENO", licenseNo)
                intent.putExtra("LICENSESTART", licensestart)
                intent.putExtra("LICENSEEND", licenseEnd)
                intent.putExtra("VehicleType", vehicleType)
                intent.putExtra("BLUEMILEAGE", binding.atvAddBlueMileage.text.toString())
                intent.putExtra("CURRENTMILEAGE", binding.atvVehicleCurrentMileage.text.toString())
                intent.putExtra("CLIENTUNIQUEID", inspectionID)
                startActivity(intent)
            } else {
                Toast.makeText(
                    requireContext(), "Please enter complete details", Toast.LENGTH_SHORT
                ).show()
            }

        }
        binding.edtCompanys.setOnClickListener {
//            showcompanylistdialog()


            if (!isTextViewVisible) {
//                viewVisibleAnimator(binding.sp1)
                binding.sp1.visibility = View.VISIBLE
                isTextViewVisible
            } else {
                binding.sp1.visibility = View.GONE
                !isTextViewVisible
            }

            isTextViewVisible = !isTextViewVisible

        }

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val text = parent!!.getItemAtPosition(position).toString()
//                companyname?.setText(parent?.selectedItem.toString())
                spinnerposition = position
                companynames = parent?.selectedItem.toString()
                spinner?.visibility = View.GONE
//                llms.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinner?.visibility = View.GONE
            }
        }
        binding.spinnerSelectDA.setOnClickListener {
            ShowDriverListDialog()

        }
        binding.spinnerSelectVehicle.setOnClickListener {

            ShowReturnVehicleList()
        }
        binding.spinnerVehicleFuelLevel.setOnClickListener {
            if (!isTextViewVisibletwo) {
//                viewVisibleAnimator(binding.sp1)
                binding.rvVehicleFuellevel.visibility = View.VISIBLE
                binding.scrollmain.post(Runnable { binding.scrollmain.fullScroll(View.FOCUS_DOWN) })
                isTextViewVisibletwo
            } else {
                binding.rvVehicleFuellevel.visibility = View.GONE
                !isTextViewVisibletwo
            }

            isTextViewVisibletwo = !isTextViewVisibletwo


        }

        binding.spinnerVehicleOilLevel.setOnClickListener {
            if (!isTextViewVisibletwos) {
                binding.scrollmain.post(Runnable { binding.scrollmain.fullScroll(View.FOCUS_DOWN) })

                binding.rvVehicleOillevel.visibility = View.VISIBLE
                isTextViewVisibletwos
            } else {
                binding.rvVehicleOillevel.visibility = View.GONE
                !isTextViewVisibletwos
            }

            isTextViewVisibletwos = !isTextViewVisibletwos

//            viewVisibleAnimator(  binding.rvVehicleOillevel)
        }
        if (binding.spinnerVehicleFuelLevel.text.isNotEmpty()) {
            binding.llstartinspection.visibility = View.VISIBLE
        }
        return root
    }

    fun Observers() {
        binding.pb.visibility = View.VISIBLE
        binding.nested.alpha = 0.5f
        mainViewModel.GetCompanyListing().observe(viewLifecycleOwner, Observer {
            Log.e("dkfdjkfjkdfjdresponse", "onCreateView: " + it)
            if (it != null) {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
//                attachmentAdapter.data.addAll(it)
                list.clear()
                list.addAll(it)

                attachmentAdapter.notifyDataSetChanged()
            } else {

                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
            }


        })
        binding.pb.visibility = View.VISIBLE
        binding.nested.alpha = 0.5f
        mainViewModel.GetDriverListing().observe(viewLifecycleOwner, Observer {
            if (it != null) {

                items.addAll(it)
                DriverListAdapter.data.addAll(it)
                DriverListAdapter.notifyDataSetChanged()
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
            } else {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
            }
        })
        binding.pb.visibility = View.VISIBLE
        binding.nested.alpha = 0.5f
        mainViewModel.GetVehicleListing().observe(viewLifecycleOwner, Observer {

            if (it != null) {
                itemstwo.addAll(it)
                vehicleType = it.get(0).VehicleType
                crDASelectedVehicleType = it.get(0).VehicleType
                ReturnVehicleAdapter.data.addAll(it)
                ReturnVehicleAdapter.notifyDataSetChanged()
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
            } else {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
            }
        })
        binding.pb.visibility = View.VISIBLE
        binding.nested.alpha = 0.5f
        mainViewModel.GetVehicleLocationListing().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
                selectVehcilelocationadapter.data.addAll(it)
                selectVehcilelocationadapter.notifyDataSetChanged()
            } else {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
            }
        })
        binding.pb.visibility = View.VISIBLE
        binding.nested.alpha = 0.5f
        mainViewModel.GetVehiclefuelListing().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
                FuellevelAdapter.data.addAll(it)

                FuellevelAdapter.notifyDataSetChanged()


            } else {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
            }

        })
        binding.pb.visibility = View.VISIBLE
        binding.nested.alpha = 0.5f
        mainViewModel.GetVehicleOilListing().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
                OillevelAdapter.data.addAll(it)
                OillevelAdapter.notifyDataSetChanged()


            } else {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f
            }

        })
        binding.pb.visibility = View.VISIBLE
        binding.nested.alpha = 0.5f
        mainViewModel.GetVehicleRequestTypeList().observe(viewLifecycleOwner, Observer {

            if (it != null) {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f

                attachmentAdapter.data.addAll(it)
                listnew.clear()
                listnew.addAll(it)

                attachmentAdapter.notifyDataSetChanged()
            } else {
                binding.pb.visibility = View.GONE
                binding.nested.alpha = 1f

            }
        })
//        LottieDialog.dismiss()
    }

    fun showcompanylistdialog() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.change_passwordninetydays, null)
        deleteDialog = AlertDialog.Builder(requireContext()).create()
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

    override fun OnItemClickRecyclerViewClicks(
        recyclerViewId: Int, position: Int, itemclicked: String, id: Int
    ) {
        when (recyclerViewId) {
            R.id.sp1 -> {
                binding.edtCompanys.setText(itemclicked)
                binding.sp1.visibility = View.GONE
                Prefs.getInstance(App.instance).companyId = id.toString()
                // Handle item click event for RecyclerView1
            }

            R.id.rv_vehicle_fuellevel -> {
                DependencyClass.selectedVehicleFuelIdDA = id
                binding.spinnerVehicleFuelLevel?.setText(itemclicked)
                if (binding.spinnerSelectVehicle.text.isNotEmpty() && binding.atvVehicleCurrentMileage.text.isNotEmpty() && binding.spinnerVehicleFuelLevel.text.isNotEmpty() && binding.atvAddBlueMileage.text.isNotEmpty() && binding.spinnerVehicleOilLevel.text.isNotEmpty()) {

                    binding.llstartinspection.visibility = View.VISIBLE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
                }
                binding.rvVehicleFuellevel?.visibility = View.GONE
            }

            R.id.rv_vehicle_oillevel -> {
                if (binding.spinnerSelectVehicle.text.isNotEmpty() && binding.atvVehicleCurrentMileage.text.isNotEmpty() && binding.spinnerVehicleFuelLevel.text.isNotEmpty() && binding.atvAddBlueMileage.text.isNotEmpty() && binding.spinnerVehicleOilLevel.text.isNotEmpty()) {

                    binding.llstartinspection.visibility = View.VISIBLE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
                }
                selectedVehicleOilLevelListIdDA = id
                binding.spinnerVehicleOilLevel?.setText(itemclicked)
                binding.rvVehicleOillevel.visibility = View.GONE


                if (binding.spinnerSelectVehicle.text.isNotEmpty() && binding.atvVehicleCurrentMileage.text.isNotEmpty() && binding.spinnerVehicleFuelLevel.text.isNotEmpty() && binding.atvAddBlueMileage.text.isNotEmpty() && binding.spinnerVehicleOilLevel.text.isNotEmpty()) {

                    binding.llstartinspection.visibility = View.VISIBLE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
                    binding.starts.setOnClickListener {
                        if (binding.spinnerSelectVehicle.text.isNotEmpty() && binding.atvVehicleCurrentMileage.text.isNotEmpty() && binding.spinnerVehicleFuelLevel.text.isNotEmpty() && binding.atvAddBlueMileage.text.isNotEmpty() && binding.spinnerVehicleOilLevel.text.isNotEmpty()) {

                            binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
                            binding.startVanHire.visibility = View.VISIBLE
                            startInspection()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please fill above Information first",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    binding.headerVehicleInformation.setBackgroundColor(requireContext().getColor(R.color.greenBtn))
                } else {
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformationVanhire.visibility = View.GONE
                    binding.headerVehicleInformation.setBackgroundColor(requireContext().getColor(R.color.blue_hex))
                }
            }
//                 R.id.recyclerView2 -> {
//                     // Handle item click event for RecyclerView2
//                 }
//                 // Add more conditions for additional RecyclerViews
        }
    }

    override fun onItemClick(item: VehicleReturnModelListItem) {

        Prefs.getInstance(App.instance).vmIdReturnveh = item.vehicleId.toString()
        if (binding.spinnerSelectVehicle.text.isNotEmpty() && binding.atvVehicleCurrentMileage.text.isNotEmpty() && binding.spinnerVehicleFuelLevel.text.isNotEmpty() && binding.atvAddBlueMileage.text.isNotEmpty() && binding.spinnerVehicleOilLevel.text.isNotEmpty()) {

            binding.llstartinspection.visibility = View.VISIBLE
            binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
        }
        selectedVehicleIdDA = item.vehicleId
        binding.spinnerSelectVehicleLocation.setText(item.LocationName)
        returbDA_wmID = item.vehicleId.toString()
        GetVehicleCurrentInsuranceInfo()
        selectedVehicleLocIdDA = item.LocationId
        binding.spinnerSelectVehicle.setText(item.vehicleRegNo)
//        llmtwo.visibility = View.VISIBLE
//        llmthree.visibility = View.VISIBLE
        deleteDialogthree.dismiss()
    }

    override fun onItemClick(item: GetVehicleRequestTypeItem) {

    }

    override fun onItemClick(item: DriverListResponseModelItem) {
        binding.spinnerSelectDA.setText(item.name)
        DA_id = item.id.toString()
        VehicleAllocatedTODAID = DA_id.toInt()
        DriverListAdapter
        getDDAMandate()





        deleteDialogtwo.dismiss()
    }

    fun ShowDriverListDialog() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.driver_list_dialog, null)
        deleteDialogtwo = AlertDialog.Builder(requireContext()).create()
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

    private fun filterData(query: String?) {
        val filteredItems = if (query.isNullOrEmpty()) {
            items
        } else {

            items.filter {
                it.name.contains(
                    query, ignoreCase = true
                )
            } // Replace 'name' with the appropriate property of your item
        }.toMutableList()



        DriverListAdapter.data = filteredItems as ArrayList<DriverListResponseModelItem>
        DriverListAdapter.notifyDataSetChanged()
    }

    fun ShowReturnVehicleList() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.vehicle_list_dialog, null)
        deleteDialogthree = AlertDialog.Builder(requireContext()).create()
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

        ReturnVehicleAdapter.notifyDataSetChanged()

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

    private fun filterDatatwo(query: String?) {
        val filteredItems = if (query.isNullOrEmpty()) {
            itemstwo.toMutableList()
        } else {
            itemstwo.filter { item ->
                item.vehicleRegNo?.contains(
                    query, ignoreCase = true
                ) ?: false
            }.toMutableList()
        }

        // Update the adapter's data and notify the changes
        with(ReturnVehicleAdapter) {
            data = filteredItems as ArrayList<VehicleReturnModelListItem>
            notifyDataSetChanged()
        }
    }

    fun EditableFalse(view: View) {

        view.isEnabled = true
        view.isClickable = true
        view.isFocusable = false
        view.isFocusableInTouchMode = false
    }

    fun EditableTrue(view: View) {
        view.isEnabled = true
        view.isClickable = true
        view.isFocusable = true
        view.isFocusableInTouchMode = true
    }

    fun getDDAMandate() {

        mainViewModel.GetDDAmandate(DA_id).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                checkChMessage = it.vehicleInfo.chMessage
                checkChMessagetwo = it.vehicleInfo.OnRoadVehicleAssignedToThisDriverMessage
                Prefs.getInstance(App.instance).vehLmId = it.vehicleInfo.vmRegId.toString()
                if (it.vehicleInfo.dOB.isNotEmpty()) {
                    DOB = it.vehicleInfo.dOB
//                    binding.edtCompany.setText(DOB)
                } else {
                    binding.edtCompany.setText("")
                }
                if (it.vehicleInfo.lisenceNumber.isNotEmpty()) {
                    licenseNo = it.vehicleInfo.lisenceNumber
//                    binding.edtMileage.setText(licenseNo)
                } else {
                    binding.edtMileage.setText("")
                }
                if (it.vehicleInfo.lisanceStartDate.isNotEmpty()) {
                    licensestart = it.vehicleInfo.lisanceStartDate
//                    binding.edtBlue.setText(licensestart)
                } else {
                    binding.edtBlue.setText("")
                }
                if (it.vehicleInfo.lisanceEnddate.isNotEmpty()) {
                    licenseEnd = it.vehicleInfo.lisanceEnddate
//                    binding.edtFuel.setText(licenseEnd)
                } else {
                    binding.edtFuel.setText("")
                }
                DriverHomeDepotId = it.vehicleInfo.DriverHomeDepotId

                if (!it.vehicleInfo.chMessage.isNullOrEmpty()) {

                    DriverHomeDepotId = it.vehicleInfo.DriverHomeDepotId
                    binding.textview.visibility = View.VISIBLE
                    binding.textview.setText(it.vehicleInfo.chMessage)
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.textviewbreakdown.visibility = View.GONE
                    binding.textview.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.red
                        )
                    )
                } else if (it.vehicleInfo.chMessage.isNullOrEmpty() && it.vehicleInfo.IsOnRoadVehicleAssignedToThisDriver) {
                    binding.textviewbreakdown.visibility = View.VISIBLE
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.textviewbreakdown.setOnClickListener {
                        binding.textview.visibility = View.VISIBLE
                        val intent = Intent(requireContext(), BreakDownActivity::class.java)
                        startActivity(intent)
                    }
                    binding.textview.visibility = View.VISIBLE
                    binding.textview.setText(it.vehicleInfo.OnRoadVehicleAssignedToThisDriverMessage)
                    binding.textview.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.red
                        )
                    )
                } else {
                    binding.textview.visibility = View.VISIBLE
                    binding.textviewbreakdown.visibility = View.GONE

                    vehicleLocation = it.vehicleInfo.vehicleLocation
                    binding.textview.setText("Allocated Vehicle: " + it.vehicleInfo.currentVehicleRegNo + "\n" + "Vehicle location: " + it.vehicleInfo.vehicleLocation)
                    binding.textview.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.text_color
                        )
                    )
                    Prefs.getInstance(App.instance).VmID = it.vehicleInfo.vmRegId.toString()
//                    relativelayout?.visibility = View.GONE
//                    linearLayout?.visibility = View.GONE
                }

                if (checkChMessage!!.isNotEmpty() || checkChMessagetwo!!.isNotEmpty()) {
                    binding.headerVehicleInformation.visibility = View.GONE
                    binding.bodyVehicleInfo.visibility = View.GONE
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
//                    binding.headerSelectVehicleOptions.setBackgroundColor(
//                        requireContext().getColor(
//                            R.color.blue_hex
//                        )
//                    )

                } else if (binding.edtCompanys.text.isNotEmpty() && binding.spinnerSelectDA.text.isNotEmpty() && checkChMessage!!.isEmpty() && checkChMessagetwo!!.isEmpty()) {
                    binding.headerVehicleInformation.visibility = View.VISIBLE
                    binding.bodyVehicleInfo.visibility = View.VISIBLE
//                    binding.headerSelectVehicleOptions.setBackgroundColor(
//                        requireContext().getColor(
//                            R.color.greenBtn
//                        )
//                    )

                }


            }

        })
    }

    fun GetVehicleCurrentInsuranceInfo() {

        mainViewModel.GetVehicleCurrentInsuranceInfo(
            Prefs.getInstance(App.instance).vmIdReturnveh.toInt()
        )
        mainViewModel.GetVehicleCurrentInsuranceInfo.observe(this, Observer {
            if (it != null) {
                vehLmId = it.vehicleInfo.vehLmId


                errortext = it.vehicleInfo.motAndRoad
                if (it.vehicleInfo.moveRequestAlreadyExistId > 0) {
                    binding.tvRelease.visibility = View.GONE
                    binding.txterrortwo?.visibility = View.GONE
                    binding.txterror?.visibility = View.VISIBLE
                    binding.txterror?.setText("There is a pending location transfer request for this vehicle; would you like to proceed with the transfer?")
                    binding.tvTransfer.visibility = View.VISIBLE
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.tvTransfer.setOnClickListener {

                        startActivity(Intent(requireContext(), ReturnToDaActivity::class.java))
                    }
                    binding.milegaetext.visibility = View.GONE
                    if (it.vehicleInfo.motAndRoad.equals("This vehicle is not Available to Allocate, For this vehicle, a release request has already been made, but the transport team has not yet taken any action. Contact the transport team if necessary..") || it.vehicleInfo.motAndRoad.equals(
                            "This vehicle is currently on the road. Please return it first"
                        ) || it.vehicleInfo.motAndRoad.equals("This vehicle is restricted to allocate as this vehicle is in Garage") || it.vehicleInfo.motAndRoad.equals(
                            "This vehicle is restricted to allocate as this vehicle has status Accident"
                        ) || it.vehicleInfo.motAndRoad.equals("This vehicle is restricted to allocate as this vehicle is not Available")

                    ) {
                        binding.tvRelease.visibility = View.GONE
                        binding.tvTransfer.visibility = View.GONE
                        binding.txterrortwo?.visibility = View.VISIBLE
                        binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
                        binding.txterror?.visibility = View.GONE
                        binding.llselectvehiclesub.visibility = View.GONE
                        binding.llstartinspection.visibility = View.GONE
                        binding.headeruploadVehicleInformation.visibility = View.GONE
                        binding.milegaetext.visibility = View.GONE

                    }

                } else if ( it.vehicleInfo.motAndRoad.equals(
                        "Road Tax is expired"
                    ) || it.vehicleInfo.motAndRoad.equals("MOT is expired") && it.vehicleInfo.vehLmId.equals(
                        DriverHomeDepotId
                    )
                ) {
                    binding.txterrortwo?.visibility = View.VISIBLE
                    binding.tvTransfer.visibility = View.GONE
                    binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
                    binding.txterror?.visibility = View.VISIBLE
                    binding.tvRelease.visibility = View.GONE
                    binding.llselectvehiclesub.visibility = View.VISIBLE
                    binding.llstartinspection.visibility = View.VISIBLE
                    binding.headeruploadVehicleInformation.visibility = View.VISIBLE
                    binding.milegaetext.visibility = View.VISIBLE

                } else if ( it.vehicleInfo.motAndRoad.equals(
                        "Road Tax is expired"
                    ) || it.vehicleInfo.motAndRoad.equals("MOT is expired") && !it.vehicleInfo.vehLmId.equals(
                        DriverHomeDepotId
                    )
                ) {
                    binding.tvTransfer.visibility = View.GONE
                    binding.txterrortwo?.visibility = View.VISIBLE
                    binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
                    Log.e("jackdenial1", "GetVehicleCurrentInsuranceInfo:  ")
                    binding.txterror?.visibility = View.VISIBLE

                    binding.txterror.setText(" Vehicle is not on the drivers location, please make a request to transfer the vehicle to driver's location first.")
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.tvRelease.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.milegaetext.visibility = View.GONE

                }
                else if (it.vehicleInfo.motAndRoad.contains("This Vehicle is not insured") && it.vehicleInfo.sAllowVehicleWithoutInsurance.equals(false)){
                    binding.tvTransfer.visibility = View.GONE
                    binding.txterrortwo?.visibility = View.VISIBLE
                    binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
                    Log.e("jackdenial1", "GetVehicleCurrentInsuranceInfo:  ")
                    binding.txterror?.visibility = View.VISIBLE

                    binding.txterror.setText(" Vehicle is not on the drivers location, please make a request to transfer the vehicle to driver's location first.")
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.tvRelease.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.milegaetext.visibility = View.GONE

                }
                else if (it.vehicleInfo.motAndRoad.contains("This Vehicle is not insured") && it.vehicleInfo.sAllowVehicleWithoutInsurance.equals(true) && it.vehicleInfo.vehLmId.equals(
                        DriverHomeDepotId)){
                    binding.txterrortwo?.visibility = View.VISIBLE
                    binding.tvTransfer.visibility = View.GONE
                    binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
                    binding.txterror?.visibility = View.VISIBLE
                    binding.tvRelease.visibility = View.GONE
                    binding.llselectvehiclesub.visibility = View.VISIBLE
                    binding.llstartinspection.visibility = View.VISIBLE
                    binding.headeruploadVehicleInformation.visibility = View.VISIBLE
                    binding.milegaetext.visibility = View.VISIBLE


                }

                else if (it.vehicleInfo.motAndRoad.equals("This vehicle is not Available to Allocate, For this vehicle, a release request has already been made, but the transport team has not yet taken any action. Contact the transport team if necessary.") || it.vehicleInfo.motAndRoad.equals(
                        "This vehicle is currently on the road. Please return it first"
                    ) || it.vehicleInfo.motAndRoad.equals("This vehicle is restricted to allocate as this vehicle is in Garage") || it.vehicleInfo.motAndRoad.equals(
                        "This vehicle is restricted to allocate as this vehicle has status Accident"
                    ) || it.vehicleInfo.motAndRoad.equals("This vehicle is restricted to allocate as this vehicle is not Available") && !it.vehicleInfo.vehLmId.equals(
                        DriverHomeDepotId
                    )

                ) {
                    binding.tvTransfer.visibility = View.GONE
                    binding.tvRelease.visibility = View.GONE
                    binding.txterrortwo?.visibility = View.VISIBLE
                    binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
                    binding.txterror?.visibility = View.GONE
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.milegaetext.visibility = View.GONE

                } else if (it.vehicleInfo.motAndRoad.equals("This vehicle is not Available to Allocate, For this vehicle, a release request has already been made, but the transport team has not yet taken any action. Contact the transport team if necessary.") || it.vehicleInfo.motAndRoad.equals(
                        "This vehicle is currently on the road. Please return it first"
                    ) || it.vehicleInfo.motAndRoad.equals("This vehicle is restricted to allocate as this vehicle is in Garage") || it.vehicleInfo.motAndRoad.equals(
                        "This vehicle is restricted to allocate as this vehicle has status Accident"
                    ) || it.vehicleInfo.motAndRoad.equals("This vehicle is restricted to allocate as this vehicle is not Available") && it.vehicleInfo.vehLmId.equals(
                        DriverHomeDepotId
                    )

                ) {
                    binding.tvTransfer.visibility = View.GONE
                    binding.tvRelease.visibility = View.GONE
                    binding.txterrortwo?.visibility = View.VISIBLE
                    binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
                    binding.txterror?.visibility = View.GONE
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.milegaetext.visibility = View.GONE

                } else if (it.vehicleInfo.notAvailableTicketAlreadyGenerated == false && it.vehicleInfo.vehicleAvailableToAllocate == false && it.vehicleInfo.showReleaseButton.equals(
                        true
                    )
                ) {
                    binding.tvTransfer.visibility = View.GONE
                    binding.tvRelease.visibility = View.VISIBLE
                    binding.txterrortwo?.visibility = View.VISIBLE
                    binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
                    binding.txterror?.visibility = View.GONE
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.milegaetext.visibility = View.GONE
                    binding.tvRelease?.setOnClickListener {
                        showAlertRelease()
                    }

                } else if (!it.vehicleInfo.vehLmId.equals(DriverHomeDepotId)) {
                    Log.e("jackdenial", "GetVehicleCurrentInsuranceInfo:  ")
                    binding.txterror.visibility = View.VISIBLE
                    binding.tvRelease.visibility = View.GONE
                    binding.txterror.setText("  Vehicle is not on the drivers location, please make a request to transfer the vehicle to driver's location first.")
                    binding.txterrortwo.visibility = View.GONE
                    binding.tvTransfer.visibility = View.GONE
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.milegaetext.visibility = View.GONE
                    if (it.vehicleInfo.motAndRoad.equals(
                            "This vehicle is not Available to Allocate."
                        )
                    ) {
                        binding.tvRelease.visibility = View.VISIBLE
                        binding.txterrortwo?.visibility = View.VISIBLE
                        binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
                        binding.txterror?.visibility = View.GONE
                        binding.tvTransfer.visibility = View.GONE
                        binding.llselectvehiclesub.visibility = View.GONE
                        binding.llstartinspection.visibility = View.GONE
                        binding.headeruploadVehicleInformation.visibility = View.GONE
                        binding.milegaetext.visibility = View.GONE
                        binding.tvRelease?.setOnClickListener {
                            showAlertRelease()
                        }
                    } else if (it.vehicleInfo.motAndRoad.isNotEmpty()) {
                        binding.txterror?.visibility = View.VISIBLE
                        binding.txterror.setText(it.vehicleInfo.motAndRoad)
                        binding.tvRelease.visibility = View.GONE
                        binding.txterrortwo?.visibility = View.VISIBLE

                        binding.tvTransfer.visibility = View.GONE
                        binding.llselectvehiclesub.visibility = View.GONE
                        binding.llstartinspection.visibility = View.GONE
                        binding.headeruploadVehicleInformation.visibility = View.GONE
                        binding.milegaetext.visibility = View.GONE

                    }


                } else if (it.vehicleInfo.vehLmId.equals(DriverHomeDepotId) && it.vehicleInfo.motAndRoad.isNotEmpty()) {
                    binding.txterror?.visibility = View.VISIBLE
                    binding.txterror.setText(it.vehicleInfo.motAndRoad)
                    binding.tvRelease.visibility = View.GONE
                    binding.txterrortwo?.visibility = View.VISIBLE

                    binding.tvTransfer.visibility = View.GONE
                    binding.llselectvehiclesub.visibility = View.GONE
                    binding.llstartinspection.visibility = View.GONE
                    binding.headeruploadVehicleInformation.visibility = View.GONE
                    binding.milegaetext.visibility = View.GONE
                } else {
                    binding.txterrortwo?.visibility = View.GONE
                    binding.tvTransfer.visibility = View.GONE
                    binding.txterror?.visibility = View.GONE
                    binding.tvRelease.visibility = View.GONE
                    binding.llselectvehiclesub.visibility = View.VISIBLE
                    binding.llstartinspection.visibility = View.VISIBLE
                    binding.headeruploadVehicleInformation.visibility = View.VISIBLE
                    binding.milegaetext.visibility = View.VISIBLE
                }


//                if (!it.vehicleInfo.vehLmId.equals(DriverHomeDepotId)) {
//
//                    if (it.vehicleInfo.motAndRoad.equals(
//                            "This vehicle is not Available to Allocate."
//                        )
//                    ) {
//                        binding.tvRelease.visibility = View.VISIBLE
//                        binding.txterrortwo?.visibility = View.VISIBLE
//                        binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
//                        binding.txterror?.visibility = View.GONE
//                        binding.llselectvehiclesub.visibility = View.GONE
//                        binding.llstartinspection.visibility = View.GONE
//                        binding.headeruploadVehicleInformation.visibility = View.GONE
//                        binding.milegaetext.visibility = View.GONE
//                        binding.tvRelease?.setOnClickListener {
//                            showAlertRelease()
//                        }
//                    } else if (it.vehicleInfo.motAndRoad.isNotEmpty() ) {
//                        binding.tvRelease.visibility = View.GONE
//                        binding.txterror?.visibility = View.VISIBLE
//                        binding.txterrortwo?.text = it.vehicleInfo.motAndRoad
//                        binding.llselectvehiclesub.visibility = View.GONE
//                        binding.llstartinspection.visibility = View.GONE
//                        binding.headeruploadVehicleInformation.visibility = View.GONE
//                        binding.milegaetext.visibility = View.GONE
//                        binding.txterror?.visibility = View.VISIBLE
//                        binding.txterror?.setText("Vehicle is not on the drivers location, please make a request to transfer the vehicle to driver's location first.")
//                    } else  if (it.vehicleInfo.motAndRoad.contains("This Vehicle is New  (Not yet assigned to anyone) \\nThis Vehicle is not insured.") && it.vehicleInfo.vehLmId.equals(
//                            DriverHomeDepotId)){
//                        binding.txterror.visibility = View.GONE
//                        binding.tvRelease.visibility = View.GONE
//                        binding.txterrortwo.visibility = View.GONE
//                        binding.txterrortwo.setText(it.vehicleInfo.motAndRoad)
//                        binding.txterror.setText("")
//                        binding.milegaetext.visibility = View.VISIBLE
//                        binding.llselectvehiclesub.visibility = View.VISIBLE
//                        binding.llstartinspection.visibility = View.VISIBLE
//                        binding.headeruploadVehicleInformation.visibility = View.VISIBLE
//                    }
//
//
//                } else if (it.vehicleInfo.motAndRoad.isNotEmpty() && it.vehicleInfo.vehLmId.equals(
//                        DriverHomeDepotId
//                    )
//                ) {
//                    binding.tvRelease.visibility = View.GONE
//                    binding.txterror.visibility = View.GONE
//                    binding.txterrortwo.visibility = View.VISIBLE
//                    binding.txterrortwo.text = (it.vehicleInfo.motAndRoad)
//                    binding.llselectvehiclesub.visibility = View.GONE
//                    binding.llstartinspection.visibility = View.GONE
//                    binding.headeruploadVehicleInformation.visibility = View.GONE
//                    binding.milegaetext.visibility = View.GONE
//
//
//                } else if (!it.vehicleInfo.vehLmId.equals(DriverHomeDepotId) && it.vehicleInfo.motAndRoad.isNotEmpty()) {
//                    binding.tvRelease?.visibility = View.GONE
//                    binding.txterror?.visibility = View.VISIBLE
//                    binding.txterrortwo?.visibility = View.VISIBLE
//                    binding.txterrortwo?.text = (it.vehicleInfo.motAndRoad)
//                    binding.llselectvehiclesub.visibility = View.GONE
//                    binding.llstartinspection.visibility = View.GONE
//                    binding.headeruploadVehicleInformation.visibility = View.GONE
//                    binding.milegaetext.visibility = View.GONE
//                } else {
//                    binding.txterror.visibility = View.GONE
//                    binding.tvRelease.visibility = View.GONE
//                    binding.txterrortwo.visibility = View.GONE
//                    binding.llstartinspection.visibility = View.VISIBLE
//                    binding.headeruploadVehicleInformation.visibility = View.VISIBLE
//                    binding.llselectvehiclesub.visibility = View.VISIBLE
//                    binding.milegaetext.visibility = View.VISIBLE
//                }
////            if (!vehLmId!!.equals(DriverHomeDepotId)) {
////
////                binding.llselectvehiclesub.visibility=View.GONE
////
////                } else if (vehLmId!!.equals(DriverHomeDepotId) && !it.vehicleInfo.motAndRoad.contains(
////                        "This Vehicle is not insured."
////                    ) || ! it.vehicleInfo.motAndRoad.contains("Road Tax is expired") || ! it.vehicleInfo.motAndRoad.contains("MOT is expired")
////                ) {
////
////                binding.llselectvehiclesub.visibility=View.GONE
////
////                }
////                else if (vehLmId!!.equals(DriverHomeDepotId) && it.vehicleInfo.motAndRoad.isEmpty()){
////                binding.llselectvehiclesub.visibility=View.VISIBLE
////                }
////                else{
////                binding.llselectvehiclesub.visibility=View.VISIBLE
////                }
//
//            }

            }
        })


    }

    fun showAlertRelease() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.release_dialog, null)
        val deleteDialog: androidx.appcompat.app.AlertDialog =
            androidx.appcompat.app.AlertDialog.Builder(requireContext()).create()/*        val imageView: ImageView = view.findViewById(R.id.ic_cross_orange)
                imageView.setOnClickListener {
                    deleteDialog.dismiss()
                }*/
        val btone: Button = view.findViewById(R.id.bt_no)
        val bttwo: Button = view.findViewById(R.id.bt_yes)

        btone.setOnClickListener {
            deleteDialog.dismiss()
        }

        bttwo.setOnClickListener {

            mainViewModel.CreateVehicleReleaseReq(
                Prefs.getInstance(App.instance).vmIdReturnveh.toDouble(),
                Prefs.getInstance(App.instance).clebUserId.toDouble()
            )
            mainViewModel.CreateVehicleReleaseReqlivedata.observe(requireActivity(), Observer {
                if (it != null) {
                    if (it.Status == "200") {
                        Snackbar.make(binding.scrollmain, "Release request has been created.", 2000)
                            .show()
                        GetVehicleCurrentInsuranceInfo()
//                        Toast.makeText(
//                            requireContext(), "Release request is created", Toast.LENGTH_SHORT
//                        ).show()
                    }
                }


            })
            deleteDialog.dismiss()
        }
        deleteDialog.setView(view)
        deleteDialog.setCanceledOnTouchOutside(false)
        deleteDialog.setCancelable(false)
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        try {
            deleteDialog.show()
        } catch (_: Exception) {

        }

    }

    private fun startInspection() {
        binding.pbs.visibility = View.VISIBLE
        startboolean = true
        clientUniqueID()
//        if (isAllImageUploaded) {
//            mbinding.tvNext.visibility = View.VISIBLE
//        }
        binding.headeruploadVehicleInformationVanhire.visibility = View.VISIBLE
        binding.startVanHire.visibility = View.VISIBLE
//        binding.constmain.visibility = View.VISIBLE
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
                        ), customerDetails = CustomerDetails(
                            name = "", //if sent, user can't edit
                            email = "", //if sent, user can't edit
                            dialCode = "", //if sent, user can't edit
                            phoneNumber = "", //if sent, user can't edit
                        )
                    ),
                    userFlowParams = UserFlowParams(
                        isOffline = false, // true, Offline quote will be created | false, online quote will be created | null, online

                        skipInputPage = true, // true, Inspection will be started with camera page | false, Inspection will be started

                    ),

                    result = { isStarted, msg, code ->
                        if (isStarted) {
                            binding.pbs.visibility = View.GONE
                        }
                        Log.e("messsagesss", "startInspection: " + msg + code)

                    })
            } catch (_: Exception) {

                binding.pbs.visibility = View.GONE
            }
        }

    }

    fun clientUniqueID(): String {
        val x = "123456"
        val y = "123456"
        // example string
        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))

        val regexPattern: Regex
        regexPattern = Regex("${x.take(3)}${y.take(3)}${formattedDate}")
        inspectionID = regexPattern.toString()
        return regexPattern.toString()
        Log.e("resistrationvrnpatterhn", "clientUniqueID: " + inspectionID)
    }


}
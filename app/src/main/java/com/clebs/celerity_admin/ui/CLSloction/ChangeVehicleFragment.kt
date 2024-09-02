package com.clebs.celerity_admin.ui.CLSloction

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.MainActivityTwo
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.CompanyListAdapter
import com.clebs.celerity_admin.adapters.DriverListAdapter
import com.clebs.celerity_admin.adapters.FuelLevelAdapter
import com.clebs.celerity_admin.adapters.ReturnVehicleAdapter
import com.clebs.celerity_admin.adapters.SelectVehicleLocationAdapter
import com.clebs.celerity_admin.adapters.VehicleOilLevelAdapter
import com.clebs.celerity_admin.database.User
import com.clebs.celerity_admin.database.VehicleInformation
import com.clebs.celerity_admin.databinding.FragmentGalleryBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.CompanyListResponseItem
import com.clebs.celerity_admin.models.DriverListResponseModelItem
import com.clebs.celerity_admin.models.GetVehicleRequestTypeItem
import com.clebs.celerity_admin.models.VehicleReturnModelListItem
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.ui.App
import com.clebs.celerity_admin.ui.BreakDownActivity
import com.clebs.celerity_admin.utils.OnItemClickRecyclerView
import com.clebs.celerity_admin.utils.OnReturnVehicle
import com.clebs.celerity_admin.utils.Onclick
import com.clebs.celerity_admin.utils.OnclickDriver
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.setupHalfHeight
import com.clebs.celerity_admin.utils.setupHalfHeightForlisting
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class ChangeVehicleFragment : Fragment(), Onclick, OnclickDriver, OnReturnVehicle,
    OnItemClickRecyclerView {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var inspectionID: String
    var time = String()

    var DOB = String()
    var licensestart = String()
    var licenseEnd = String()
    var licenseNo = String()
    var vehiclelocation = String()

    private lateinit var regexPattern: Regex
    var companynames: String? = null
    private var datextinputlayout: TextInputLayout? = null
    private var returntextinputlayout: TextInputLayout? = null
    private var textinput: EditText? = null
    private var textinputtwo: EditText? = null
    private var textinputthree: EditText? = null
    private var textinputfour: EditText? = null
    private var edt_layout: TextInputLayout? = null
    private var requesttype: TextInputLayout? = null
    private var requesttypetext: EditText? = null
    private var DA_id = String()
    private var checkChMessage: String? = null
    private var checkChMessagetwo: String? = null
    private var vehicleLocation: String? = null
    private var DriverHomeDepotId: Int? = null
    private var vehLmId: Int? = null
    var spinnerposition: Int? = -1
    private var returbDA_wmID = String()
    lateinit var deleteDialog: AlertDialog
    lateinit var deleteDialogtwo: AlertDialog
    lateinit var request_type: AlertDialog
    lateinit var deleteDialogthree: AlertDialog
    private var RADIOBUTTONWORTHY: RadioButton? = null
    private var RADIOBUTTONNOTWORTHY: RadioButton? = null
    lateinit var DriverListAdapter: DriverListAdapter
    lateinit var ReturnVehicleAdapter: ReturnVehicleAdapter
    var spinner: Spinner? = null

    private var RETURNTODEOPSUP: TextView? = null
    private var RBTRAINING: RadioButton? = null
    private var linearLayout: LinearLayout? = null


    private var saveClickCounter = 0
    private var saveClickCountertwo = 0
    private var saveClickCounterthree = 0
    private var saveClickCounterfour = 0
    private var saveClickCounterfive = 0
    private var saveClickCountersix = 0
    private var saveClickCounterseven = 0
    private var textViewInspection: TextView? = null
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
    private var listnew = ArrayList<GetVehicleRequestTypeItem>()
    var current_screen = String()

    lateinit var FuellevelAdapter: FuelLevelAdapter
    lateinit var OillevelAdapter: VehicleOilLevelAdapter
    lateinit var textView: TextView
    lateinit var textviewbreakdwon: TextView
    lateinit var returndepo: TextView
    private var radioButtonDA: RadioButton? = null
    private var radioButtonreturn: RadioButton? = null
    private var relativelayout: RelativeLayout? = null

    private var radioButtonworthy: RadioButton? = null
    private var radioButtonnotworthy: RadioButton? = null
    private var daname: EditText? = null
    private var edt: EditText? = null
    private var edt_select_vehicle: EditText? = null
    private var txterror: TextView? = null
    private var txterrortwo: TextView? = null
    private var errortext = String()
    private var tvrelease: TextView? = null
    private var edttwo: EditText? = null
    private var edtthree: EditText? = null
    private var edtcurrentmileage: EditText? = null
    private var edtmilegae: EditText? = null
    private var edtaddblue: EditText? = null


    private var rv_locatio: RecyclerView? = null
    private var rv_fuel: RecyclerView? = null
    private var rv_oil: RecyclerView? = null

    private var returnname: EditText? = null
    private val items = ArrayList<DriverListResponseModelItem>()
    private val itemstwo = ArrayList<VehicleReturnModelListItem>()
    lateinit var mainViewModel: MainViewModel
    lateinit var attachmentAdapter: CompanyListAdapter
    private var isBreakDown: Boolean? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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


        FuellevelAdapter = FuelLevelAdapter(ArrayList(), this)
        OillevelAdapter = VehicleOilLevelAdapter(ArrayList(), this)
        val bundle = Bundle()
        current_screen = arguments?.getString("change_DA").toString()
        Log.e(
            "lkjfdkjfdfuidfiuodfoicurerentscrteemn", "onCreateViewCurrentScreen: " + current_screen
        )
        if (current_screen.isNotEmpty()) {
            binding.textView3.performClick()
        }

        isfirst = Prefs.getInstance(requireContext()).Isfirst

        binding.pb2.visibility = View.VISIBLE
//        binding.constmain.alpha = 0.5f
        EditableFalse(binding.textView5)
//        EditableFalse(binding.textView3)
        EditableFalse(binding.textView5)
        Observers()
        Thread {
            GlobalScope.launch {
                if (!App.offlineSyncDB!!.isUserTableEmpty()) {
                    binding.checkone.visibility = View.VISIBLE
                    binding.textView4.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.img_round
                        )
                    )
                    binding.consttwo.alpha = 1f
                    EditableTrue(binding.textView4)

                } else {
                    binding.checkone.visibility = View.GONE
                    EditableFalse(binding.textView4)
                    binding.textView4.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.img_round_1
                        )
                    )
                    binding.consttwo.alpha = 0.5f
                }
                if (!App.offlineSyncDB!!.isUserTableEmptyInformation()) {

                    binding.checktwo.visibility = View.VISIBLE


                    binding.textView6.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.img_round
                        )
                    )
                    binding.constthree.alpha = 1f

                    EditableTrue(binding.textView6)
                } else {
                    binding.checktwo.visibility = View.GONE
                    binding.constthree.alpha = 0.5f
                    binding.textView6.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.img_round_1
                        )
                    )
                    EditableFalse(binding.textView6)
                }


            }


        }.start()





        cqSDKInitializer.checkOfflineQuoteSyncCompleteStatus() { isSyncCompletedForAllQuotes ->
            if (isSyncCompletedForAllQuotes) {

            }
        }

        clientUniqueID()
        val textView3: CircleImageView = binding.textView3
        binding.textView4.setOnClickListener {
            if (saveClickCountertwo++ == 0) {

                //Your Dialog Showing Code
                selectVehicleInformation()

                Handler().postDelayed({
                    saveClickCountertwo = 0
                }, 1000)
            }
            true


        }
        binding.textView6.setOnClickListener {
            if (saveClickCounterthree++ == 0) {

                //Your Dialog Showing Code
                uploadVehiclePicture()

                Handler().postDelayed({
                    saveClickCounterthree = 0
                }, 1000)
            }
            true


        }
        binding.textView5.setOnClickListener {
            if (saveClickCounterfour++ == 0) {

                //Your Dialog Showing Code
                bottom_sheetVanHire()

                Handler().postDelayed({
                    saveClickCounterfour = 0
                }, 1000)
            }
            true

        }
        textView3.setOnClickListener {
            lifecycleScope.launch {
                if (saveClickCounter++ == 0) {

                    //Your Dialog Showing Code
                    selectVehicleOptions()

                    Handler().postDelayed({
                        saveClickCounter = 0
                    }, 1000)
                }
                true


            }

        }


        return root
    }


    fun Observers() {

        mainViewModel.GetCompanyListing().observe(viewLifecycleOwner, Observer {
            Log.e("dkfdjkfjkdfjdresponse", "onCreateView: " + it)
            if (it != null) {
                binding.pb2.visibility = View.GONE
                binding.constmain.alpha = 1f
//                attachmentAdapter.data.addAll(it)
                list.clear()
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
        mainViewModel.GetVehiclefuelListing().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.pb2.visibility = View.GONE
                FuellevelAdapter.data.addAll(it)

                FuellevelAdapter.notifyDataSetChanged()
                binding.constmain.alpha = 1f

            } else {
                binding.pb2.visibility = View.GONE
                binding.constmain.alpha = 1f
            }

        })
        mainViewModel.GetVehicleOilListing().observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.pb2.visibility = View.GONE
                OillevelAdapter.data.addAll(it)
                OillevelAdapter.notifyDataSetChanged()
                binding.constmain.alpha = 1f

            } else {
                binding.pb2.visibility = View.GONE
                binding.constmain.alpha = 1f
            }

        })
        getvehcileRequestTypeList()

    }

    fun getDDAMandate() {

        mainViewModel.GetDDAmandate(DA_id).observe(viewLifecycleOwner, Observer {
            if (it != null) {
                checkChMessage = it.vehicleInfo.chMessage
                checkChMessagetwo = it.vehicleInfo.OnRoadVehicleAssignedToThisDriverMessage

                if (it.vehicleInfo.dOB.isNotEmpty()) {
                    DOB = it.vehicleInfo.dOB
                }
                if (it.vehicleInfo.lisenceNumber.isNotEmpty()){
                    licenseNo=it.vehicleInfo.lisenceNumber
                }
                if (it.vehicleInfo.lisanceStartDate.isNotEmpty()){
                    licensestart=it.vehicleInfo.lisanceStartDate
                }
                if (it.vehicleInfo.lisanceEnddate.isNotEmpty()){
                    licenseEnd=it.vehicleInfo.lisanceEnddate
                }
                if (!it.vehicleInfo.chMessage.isNullOrEmpty()) {

                    DriverHomeDepotId = it.vehicleInfo.DriverHomeDepotId
                    textView.setText(it.vehicleInfo.chMessage)
                    textviewbreakdwon.visibility = View.GONE
                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                } else if (it.vehicleInfo.chMessage.isNullOrEmpty() && it.vehicleInfo.IsOnRoadVehicleAssignedToThisDriver) {
                    textviewbreakdwon.visibility = View.VISIBLE
                    textviewbreakdwon.setOnClickListener {

                        val intent = Intent(requireContext(), BreakDownActivity::class.java)
                        startActivity(intent)
                    }
                    textView.setText(it.vehicleInfo.OnRoadVehicleAssignedToThisDriverMessage)
                    textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                } else {
                    textviewbreakdwon.visibility = View.GONE
                    vehicleLocation = it.vehicleInfo.vehicleLocation
                    textView.setText("Allocated Vehicle: " + it.vehicleInfo.currentVehicleRegNo + "\n" + "Vehicle location: " + it.vehicleInfo.vehicleLocation)
                    textView.setTextColor(
                        ContextCompat.getColor(
                            requireContext(), R.color.text_color
                        )
                    )
                    Prefs.getInstance(App.instance).VmID =
                        it.vehicleInfo.vmRegId.toString()
                    relativelayout?.visibility = View.GONE
                    linearLayout?.visibility = View.GONE
                }

            }

        })
    }

    fun getDDAreturnALlocation() {
        mainViewModel.GetDDAmandateReturn(returbDA_wmID).observe(viewLifecycleOwner, Observer {
            binding.pb2.visibility = View.GONE
            if (it != null) {
                textView.setText("Allocated DA: " + it.vehicleInfo.daName)
                binding.pb2.visibility = View.GONE



                getRepoInfoModel()

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

                if (it.vehicleInfo.motAndRoadExist) {
                    errortext = it.vehicleInfo.motAndRoad
                }
                if (!it.vehicleInfo.vehLmId.equals(DriverHomeDepotId)) {
                    if (it.vehicleInfo.motAndRoad.equals(
                            "This vehicle is not Available to Allocate."
                        )
                    ) {
                        tvrelease?.visibility = View.VISIBLE
                        txterrortwo?.visibility = View.VISIBLE
                        txterrortwo?.text = it.vehicleInfo.motAndRoad
                        txterror?.visibility = View.GONE
                        tvrelease?.setOnClickListener {
                            showAlertRelease()
                        }
                    } else {
                        tvrelease?.visibility = View.GONE
                        txterror?.visibility = View.VISIBLE
                        txterrortwo?.text = it.vehicleInfo.motAndRoad
                        txterror?.visibility = View.VISIBLE
                        txterror?.setText("Vehicle is not on the drivers location, please make a request to transfer the vehicle to driver's location first.")
                    }


                } else if (it.vehicleInfo.motAndRoad.isNotEmpty() && it.vehicleInfo.vehLmId.equals(
                        DriverHomeDepotId
                    )
                ) {
                    tvrelease?.visibility = View.GONE
                    txterror?.visibility = View.GONE
                    txterrortwo?.visibility = View.VISIBLE
                    txterrortwo?.text = (it.vehicleInfo.motAndRoad)


                } else if (!it.vehicleInfo.vehLmId.equals(DriverHomeDepotId) && it.vehicleInfo.motAndRoad.isNotEmpty()) {
                    tvrelease?.visibility = View.GONE
                    txterror?.visibility = View.VISIBLE
                    txterrortwo?.visibility = View.VISIBLE
                    txterrortwo?.text = (it.vehicleInfo.motAndRoad)
                } else {
                    txterror?.visibility = View.GONE
                    tvrelease?.visibility = View.GONE
                    txterrortwo?.visibility = View.GONE
                }


            }


        })


    }

    fun getRepoInfoModel() {
        mainViewModel.GetRepoInfoModel(Prefs.getInstance(App.instance).vmIdReturnveh.toString())
            .observe(viewLifecycleOwner, Observer {
                relativelayout?.visibility = View.VISIBLE
                linearLayout?.visibility = View.VISIBLE
                binding.pb2.visibility = View.GONE
                if (it != null) {
                    if (it.vehicleInfo.showDepo) {
                        RETURNTODEOPSUP?.setText("Return to depo?")
//                        linearLayout?.visibility = View.VISIBLE
//                        linearLayout?.setBackgroundDrawable(
//                            ContextCompat.getDrawable(
//                                requireContext(),
//                                R.drawable.shape_radio_two
//                            )
//                        )


//                    llmthree.visibility = View.VISIBLE
//                    returndepo.setText("Return to depo?")


                    } else if (it.vehicleInfo.showSupplier) {
                        RETURNTODEOPSUP?.setText("Return to Supplier?")
//                        linearLayout?.visibility = View.GONE
//                        linearLayout?.setBackgroundDrawable(
//                            ContextCompat.getDrawable(
//                                requireContext(),
//                                R.drawable.shaperadio
//                            )
//                        )

//                    llmthree.visibility = View.GONE
//                    returndepo.setText("Return To supplier")
                    } else {

                    }

                } else {

                }


            })

    }

    fun getvehcileRequestTypeList() {
        mainViewModel.GetVehicleRequestTypeList().observe(viewLifecycleOwner, Observer {

            if (it != null) {

                binding.pb2.visibility = View.GONE
                binding.constmain.alpha = 1f
                attachmentAdapter.data.addAll(it)
                listnew.clear()
                listnew.addAll(it)

                attachmentAdapter.notifyDataSetChanged()
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

    private suspend fun selectVehicleOptions() {

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val showSheet = LayoutInflater.from(requireContext()).inflate(
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
        textviewbreakdwon = showSheet.findViewById(R.id.tvBreakdown)
        returndepo = showSheet.findViewById(R.id.returndepo)
        returnvehiclename = showSheet.findViewById(R.id.edt_return_da)
        radioButtonDA = showSheet.findViewById(R.id.rbReTraining)
        radioButtonreturn = showSheet.findViewById(R.id.rbTraining)
        radioButtonworthy = showSheet.findViewById(R.id.roadworthy)
        radioButtonnotworthy = showSheet.findViewById(R.id.notworthys)


        datextinputlayout = showSheet.findViewById(R.id.edt_layout_two)
        edt_layout = showSheet.findViewById(R.id.edt_layout)
        returntextinputlayout = showSheet.findViewById(R.id.edt_layout_return)
        requesttype = showSheet.findViewById(R.id.edt_layout_three)
        rv_select_type = showSheet.findViewById(R.id.rv_select_type)
        requesttypetext = showSheet.findViewById(R.id.edt)
        EditableFalse(requesttypetext!!)
        Log.e(
            "cxfjkdkjfdkjfjdfdjfhjdfjhdfjdhfdfhdgdejh",
            "selectVehicleOptionsDAlist: " + current_screen
        )

        if (current_screen.isNotEmpty()) {
            if (current_screen == "changeda") {
                datextinputlayout?.visibility = View.VISIBLE
                daname?.visibility = View.VISIBLE

            }
        }
        requesttypetext!!.setOnClickListener {
            if (saveClickCounterfive++ == 0) {

                //Your Dialog Showing Code
                showcompanylistdialog()

                Handler().postDelayed({
                    saveClickCounterfive = 0
                }, 1000)
            }
            true


        }

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


        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, list.map {

                it.name
            })
        spinner?.adapter = adapter
        updates()
        companyname?.setOnClickListener {
//            showcompanylistdialog()


            spinner?.performClick()


        }

        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val text = parent!!.getItemAtPosition(position).toString()
//                companyname?.setText(parent?.selectedItem.toString())
                spinnerposition = position
                companynames = parent?.selectedItem.toString()
                spinner?.visibility = View.VISIBLE
//                llms.visibility = View.VISIBLE
                Log.e("slevhdsd", "onItemSelected: " + companynames)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                spinner?.visibility = View.GONE
            }
        }



        daname?.setOnClickListener {
            if (saveClickCountersix++ == 0) {

                //Your Dialog Showing Code
                ShowDriverListDialog()

                Handler().postDelayed({
                    saveClickCountersix = 0
                }, 1000)
            }
            true


        }
        returnvehiclename?.setOnClickListener {
            if (saveClickCounterseven++ == 0) {

                //Your Dialog Showing Code
                ShowReturnVehicleList()

                Handler().postDelayed({
                    saveClickCounterseven = 0
                }, 1000)
            }
            true


        }
        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
        val textView: TextView = showSheet.findViewById(R.id.bt_change)
        textView.setOnClickListener {
            if (!checkChMessage.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(), "Please select other DA to proceed", Toast.LENGTH_SHORT
                ).show()

            } else if (!checkChMessagetwo.isNullOrEmpty()) {
                Toast.makeText(
                    requireContext(), "Please select other DA to proceed", Toast.LENGTH_SHORT
                ).show()
            }
//            bottomSheetDialog.dismiss()
            else if (current_screen.equals("changeda") && daname?.text!!.isEmpty()) {
                Toast.makeText(requireContext(), "Please select DA name", Toast.LENGTH_SHORT).show()
            } else if (current_screen.equals("returnveh") && returnvehiclename?.text!!.isEmpty()) {
                Toast.makeText(
                    requireContext(), "Please select Vehicle to return", Toast.LENGTH_SHORT
                ).show()
            } else {


                GlobalScope.launch {
                    Log.e("sdffncmnxmv", "selectVehicleOptions: ")
                    if (App.offlineSyncDB!!.isUserTableEmpty()) {
                        // The users table is empty, you can perform some initial setup here
                        if (current_screen.equals("changeda") && daname?.text!!.isNotEmpty()) {
                            lifecycleScope.launch {
                                App.offlineSyncDB!!.insert(
                                    User(
                                        0,
                                        companynames!!.toString(),
                                        changeDAvechile = true,
                                        returnDAvehicle = false,
                                        selectDA = daname!!.text.toString(),
                                        selectVehicleReturn = "",

                                        spinnerposition!!
                                    )
                                )
                            }

                        } else if (current_screen.equals("returnveh") && returnvehiclename!!.text.isNotEmpty()) {
                            lifecycleScope.launch {
                                App.offlineSyncDB!!.insert(
                                    User(
                                        1,
                                        companynames!!.toString(),
                                        false,
                                        true,
                                        "",
                                        returnvehiclename!!.text.toString(),

                                        spinnerposition!!
                                    )
                                )
                            }

                        }

                    } else {

                        Log.e("djfdfhdjhfdjh", "selectVehicleOptions: ")
                        App.offlineSyncDB!!.deleteUserTable()
                        if (current_screen.equals("changeda") && daname?.text!!.isNotEmpty()) {
                            lifecycleScope.launch {
                                App.offlineSyncDB!!.insert(
                                    User(
                                        0,
                                        companynames!!.toString(),
                                        changeDAvechile = true,
                                        returnDAvehicle = false,
                                        selectDA = daname!!.text.toString(),
                                        selectVehicleReturn = "",
                                        spinnerposition!!
                                    )
                                )
                            }

                        } else if (current_screen.equals("returnveh") && returnvehiclename!!.text.isNotEmpty()) {
                            lifecycleScope.launch {
                                App.offlineSyncDB!!.insert(
                                    User(
                                        1,
                                        companynames!!.toString(),
                                        false,
                                        true,
                                        "",
                                        returnvehiclename!!.text.toString(),
                                        spinnerposition!!
                                    )
                                )
                            }

                        } else if (current_screen.equals("returnveh") && returnvehiclename!!.text.isNotEmpty()) {
                            lifecycleScope.launch {
                                App.offlineSyncDB!!.insert(
                                    User(
                                        2,
                                        companynames!!.toString(),
                                        false,
                                        true,
                                        "",
                                        returnvehiclename!!.text.toString(),
                                        spinnerposition!!
                                    )
                                )
                            }
                        }


                    }

                }

//                updateUI()
                bottomSheetDialog.dismiss()
                selectVehicleInformation()

                binding.checkone.visibility = View.VISIBLE
                binding.textView4.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.img_round
                    )
                )
                binding.consttwo.alpha = 1f
                EditableTrue(binding.textView4)
            }


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
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val showSheet = LayoutInflater.from(requireContext()).inflate(
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
        val tv_mileage: TextView = showSheet.findViewById(R.id.tv_milegae)


        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
        val bt_change: TextView = showSheet.findViewById(R.id.bt_change)
        edt = showSheet.findViewById(R.id.edt_company)
        edt_select_vehicle = showSheet.findViewById(R.id.edt_select_vehicle)
        txterror = showSheet.findViewById(R.id.txterror)
        txterrortwo = showSheet.findViewById(R.id.txterrortwo)
        tvrelease = showSheet.findViewById(R.id.tvRelease)
        edttwo = showSheet.findViewById(R.id.edt_fuel)
        edtthree = showSheet.findViewById(R.id.edt_oil)
        edtcurrentmileage = showSheet.findViewById(R.id.edt_mileage)
        edtaddblue = showSheet.findViewById(R.id.edt_blue)

        rv_locatio = showSheet.findViewById(R.id.rv_vehicle_location)
        rv_fuel = showSheet.findViewById(R.id.rv_vehicle_fuellevel)
        rv_oil = showSheet.findViewById(R.id.rv_vehicle_oillevel)


        rv_fuel?.adapter = FuellevelAdapter
        rv_oil?.adapter = OillevelAdapter
        rv_locatio?.adapter = selectVehcilelocationadapter


        edt?.isEnabled = true
        edt?.isClickable = true
        edt?.isFocusable = false
        edt?.isFocusableInTouchMode = false
        edttwo?.isEnabled = true
        edttwo?.isClickable = true
        edttwo?.isFocusable = false
        edttwo?.isFocusableInTouchMode = false
        edtthree?.isEnabled = true
        edtthree?.isClickable = true
        edtthree?.isFocusable = false
        edtthree?.isFocusableInTouchMode = false

        edt_select_vehicle?.isEnabled = true
        edt_select_vehicle?.isClickable = true
        edt_select_vehicle?.isFocusable = false
        edt_select_vehicle?.isFocusableInTouchMode = false
        updateUI()
        edt_select_vehicle?.setOnClickListener {

            ShowReturnVehicleList()
        }
        edttwo?.setOnClickListener {

            rv_fuel?.visibility = View.VISIBLE
            rv_locatio?.visibility = View.GONE
            rv_oil?.visibility = View.GONE

        }
        edtthree?.setOnClickListener {
            rv_oil?.visibility = View.VISIBLE
            rv_fuel?.visibility = View.GONE
            rv_locatio?.visibility = View.GONE
        }

        edt?.setOnClickListener {
            if (current_screen.equals("changeda")) {
                rv_locatio?.visibility = View.GONE
                edt?.setText(vehicleLocation)

            } else {
                rv_locatio?.visibility = View.VISIBLE
            }

            rv_fuel?.visibility = View.GONE
            rv_oil?.visibility = View.GONE
        }

        mainViewModel.GetlastMileageInfo(Prefs.getInstance(App.instance).vmIdReturnveh)
            .observe(viewLifecycleOwner, Observer {
                Log.e("kdkjfjdkffdfkdjfkd", "selectVehicleInformation: " + it)
                if (it != null) {
                    Log.e(
                        "hdjhfhdkfsodpod",
                        "selectVehicleInformation: " + it.vehicleInfo.vehLastMillage
                    )
                    tv_mileage.setText(it.vehicleInfo.vehLastMillage)
                }
            })
        imageView.setOnClickListener {


            bottomSheetDialog.dismiss()
        }
        bt_change.setOnClickListener {
            if (edt?.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(), "Please enter vehicle location", Toast.LENGTH_SHORT
                ).show()


            } else if (!vehLmId!!.equals(DriverHomeDepotId)) {
                Toast.makeText(
                    requireContext(), "Please select other vehicle to proceed", Toast.LENGTH_SHORT
                ).show()

            } else if (vehLmId!!.equals(DriverHomeDepotId) && txterrortwo?.text!!.isNotEmpty() && !errortext.equals(
                    "This Vehicle is not insured."
                ) && !errortext.contains("Road Tax is expired") && !errortext.contains("MOT is expired")
            ) {
                Toast.makeText(
                    requireContext(), "Please select other vehicle to proceed", Toast.LENGTH_SHORT
                ).show()


            } else if (edtmilegae?.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter vehicle Mileage", Toast.LENGTH_SHORT)
                    .show()

            } else if (edtaddblue?.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter vehicle addblue", Toast.LENGTH_SHORT)
                    .show()
            } else if (edttwo?.text.toString().isEmpty()) {

                Toast.makeText(
                    requireContext(), "Please select vehicle fuel level", Toast.LENGTH_SHORT
                ).show()
            } else if (edtthree?.text.toString().isEmpty()) {
                Toast.makeText(
                    requireContext(), "Please select vehicle oil level", Toast.LENGTH_SHORT
                ).show()
            } else {
                GlobalScope.launch {
                    bottomSheetDialog.dismiss()
                    binding.checktwo.visibility = View.VISIBLE
                    binding.textView6.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.img_round
                        )
                    )
                    binding.constthree.alpha = 1f
                    EditableTrue(binding.textView6)

                    uploadVehiclePicture()
                    Log.e("sdffncmnxmv", "selectVehicleOptions: ")
                    if (App.offlineSyncDB!!.isUserTableEmptyInformation()) {
                        // The users table is empty, you can perform some initial setup here

                        lifecycleScope.launch {
                            App.offlineSyncDB!!.insertinfo(
                                VehicleInformation(
                                    0,
                                    edt?.text.toString(),
                                    edtcurrentmileage?.text.toString(),
                                    edtaddblue?.text.toString(),
                                    edttwo?.text.toString(),
                                    edtthree?.text.toString()
                                )
                            )

                        }


                    } else {

                        Log.e("djfdfhdjhfdjh", "selectVehicleOptions: ")
                        App.offlineSyncDB!!.deleteTableInfos()

                        lifecycleScope.launch {
                            App.offlineSyncDB!!.insertinfo(
                                VehicleInformation(
                                    1,
                                    edt?.text.toString(),
                                    edtcurrentmileage?.text.toString(),
                                    edtaddblue?.text.toString(),
                                    edttwo?.text.toString(),
                                    edtthree?.text.toString()
                                )
                            )

                        }

                    }

                }


            }


        }
        bottomSheetDialog.setContentView(showSheet)
        setupHalfHeightForlisting(bottomSheetDialog, requireContext())
//        setupFullHeight(bottomSheetDialog, requireContext())
//        setupFullHeight(bottomSheetDialog, requireContext())
        bottomSheetDialog.show()
    }

    private fun uploadVehiclePicture() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val showSheet = LayoutInflater.from(requireContext()).inflate(
            R.layout.bottom_upload,
            requireActivity().findViewById<View>(R.id.bottomSheetRealCL) as ConstraintLayout?
        )
        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
        imageView.setOnClickListener {
            bottomSheetDialog.dismiss()
            binding.pb2.visibility = View.GONE
        }
        RETURNTODEOPSUP = showSheet.findViewById(R.id.edt_traingtwo)
        val bt_change: TextView = showSheet.findViewById(R.id.bt_change)

        textViewInspection = showSheet.findViewById(R.id.bt_startInspection)
        RBTRAINING = showSheet.findViewById(R.id.rbTraining)
        val progress: ProgressBar = showSheet.findViewById(R.id.pb1)
        relativelayout = showSheet.findViewById(R.id.rlmain)
        RADIOBUTTONWORTHY = showSheet.findViewById(R.id.roadworthy)
        RADIOBUTTONNOTWORTHY = showSheet.findViewById(R.id.notworthys)
        linearLayout = showSheet.findViewById(R.id.llmthree)

        lifecycleScope.launch {
            if (!App.offlineSyncDB!!.isUploadPicturesIsEmpty()) {

                if (!App.offlineSyncDB!!.getAllUsers().get(0).changeDAvechile) {
                    getRepoInfoModel()
                }
                setFullAlpha(relativelayout!!)
                textViewInspection?.setText("Inspection Done")
                textViewInspection!!.setTextColor(
                    ContextCompat.getColor(
                        requireContext(), R.color.red_light
                    )
                )
                textViewInspection?.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        requireContext(), R.drawable.shaperadio
                    )
                )
                textViewInspection?.setClickable(false);
                textViewInspection?.setFocusableInTouchMode(false);
                textViewInspection?.setEnabled(false);

                textViewInspection?.setFocusable(false);
                binding.pb2.visibility = View.VISIBLE
//                getRepoInfoModel()

            } else {
                textViewInspection?.setText("Start inspection")
            }
        }


        progress.visibility = View.GONE
        textViewInspection!!.setOnClickListener {
            progress.visibility = View.VISIBLE
            binding.pb2.visibility = View.VISIBLE
            startInspection()
            bottomSheetDialog.dismiss()

        }
        bt_change.setOnClickListener {
            if (textViewInspection?.text.toString().equals("Start inspection")) {
                Toast.makeText(requireContext(), "Please upload pictures", Toast.LENGTH_SHORT)
                    .show()
            } else if (!RBTRAINING!!.isChecked) {
                Toast.makeText(
                    requireContext(),
                    "Please Select return to vehicle / Supplier",
                    Toast.LENGTH_SHORT
                ).show()
            } else {


            }

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
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val showSheet = LayoutInflater.from(requireContext()).inflate(
            R.layout.bottom_sheet_van_hire,
            requireActivity().findViewById<View>(R.id.bottomSheetRealCL) as ConstraintLayout?
        )
        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
        imageView.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        textinput = showSheet.findViewById(R.id.edt_company)
        textinputtwo = showSheet.findViewById(R.id.edt_mileage)
        textinputthree = showSheet.findViewById(R.id.edt_blue)
        textinputfour = showSheet.findViewById(R.id.edt_fuel)

        if (DOB.isNotEmpty()){
            textinput?.setText(DOB)
        }
        if (licenseNo.isNotEmpty()){
            textinputtwo?.setText(licenseNo)
        }
        if (licensestart.isNotEmpty()){
            textinputthree?.setText(licensestart)
        }
        if (licenseEnd.isNotEmpty()){
            textinputfour?.setText(licenseEnd)
        }
        EditableFalse(textinput!!)
        EditableFalse(textinputtwo!!)
        EditableFalse(textinputthree!!)
        EditableFalse(textinputfour!!)

        textinput?.setOnClickListener {
            calview(0)


        }

        textinputtwo?.setOnClickListener {
            calview(1)
        }
        textinputthree?.setOnClickListener {
            calview(2)
        }
        textinputfour?.setOnClickListener {
            calview(3)
        }
//        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
//        imageView.setOnClickListener {
//            bottomSheetDialog.dismiss()
//        }
        bottomSheetDialog.setContentView(showSheet)
//        setupFullHeight(bottomSheetDialog, requireContext())
//        setupFullHeight(bottomSheetDialog, requireContext())
        setupHalfHeightForlisting(bottomSheetDialog, requireContext())
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
                        ), customerDetails = CustomerDetails(
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

//    override fun onItemClick(item: CompanyListResponseItem) {
//        Log.e("dskmdsjfjd", "onItemClick: " + item.id)
//        companyname?.setText(item.name)
//
//        llms.visibility = View.VISIBLE
//        deleteDialog.dismiss()
//    }

    override fun onItemClick(item: DriverListResponseModelItem) {
        daname?.setText(item.name)

        DA_id = item.id.toString()

        getDDAMandate()

        textView.visibility = View.VISIBLE

        deleteDialogtwo.dismiss()
    }

    private fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked

        when (view.id) {
            R.id.rbReTraining -> {
//                if (checked) {
//                    radioButtonreturn?.isChecked = false
////                    datextinputlayout?.visibility = View.VISIBLE
//                    textView.visibility = View.GONE
//                    llmtwo.visibility = View.GONE
//                    llmthree.visibility = View.GONE
//                    radioButtonworthy?.isChecked = false
//                    radioButtonnotworthy?.isChecked = false
//                    requesttype?.visibility = View.GONE
//
//                    returnvehiclename?.setText("")
//                    returntextinputlayout?.visibility = View.GONE
//
//                }
            }

            R.id.rbTraining -> {
//                if (checked) {
//                    daname?.setText("")
//                    textView.visibility = View.GONE
//                    radioButtonDA?.isChecked = false
//                    datextinputlayout?.visibility = View.GONE
//                    returntextinputlayout?.visibility = View.VISIBLE
//                }
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


    override fun onItemClick(item: VehicleReturnModelListItem) {
        returnvehiclename?.setText(item.vehicleRegNo)
        edt_select_vehicle?.setText(item.vehicleRegNo)

        Prefs.getInstance(App.instance).vmIdReturnveh = item.vehicleId.toString()
        GetVehicleCurrentInsuranceInfo()
        returbDA_wmID = item.vehicleId.toString()
        binding.pb2.visibility = View.VISIBLE
        getDDAreturnALlocation()
        textView.visibility = View.VISIBLE
//        llmtwo.visibility = View.VISIBLE
//        llmthree.visibility = View.VISIBLE
        deleteDialogthree.dismiss()
    }


    override fun OnItemClickRecyclerViewClicks(
        recyclerViewId: Int, position: Int, selecteditemclicked: String
    ) {
        when (recyclerViewId) {
            R.id.rv_vehicle_location -> {
                edt?.setText(selecteditemclicked)
                rv_locatio?.visibility = View.GONE
                // Handle item click event for RecyclerView1
            }

            R.id.rv_vehicle_fuellevel -> {

                edttwo?.setText(selecteditemclicked)
                rv_fuel?.visibility = View.GONE
            }

            R.id.rv_vehicle_oillevel -> {
                edtthree?.setText(selecteditemclicked)
                rv_oil?.visibility = View.GONE
            }
//                 R.id.recyclerView2 -> {
//                     // Handle item click event for RecyclerView2
//                 }
//                 // Add more conditions for additional RecyclerViews
        }
    }

    fun calview(id: Int) {
        val myFormat = "dd MMM yyyy"

        var cal = Calendar.getInstance()
        var sdf = SimpleDateFormat(myFormat, Locale.US)
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                time = sdf.format(cal.time)
                if (id == 0) {
                    textinput?.setText(time)
                } else if (id == 1) {
                    textinputtwo?.setText(time)
                } else if (id == 2) {
                    textinputthree?.setText(time)

                } else if (id == 3) {
                    textinputfour?.setText(time)
                }
                // mention the format you need
            }

        DatePickerDialog(
            requireContext(),
            dateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()


    }

    fun EditableFalse(view: View) {

        view.isEnabled = false
        view.isClickable = false
        view.isFocusable = false
        view.isFocusableInTouchMode = false
    }

    fun EditableTrue(view: View) {
        view.isEnabled = true
        view.isClickable = true
        view.isFocusable = true
        view.isFocusableInTouchMode = true
    }

    override fun onItemClick(item: GetVehicleRequestTypeItem) {
        requesttypetext?.setText(item.name)
        deleteDialog.dismiss()
    }

    fun updates() {
        Thread {
            GlobalScope.launch {
                if (!App.offlineSyncDB!!.isUserTableEmpty()) {

                    Thread {
                        val user = App.offlineSyncDB!!.getAllUsers()
                        if (user[0].changeDAvechile) {
                            spinner?.setSelection(user.get(0).spinnerposition)
                            radioButtonDA!!.isChecked = user.get(0).changeDAvechile
                            daname?.visibility = View.VISIBLE
                            datextinputlayout?.visibility = View.VISIBLE
                            daname!!.setText(user.get(0).selectDA)
                        } else if (user[0].returnDAvehicle) {
                            spinner?.setSelection(user.get(0).spinnerposition)
                            radioButtonreturn!!.isChecked = user.get(0).returnDAvehicle
                            returnvehiclename!!.setText(user.get(0).selectVehicleReturn)
                            returntextinputlayout!!.visibility = View.VISIBLE
//                            llmtwo.visibility = View.VISIBLE
//                            llmthree.visibility = View.VISIBLE


                        } else if (user[0].returnDAvehicle) {
                            spinner?.setSelection(user.get(0).spinnerposition)
                            radioButtonreturn!!.isChecked = user.get(0).returnDAvehicle
                            returnvehiclename!!.setText(user.get(0).selectVehicleReturn)
                            returntextinputlayout!!.visibility = View.VISIBLE

//                            llmtwo.visibility = View.VISIBLE
//                            llmthree.visibility = View.VISIBLE
//                            requesttype?.visibility = View.VISIBLE

                        }



                        Log.e(
                            "dkjjdkfjkdjkfjkd",
                            "onCreateView: " + App.offlineSyncDB!!.getAllUsers()
                        )
                        //Do your database´s operations here
                    }.start()
                }
            }


        }.start()

    }

    fun updateUI() {
        Thread {
            GlobalScope.launch {
                if (!App.offlineSyncDB!!.isUserTableEmptyInformation()) {

                    Thread {
                        val user = App.offlineSyncDB!!.getAllUsersinfo()
                        edt?.setText(user.get(0).vehiclelocation)
                        edtaddblue?.setText(user.get(0).blueleve)
                        edtcurrentmileage?.setText(user.get(0).currentVehicleMileage)
                        edttwo?.setText(user.get(0).fuelelevel)
                        edtthree?.setText(user.get(0).oillevel)



                        Log.e(
                            "dkjjdkfjkdjkfjkd",
                            "onCreateView: " + App.offlineSyncDB!!.getAllUsers()
                        )
                        //Do your database´s operations here
                    }.start()
                }
            }


        }.start()
    }

    fun updatemainUI() {


        Thread {
            GlobalScope.launch {
                if (!App.offlineSyncDB!!.isUserTableEmpty()) {
                    binding.checkone.visibility = View.VISIBLE
                    binding.textView4.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.img_round
                        )
                    )
                    binding.consttwo.alpha = 1f
                    EditableTrue(binding.textView4)

                } else {
                    binding.checkone.visibility = View.GONE

                    binding.textView4.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.img_round_1
                        )
                    )
                    binding.consttwo.alpha = 0.5f
                    EditableFalse(binding.textView4)
                }
                if (!App.offlineSyncDB!!.isUserTableEmptyInformation()) {

                    binding.checktwo.visibility = View.VISIBLE
                    binding.textView6.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.img_round
                        )
                    )
                    binding.constthree.alpha = 1f
                    EditableTrue(binding.textView6)
                } else {
                    binding.checktwo.visibility = View.GONE
                    binding.constthree.alpha = 0.5f
                    binding.textView6.setImageDrawable(
                        ContextCompat.getDrawable(
                            requireContext(), R.drawable.img_round_1
                        )
                    )
                    EditableFalse(binding.textView6)
                }
            }


        }.start()
    }

    fun setFullAlpha(view: View) {
        view.alpha = 1f
    }

    fun setHalfAlpha(view: View) {
        view.alpha = 0.2f
    }

    fun changeBackgroundImage(view: ImageView) {

        view.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.img_round_1
            )
        )
    }

    fun showAlertRelease() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.release_dialog, null)
        val deleteDialog: androidx.appcompat.app.AlertDialog =
            androidx.appcompat.app.AlertDialog.Builder(requireContext()).create()
        /*        val imageView: ImageView = view.findViewById(R.id.ic_cross_orange)
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
                Prefs.getInstance(App.instance).clebUserIds.toDouble()
            )
            mainViewModel.CreateVehicleReleaseReqlivedata.observe(requireActivity(), Observer {
                if (it != null) {

                    if (it.Status == "200") {

                        Toast.makeText(
                            requireContext(),
                            "Release request is created",
                            Toast.LENGTH_SHORT
                        ).show()
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
}
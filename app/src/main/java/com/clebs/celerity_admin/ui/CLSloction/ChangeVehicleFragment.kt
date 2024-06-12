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
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity_admin.adapters.CompanyListAdapter
import com.clebs.celerity_admin.MainActivityTwo
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.adapters.DriverListAdapter
import com.clebs.celerity_admin.databinding.FragmentGalleryBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.models.CompanyListResponseItem
import com.clebs.celerity_admin.models.DriverListResponseModelItem
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.Onclick
import com.clebs.celerity_admin.utils.OnclickDriver
import com.clebs.celerity_admin.utils.Prefs
import com.clebs.celerity_admin.utils.setupFullHeight
import com.clebs.celerity_admin.viewModels.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.UserFlowParams
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChangeVehicleFragment : Fragment(), Onclick, OnclickDriver {

    private var _binding: FragmentGalleryBinding? = null
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var inspectionID: String
    private lateinit var regexPattern: Regex
    lateinit var deleteDialog: AlertDialog
    lateinit var DriverListAdapter: DriverListAdapter
    private var isfirst: Boolean? = false
    lateinit var Activity: MainActivityTwo
    private var startonetime: Boolean? = false
    private var companyname: EditText? = null
    private var daname: EditText? = null
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
        isfirst = Prefs.getInstance(requireContext()).Isfirst

        Observers()






        cqSDKInitializer.checkOfflineQuoteSyncCompleteStatus() { isSyncCompletedForAllQuotes ->
            if (isSyncCompletedForAllQuotes) {
                Toast.makeText(
                    requireContext(),
                    "Vehicle pictures are uploaded",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        clientUniqueID()
        val textView3: TextView = binding.textView3
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
fun Observers(){
    mainViewModel.GetCompanyListing().observe(viewLifecycleOwner, Observer {
        Log.e("dkfdjkfjkdfjdresponse", "onCreateView: " + it)
        if (it != null) {
            attachmentAdapter.data.addAll(it)
            attachmentAdapter.notifyDataSetChanged()
        }


    })
    mainViewModel.GetDriverListing().observe(viewLifecycleOwner, Observer {
        if (it != null) {
            DriverListAdapter.data.addAll(it)
            DriverListAdapter.notifyDataSetChanged()
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

        companyname?.isEnabled = true
        companyname?.isClickable = true
        companyname?.isFocusable = false
        companyname?.isFocusableInTouchMode = false
        daname?.isEnabled = true
        daname?.isClickable = true
        daname?.isFocusable = false
        daname?.isFocusableInTouchMode = false

        companyname?.setOnClickListener {
            showcompanylistdialog()
        }
        daname?.setOnClickListener {
            ShowDriverListDialog()
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
            binding.down.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_check_24
                )
            )
        }
        imageView.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(showSheet)
        setupFullHeight(bottomSheetDialog, requireContext())

        bottomSheetDialog.show()
    }

    private fun selectVehicleInformation() {
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val showSheet = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.bottom_sheet_vehicle_info,
                requireActivity().findViewById<View>(R.id.bottomSheetRealCL) as ConstraintLayout?
            )

        val imageView: ImageView = showSheet.findViewById(R.id.cancleIV)
        imageView.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.setContentView(showSheet)
//        setupFullHeight(bottomSheetDialog, requireContext())
        setupFullHeight(bottomSheetDialog, requireContext())
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
        deleteDialog =
            AlertDialog.Builder(requireContext()).create()
        deleteDialog.setView(view)
        val list: RecyclerView = view.findViewById(R.id.rv_driver)
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

    override fun onItemClick(item: CompanyListResponseItem) {
        Log.e("dskmdsjfjd", "onItemClick: ")
        companyname?.setText(item.name)
        deleteDialog.dismiss()
    }

    override fun onItemClick(item: DriverListResponseModelItem) {

    }
}
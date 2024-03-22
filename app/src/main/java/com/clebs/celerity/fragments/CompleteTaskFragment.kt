package com.clebs.celerity.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.DriverRouteAdapter
import com.clebs.celerity.adapters.RideAlongAdapter
import com.clebs.celerity.databinding.FragmentCompleteTaskBinding
import com.clebs.celerity.databinding.TimePickerDialogBinding
import com.clebs.celerity.models.requests.SaveBreakTimeRequest
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponse
import com.clebs.celerity.models.response.GetVehicleImageUploadInfoResponse
import com.clebs.celerity.models.response.RideAlongDriverInfoByDateResponse
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.ui.HomeActivity.Companion.checked
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.navigateTo
import com.clebs.celerity.utils.showErrorDialog
import com.clebs.celerity.utils.showTimePickerDialog
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.toRequestBody
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import java.util.UUID

class CompleteTaskFragment : Fragment() {
    lateinit var mbinding: FragmentCompleteTaskBinding
    private var isclicked: Boolean = true
    private lateinit var viewModel: MainViewModel
    private lateinit var imageView: ImageView
    private var userId: Int = 0
    private lateinit var regexPattern: Regex
    private lateinit var inspectionID: String
    private var requestCode: Int = 0
    private var showImageUploadLayout: Boolean = false
    private var isAllImageUploaded: Boolean = false
    private var isInspectionDone: Boolean = false
    private var imagesUploaded: Boolean = false
    private var isClockedIn: Boolean = false
    private var isOnRoadHours: Boolean = false
    private var visibilityLevel = -1
    var breakStartTime: String = ""
    var breakEndTime: String = ""
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var fragmentManager: FragmentManager
    private var imageUploadLevel = 0
    var apiCount = 0

    var inspectionOfflineImagesCHeck: Boolean? = null
    private var inspectionstarted: Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentCompleteTaskBinding.inflate(inflater, container, false)
        }
        val clickListener = View.OnClickListener {
            showAlert()
        }
        loadingDialog = (activity as HomeActivity).loadingDialog
        userId = Prefs.getInstance(requireContext()).userID.toInt()
        mbinding.rlcomtwoBreak.setOnClickListener(clickListener)
        mbinding.downIvsBreak.setOnClickListener(clickListener)
        mbinding.parentBreak.setOnClickListener(clickListener)
        mbinding.ivFaceMask.setImageResource(R.drawable.upload_icc)
        Prefs.getInstance(requireContext()).clearNavigationHistory()
        fragmentManager = (activity as HomeActivity).fragmentManager
        cqSDKInitializer = CQSDKInitializer(requireContext())
        cqSDKInitializer.triggerOfflineSync()
        setProgress()

        inspectionstarted = Prefs.getInstance(requireContext()).getBoolean("Inspection", false)
        viewModel = (activity as HomeActivity).viewModel
        //(activity as HomeActivity).getVehicleLocationInfo()

        showDialog()
        viewModel.GetVehicleImageUploadInfo(Prefs.getInstance(requireContext()).userID.toInt())


        observers()

        showDialog()
        viewModel.GetDriverBreakTimeInfo(userId)
        showDialog()
        viewModel.GetDailyWorkInfoById(userId)
        viewModel.GetDriverRouteInfoByDate(userId)
        viewModel.GetRideAlongDriverInfoByDate(userId)

        BubbleShowCaseBuilder(requireActivity()) //Activity instance
            .title("Start Inspection") //Any title for the bubble view
            .description("Click here to capture Vehicle Images") //More detailed description
            .arrowPosition(BubbleShowCase.ArrowPosition.TOP)
            //You can force the position of the arrow to change the location of the bubble.
            .backgroundColor((requireContext().getColor(R.color.very_light_orange)))
            //Bubble background color
            .textColor(requireContext().getColor(R.color.black)) //Bubble Text color
            .titleTextSize(16) //Title text size in SP (default value 16sp)
            .descriptionTextSize(12) //Subtitle text size in SP (default value 14sp)
            .image(requireContext().resources.getDrawable(R.drawable.baseline_image_search_24)!!) //Bubble main image
            .closeActionImage(requireContext().resources.getDrawable(R.drawable.cross)!!) //Custom close action image

            .listener(
                (object : BubbleShowCaseListener { //Listener for user actions
                    override fun onTargetClick(bubbleShowCase: BubbleShowCase) {
                        //Called when the user clicks the target
                        bubbleShowCase.dismiss()
                    }

                    override fun onCloseActionImageClick(bubbleShowCase: BubbleShowCase) {
                        //Called when the user clicks the close button
                        bubbleShowCase.dismiss()
                    }

                    override fun onBubbleClick(bubbleShowCase: BubbleShowCase) {
                        //Called when the user clicks on the bubble
                        bubbleShowCase.dismiss()
                    }

                    override fun onBackgroundDimClick(bubbleShowCase: BubbleShowCase) {
                        bubbleShowCase.dismiss()
                        //Called when the user clicks on the background dim
                    }
                })
            )
            .targetView(mbinding.startinspection).highlightMode(BubbleShowCase.HighlightMode.VIEW_SURFACE) //View to point out
            .show()
        clientUniqueID()

        mbinding.rlcomtwoClock.setOnClickListener {
            showDialog()
            viewModel.UpdateClockInTime(userId)

        }

        mbinding.rlcomtwoClockOut.setOnClickListener {
            showDialog()
            viewModel.UpdateClockOutTime(userId)
        }

        mbinding.rideAlong.setOnClickListener {
            navigateTo(R.id.rideAlongFragment, requireContext(), findNavController())
        }

        mbinding.headerTop.icpnUser.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }



        if (checked.equals("0")) {
            //findNavController().navigate(R.id.vechileMileageFragment)
            navigateTo(R.id.vechileMileageFragment, requireContext(), findNavController())
        }

        mbinding.clFaceMask.setOnClickListener {
            requestCode = 0
            pictureDialogBase64(mbinding.ivFaceMask, requestCode)
        }
        mbinding.startinspection.setOnClickListener {
            startInspection()
        }

        mbinding.clOilLevel.setOnClickListener {
            requestCode = 5
            pictureDialogBase64(mbinding.ivOilLevel, requestCode)
//            startInspection()
        }

        mbinding.clAddBlueImg.setOnClickListener {
            requestCode = 7
            pictureDialogBase64(mbinding.ivAddBlueImg, requestCode)
//            startInspection()
        }

        mbinding.AddRoute.setOnClickListener {
            navigateTo(R.id.onRoadHoursFragment, requireContext(), findNavController())
        }

        mbinding.rlcom.setOnClickListener {
            if (isclicked) {
                mbinding.taskDetails.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.green_down_arrow)
                mbinding.view2.visibility = View.VISIBLE
            } else {
                mbinding.taskDetails.visibility = View.GONE
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
                mbinding.view2.visibility = View.GONE
                //mbinding.uploadLayouts.visibility = View.VISIBLE
            }
            isclicked = !isclicked
        }
        mbinding.downIv.setOnClickListener {
            if (isclicked) {
                mbinding.taskDetails.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.green_down_arrow)
                mbinding.view2.visibility = View.VISIBLE
            } else {
                mbinding.taskDetails.visibility = View.GONE
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
                mbinding.view2.visibility = View.GONE
                //mbinding.uploadLayouts.visibility = View.VISIBLE
            }
            isclicked = !isclicked
        }


        mbinding.tvNext.setOnClickListener {
            isInspectionDone = true
            mbinding.tvNext.visibility = View.GONE
            mbinding.uploadLayouts.visibility = View.GONE
            mbinding.rlcomtwoClock.visibility = View.VISIBLE
        }

        mbinding.rlcomtwoRoad.setOnClickListener {
            if (mbinding.routeLayout.visibility == View.GONE) mbinding.routeLayout.visibility =
                View.VISIBLE
            else mbinding.routeLayout.visibility = View.GONE
        }
        return mbinding.root
    }

    override fun onResume() {
        super.onResume()

        inspectionstarted = Prefs.getInstance(requireContext()).getBoolean("Inspection", false)
        Log.d("hdhsdshdsdjshhsds", "Ins $inspectionstarted")
        checkInspection()
        if (inspectionstarted?.equals(true) == true) {

            setVisibiltyLevel()

        } else {
            mbinding.startinspection.visibility = View.VISIBLE
        }


    }

    private fun observers() {
        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {

            hideDialog()

            if (it != null) {
                mbinding.headerTop.dxLoc.text = it?.locationName ?: ""
                mbinding.headerTop.dxReg.text = it?.vmRegNo ?: ""
            }

            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                mbinding.headerTop.anaCarolin.text = name
            }
            mbinding.headerTop.dxm5.text = (activity as HomeActivity).date
            val isLeadDriver = (activity as HomeActivity).isLeadDriver
            if (!isLeadDriver) {
                mbinding.rideAlong.visibility = View.GONE
            }
        }


        viewModel.livedataSaveBreakTime.observe(viewLifecycleOwner) {
            if (it != null) {
                //visibilityLevel = 3
            } else {
                // showToast("Something went wrong!!", requireContext())
            }
            hideDialog()
        }

        viewModel.livedataDailyWorkInfoByIdResponse.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (it.ClockedInTime != null) {
                    mbinding.tvClockedIN.text = it.ClockedInTime.toString()
                    isClockedIn = true
                    setVisibiltyLevel()
                } else {
                    isClockedIn = false
                    setVisibiltyLevel()
                }

                if (it.ClockedOutTime != null) {
                    mbinding.clockOutMark.setImageResource(R.drawable.ic_yes)
                    mbinding.clockedOutTime.text = it.ClockedOutTime.toString()
                }
            } else {
                with(mbinding) {
                    listOf(
                        rlcomtwoClock, rlcomtwoBreak, onRoadView, rlcomtwoBreak, rlcomtwoClockOut
                    ).forEach { thisView -> thisView.visibility = View.GONE }
                }
            }
        }

        viewModel.livedataClockInTime.observe(viewLifecycleOwner) {
            hideDialog()
            viewModel.GetDailyWorkInfoById(userId)
            showDialog()
            if (it != null) {
                isClockedIn = true
                setVisibiltyLevel()
            } else {
                showToast("Please add face mask image first", requireContext())
            }
        }

        viewModel.livedataUpdateClockOutTime.observe(viewLifecycleOwner) {
            hideDialog()
            viewModel.GetDailyWorkInfoById(userId)
            showDialog()
            if (it != null) {
                mbinding.clockOutMark.setImageResource(R.drawable.ic_yes)
            }
        }


        viewModel.livedataDriverBreakInfo.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                val latestBreakInfo = it.lastOrNull()

                latestBreakInfo?.let { breakInfo ->
                    val breakTimeEnd = breakInfo.BreakTimeEnd
                    val breakTimeStart = breakInfo.BreakTimeStart
                    if (breakTimeStart.isNotEmpty() && breakTimeEnd.isNotEmpty()) {
                        mbinding.downIvsBreak.setImageResource(R.drawable.ic_yes)
                    } else {
                        showToast("No Break time information added!!", requireContext())
                    }
                } ?: showToast("No Break time information added!!", requireContext())
            } else {

            }
        }

        viewModel.uploadVehicleImageLiveData.observe(viewLifecycleOwner, Observer {
            hideDialog()
            viewModel.GetVehicleImageUploadInfo(Prefs.getInstance(requireContext()).userID.toInt())
            showDialog()
            if (it != null) {
                if (it.Status == "200") {
                    showDialog()
                    viewModel.GetVehicleImageUploadInfo(userId)
                    setImageUploadViews(requestCode, 1)
                } else {
                    setImageUploadViews(requestCode, 0)
                }
            } else {
                setImageUploadViews(requestCode, 0)
            }
        })

        viewModel.vehicleImageUploadInfoLiveData.observe(viewLifecycleOwner, Observer {
            hideDialog()
            println(it)
            if (it != null) {
                if (it!!.Status == "404") {
                    mbinding.vehiclePicturesIB.setImageResource(R.drawable.ic_cross)
                    showImageUploadLayout = true
                    imagesUploaded = false
                    setVisibiltyLevel()
                } else {
                    if (it.IsVehicleImageUploaded == false) {
                        showImageUploadLayout = true
                        imagesUploaded = false
                        setVisibiltyLevel()
                        //  mbinding.vehiclePicturesIB.setImageResource(R.drawable.ic_cross)
                    } else {

                        showImageUploadLayout = checkNull(it)

                        if (showImageUploadLayout) {
                            imagesUploaded = false
                            setVisibiltyLevel()
                            //   mbinding.vehiclePicturesIB.setImageResource(R.drawable.ic_cross)
                            mbinding.taskDetails.visibility = View.VISIBLE
                        } else {
                            imagesUploaded = true
                            setVisibiltyLevel()
                            isAllImageUploaded = true
                        }
                        if (it.DaVehImgFaceMaskFileName != null) {
                            imageUploadLevel = 1
                            setProgress()
                            mbinding.ivFaceMask.setImageResource(
                                R.drawable.ic_yes
                            )
                        }

                        if (it.DaVehicleAddBlueImage != null) {
                            imageUploadLevel = 2
                            setProgress()

                            mbinding.ivAddBlueImg.setImageResource(
                                R.drawable.ic_yes
                            )
                        }

                        if (it.DaVehImgOilLevelFileName != null) {
                            imageUploadLevel = 3
                            setProgress()
                            mbinding.ivOilLevel.setImageResource(
                                R.drawable.ic_yes
                            )
                        }

                        if (it.DaVehicleAddBlueImage != null && it.DaVehImgOilLevelFileName != null && it.DaVehImgFaceMaskFileName != null) {
                            imageUploadLevel = 3
                        }

                        mbinding.run {
                            mbinding.tvNext.isEnabled =
                                it.DaVehicleAddBlueImage != null && it.DaVehImgFaceMaskFileName != null && it.DaVehImgOilLevelFileName != null
                            if (tvNext.isEnabled) {
                                tvNext.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.white
                                    )
                                )
                            } else {
                                tvNext.setTextColor(
                                    ContextCompat.getColor(
                                        requireContext(), R.color.orange
                                    )
                                )
                            }
                        }
                    }
                }
            }
        })


        val adapter = DriverRouteAdapter(GetDriverRouteInfoByDateResponse())

        mbinding.getDriverRouteId.adapter = adapter
        mbinding.getDriverRouteId.layoutManager = LinearLayoutManager(requireContext())

        viewModel.liveDatadriverInfobyRouteDate.observe(viewLifecycleOwner) { routes ->
            routes?.let {
                if(it!=null){
                    adapter.list.clear()
                    adapter.list.addAll(it)
                    adapter.notifyDataSetChanged()
                }
            } ?: run {
            }
        }

        val rideAlongAdapter = RideAlongAdapter(RideAlongDriverInfoByDateResponse(),findNavController(),Prefs.getInstance(requireContext()))

        mbinding.questionareRv.adapter = rideAlongAdapter
        mbinding.questionareRv.layoutManager = LinearLayoutManager(requireContext())

        viewModel.liveDataRideAlongDriverInfoByDateResponse.observe(viewLifecycleOwner) { rideAlongs ->
            rideAlongs.let {
                if(it!=null){
                    rideAlongAdapter.data.clear()
                    rideAlongAdapter.data.addAll(it)
                    rideAlongAdapter.notifyDataSetChanged()
                }
            }

        }

    }

    private fun checkNull(res: GetVehicleImageUploadInfoResponse): Boolean {
        return res.DaVehImgFaceMaskFileName == null || res.DaVehicleAddBlueImage == null || res.DaVehImgOilLevelFileName == null
    }

    private fun chkTime(edtBreakstart: EditText, edtBreakend: EditText): Boolean {

        val startTime = edtBreakstart.text.toString()
        val endTime = edtBreakend.text.toString()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = sdf.parse(startTime)
        val end = sdf.parse(endTime)
        if (start != null) {
            if (start.before(end)) return true
        }
        return false
    }

    private fun showAlert() {
        val dialogBinding = TimePickerDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        deleteDialog.setView(dialogBinding.root)

        deleteDialog.setCanceledOnTouchOutside(true)
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.show()

        dialogBinding.icCrossOrange.setOnClickListener {
            deleteDialog.cancel()
        }
        dialogBinding.edtBreakstart.setOnClickListener {
            showTimePickerDialog(requireContext(), dialogBinding.edtBreakstart)
        }
        dialogBinding.edtBreakend.setOnClickListener {
            showTimePickerDialog(requireContext(), dialogBinding.edtBreakend)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val startText = dialogBinding.edtBreakstart.text.toString()
                val endText = dialogBinding.edtBreakend.text.toString()

                if (startText.isNotEmpty() && endText.isNotEmpty()) {
                    breakStartTime = startText
                    breakEndTime = endText
                    dialogBinding.timeTvNext.isEnabled = true
                    dialogBinding.timeTvNext.setTextColor(Color.WHITE)
                }
            }
        }

        dialogBinding.edtBreakstart.addTextChangedListener(textWatcher)
        dialogBinding.edtBreakend.addTextChangedListener(textWatcher)

        dialogBinding.timeTvNext.setOnClickListener {
            if (chkTime(dialogBinding.edtBreakstart, dialogBinding.edtBreakend)) {
                deleteDialog.cancel()
                sendBreakTimeData()
            } else {
                showErrorDialog(fragmentManager, "CTF-02", "Please add valid time information")
            }
        }

    }

    private fun sendBreakTimeData() {
        showDialog()
        viewModel.SaveBreakTime(
            SaveBreakTimeRequest(
                UserId = userId.toString(),
                DawDriverBreakId = "null",
                BreakStartTime = breakStartTime,
                BreakFinishTime = breakEndTime
            )
        )
    }


    protected fun pictureDialogBase64(iv: ImageView, codes: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runWithPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO

            ) {
                showPictureDialog(iv, codes)
            }
        } else {
            runWithPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE

            ) {
                showPictureDialog(iv, codes)
            }
        }
    }

    private fun showPictureDialog(iv: ImageView, codes: Int) {
        imageView = iv
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, codes)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            sendImage(imageBitmap, requestCode)
        }
    }

    private fun sendImage(imageBitmap: Bitmap, requestCode: Int) {

        val uniqueFileName = "image_${UUID.randomUUID()}.jpg"
        val requestBody = imageBitmap.toRequestBody()

        val imagePart = when (requestCode) {
            0 -> {
                MultipartBody.Part.createFormData(
                    "uploadFaceMaskImage", uniqueFileName, requestBody
                )
            }

            1 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleDashBoardImage", uniqueFileName, requestBody
                )
            }

            2 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleFrontImage", uniqueFileName, requestBody
                )
            }

            3 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleNearSideImage", uniqueFileName, requestBody
                )
            }

            4 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleRearImage", uniqueFileName, requestBody
                )
            }

            5 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleOilLevelImage", uniqueFileName, requestBody
                )
            }

            6 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleOffSideImage", uniqueFileName, requestBody
                )
            }

            7 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleAddBlueImage", uniqueFileName, requestBody
                )
            }

            else -> throw IllegalArgumentException()
        }

        showDialog()
        viewModel.uploadVehicleImage(userId, imagePart, requestCode)

    }

    private fun setImageUploadViews(requestCode: Int, type: Int) {
        var drawableID: Int = R.drawable.ic_yes
        if (type != 1) drawableID = R.drawable.refresh

        when (requestCode) {
            0 -> mbinding.ivFaceMask.setImageResource(drawableID)
            1 -> mbinding.ivVehicleDashboard.setImageResource(drawableID)
            2 -> mbinding.ivFront.setImageResource(drawableID)
            3 -> mbinding.ivNearSide.setImageResource(drawableID)
            4 -> mbinding.ivRearImgUp.setImageResource(drawableID)
            5 -> mbinding.ivOilLevel.setImageResource(drawableID)
            6 -> mbinding.ivOffSideImgUp.setImageResource(drawableID)
            7 -> mbinding.ivAddBlueImg.setImageResource(drawableID)
        }
    }

    private fun clientUniqueID(): String {
        val x = Prefs.getInstance(App.instance).userID.toString()
        val y = Prefs.getInstance(App.instance).get("vrn")
        // example string
        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))

        regexPattern = Regex("${x.take(3)}${y?.take(3)}${formattedDate}")
        inspectionID = regexPattern.toString()
        return regexPattern.toString()
        Log.e("resistrationvrnpatterhn", "clientUniqueID: " + regexPattern + inspectionID)
    }

    private fun startInspection() {
        if (isAllImageUploaded) {
            mbinding.tvNext.visibility = View.VISIBLE
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            loadingDialog.show()

            if (cqSDKInitializer.isCQSDKInitialized()) {
                // Show a loading dialog

                Log.e("totyototyotoytroitroi", "startInspection: " + inspectionID)
                Log.e("sdkskdkdkskdkskd", "onCreateView: ")
                // Make request to start an inspection
                try {
                    cqSDKInitializer.startInspection(activityContext = requireActivity(),
                        clientAttrs = ClientAttrs(
                            userName = "",
                            dealer = "",
                            dealerIdentifier = "",
                            client_unique_id = inspectionID //drivers ID +vechile iD + TOdays date dd// mm //yy::tt,mm
                        ),
                        result = { isStarted, msg, code ->

                            Log.e("messsagesss", "startInspection: " + msg + code)
                            if (isStarted) {
//
                            } else {
//
                            }
                            if (msg == "Success") {

                                loadingDialog.cancel()
                            }
                            if (!isStarted) {

                                loadingDialog.cancel()
                                Log.e("startedinspection", "onCreateView: " + msg + isStarted)


                            }
                        })
                } catch (_: Exception) {

                    showErrorDialog(fragmentManager, "CTF-02", "Please try again later!!")
                }
            }
        } else {
            showErrorDialog(
                fragmentManager,
                "CTF-1",
                "We are currently updating our app for Android 13+ devices. Please try again later."
            )
        }

    }

    private fun visibiltyControlls() {
        with(mbinding) {
            listOf(
                uploadLayouts,
                rlcomtwoBreak,
                onRoadView,
                rlcomtwoBreak,
                rlcomtwoClock,
                rlcomtwoClockOut,
                taskDetails
            ).forEach { thisView -> thisView.visibility = View.GONE }
        }
        when (visibilityLevel) {
            -1 -> {
                mbinding.uploadLayouts.visibility = View.VISIBLE
                mbinding.taskDetails.visibility = View.VISIBLE
                mbinding.imageUploadView.visibility = View.GONE
                /*mbinding.clFaceMask.visibility = View.GONE
                mbinding.clOilLevel.visibility = View.GONE*/
            }

            0 -> {
                mbinding.uploadLayouts.visibility = View.VISIBLE
                mbinding.taskDetails.visibility = View.VISIBLE
                mbinding.imageUploadView.visibility = View.VISIBLE
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.singlecheckmark)
            }

            1 -> {
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.frame__2_)
                mbinding.rlcomtwoClock.visibility = View.VISIBLE
            }

            2 -> {
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.frame__2_)
                mbinding.onRoadView.visibility = View.VISIBLE
                mbinding.rlcomtwoBreak.visibility = View.VISIBLE
            }

            3 -> {
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.frame__2_)
                mbinding.rlcomtwoClockOut.visibility = View.VISIBLE
            }
        }
    }

    private fun setVisibiltyLevel() {
        visibilityLevel = 0
        if (!inspectionstarted && !imagesUploaded) {
            visibilityLevel = -1
            visibiltyControlls()
            return
        }
        if (!imagesUploaded) {
            visibilityLevel = 0
            visibiltyControlls()
            return
        } else {
            visibilityLevel += 1
        }
        if (isClockedIn) {
            visibilityLevel += 1
        }
        if (isOnRoadHours) visibilityLevel += 1

        visibiltyControlls()
    }

    private fun setProgress() {
        val progressBar = mbinding.progressContainer.progressBarStep1
        mbinding.clAddBlueImg.visibility = View.GONE
        mbinding.clFaceMask.visibility = View.GONE
        mbinding.clOilLevel.visibility = View.GONE
        when (imageUploadLevel) {
            0 -> {
                progressBar.setProgress(13, true)
                mbinding.clFaceMask.visibility = View.VISIBLE

            }

            1 -> {
                progressBar.setProgress(45, true)
                mbinding.clAddBlueImg.visibility = View.VISIBLE

            }

            2 -> {
                progressBar.setProgress(70, true)
                mbinding.clOilLevel.visibility = View.VISIBLE
            }

            else -> {
                //    mbinding.clFaceMask.visibility = View.VISIBLE
                progressBar.setProgress(100, true)
                progressBar.setBackgroundColor(Color.GREEN)
            }
        }
    }

    private fun checkInspection() {
        if (inspectionstarted?.equals(true) == true) {
            Timer().scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    cqSDKInitializer.checkOfflineQuoteSyncCompleteStatus() { isSyncCompletedForAllQuotes ->
                        //Log.e("hdhsdshdsdjshhsds", "run========: $isSyncCompletedForAllQuotes")
                        inspectionOfflineImagesCHeck = isSyncCompletedForAllQuotes
                        /*    if (isSyncCompletedForAllQuotes)
                                //setProgress()*/
                    }
                }
            }, 0, 1000)
            mbinding.startinspection.visibility = View.GONE

        } else {
            mbinding.startinspection.visibility = View.VISIBLE
        }
    }

    private fun hideDialog() {
        apiCount--
        if (apiCount <= 0) {
            loadingDialog.cancel()
            apiCount = 0
        }
    }

    private fun showDialog() {
        if (apiCount == 0) {
            loadingDialog.show()
        }
        apiCount++
    }
}
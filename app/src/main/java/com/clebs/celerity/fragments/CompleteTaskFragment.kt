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
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentCompleteTaskBinding
import com.clebs.celerity.databinding.TimePickerDialogBinding
import com.clebs.celerity.models.requests.SaveBreakTimeRequest
import com.clebs.celerity.models.response.GetVehicleImageUploadInfoResponse
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
    var breakStartTime: String = ""
    var breakEndTime: String = ""
    lateinit var loadingDialog:LoadingDialog
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var fragmentManager:FragmentManager
    var inspectionOfflineImagesCHeck: Boolean? = null

    companion object {
        var inspectionstarted: Boolean? = null
    }

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


        if (inspectionstarted?.equals(true) == true) {
            Timer().scheduleAtFixedRate( object : TimerTask() {
                override fun run() {
                    cqSDKInitializer.checkOfflineQuoteSyncCompleteStatus() { isSyncCompletedForAllQuotes ->

                        inspectionOfflineImagesCHeck=isSyncCompletedForAllQuotes//if true then show double tick green


                        //if only inspection started is true then single tick or pending
                        Log.e("hdhsdshdsdjshhsds", "run========: " +isSyncCompletedForAllQuotes)
                    }

                }
            }, 0, 1000)
            mbinding.startinspection.visibility = View.GONE

        } else {
            mbinding.startinspection.visibility = View.VISIBLE
        }

        viewModel = (activity as HomeActivity).viewModel
        (activity as HomeActivity).getVehicleLocationInfo()

        viewModel.GetVehicleImageUploadInfo(Prefs.getInstance(requireContext()).userID.toInt())

        observers()

        viewModel.GetDriverBreakTimeInfo(userId)
        viewModel.GetDailyWorkInfoById(userId)

        observers()
        clientUniqueID()


        mbinding.rlcomtwoClock.setOnClickListener {
            loadingDialog.show()
            viewModel.UpdateClockInTime(userId)
        }

        mbinding.rlcomtwoClockOut.setOnClickListener {
            loadingDialog.show()
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
        if (inspectionstarted?.equals(true) == true) {

            mbinding.startinspection.visibility = View.GONE

        } else {
            mbinding.startinspection.visibility = View.VISIBLE
        }
    }

    private fun observers() {
        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                loadingDialog.cancel()
                mbinding.headerTop.dxLoc.text = it?.locationName ?: ""
                mbinding.headerTop.dxReg.text = it?.vmRegNo ?: ""
                "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                    mbinding.headerTop.anaCarolin.text = name
                }
                mbinding.headerTop.dxm5.text = (activity as HomeActivity).date
                val isLeadDriver = (activity as HomeActivity).isLeadDriver
                if (!isLeadDriver) {
                    mbinding.rideAlong.visibility = View.GONE
                }
            }
        }

        viewModel.livedataSaveBreakTime.observe(viewLifecycleOwner) {
            if (it != null) {
                //visibilityLevel = 3
            } else {
                // showToast("Something went wrong!!", requireContext())
            }
            loadingDialog.cancel()
        }

        viewModel.livedataDailyWorkInfoByIdResponse.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
            if (it != null) {
                if (it.ClockedInTime != null) {
                    mbinding.tvClockedIN.text = it.ClockedInTime.toString()
                    isClockedIn = true
                    setVisibiltyLevel()/*       mbinding.rlcomtwoClock.visibility = View.GONE
                           mbinding.rlcomtwoClockOut.visibility = View.VISIBLE
                           mbinding.onRoadView.visibility = View.VISIBLE
                           mbinding.rlcomtwoBreak.visibility = View.VISIBLE*/
                } else {/*        with(mbinding) {
                                listOf(
                                    rlcomtwoBreak,
                                    onRoadView,
                                    rlcomtwoBreak,
                                    rlcomtwoClockOut
                                ).forEach { thisView -> thisView.visibility = View.GONE }
                            }*/
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
            viewModel.GetDailyWorkInfoById(userId)
            if (it != null) {
                isClockedIn = true
                setVisibiltyLevel()
            } else {
                showToast("Please add face mask image first", requireContext())
            }
        }

        viewModel.livedataUpdateClockOutTime.observe(viewLifecycleOwner) {
            viewModel.GetDailyWorkInfoById(userId)
            if (it != null) {
                mbinding.clockOutMark.setImageResource(R.drawable.ic_yes)
            }
        }


        viewModel.livedataDriverBreakInfo.observe(viewLifecycleOwner) {
            loadingDialog.cancel()
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
                //  showToast("Something went wrong!!", requireContext())
            }
        }

        viewModel.uploadVehicleImageLiveData.observe(viewLifecycleOwner, Observer {

            loadingDialog.cancel()
            viewModel.GetVehicleImageUploadInfo(Prefs.getInstance(requireContext()).userID.toInt())
            if (it != null) {
                if (it.Status == "200") {
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
            loadingDialog.cancel()
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
                        mbinding.vehiclePicturesIB.setImageResource(R.drawable.ic_cross)
                    } else {

                        showImageUploadLayout = checkNull(it)

                        if (showImageUploadLayout) {
                            imagesUploaded = false
                            setVisibiltyLevel()
                            mbinding.vehiclePicturesIB.setImageResource(R.drawable.ic_cross)
                            mbinding.taskDetails.visibility = View.VISIBLE
                        } else {
                            imagesUploaded = true
                            setVisibiltyLevel()
                            isAllImageUploaded = true
                        }

                        if (it.DaVehImgDashBoardFileName != null) mbinding.ivVehicleDashboard.setImageResource(
                            R.drawable.ic_yes
                        )

                        if (it.DaVehImgFaceMaskFileName != null) mbinding.ivFaceMask.setImageResource(
                            R.drawable.ic_yes
                        )

                        if (it.DaVehImgRearFileName != null) mbinding.ivRearImgUp.setImageResource(R.drawable.ic_yes)

                        if (it.DaVehImgFrontFileName != null) mbinding.ivFront.setImageResource(R.drawable.ic_yes)

                        if (it.DaVehImgNearSideFileName != null) mbinding.ivNearSide.setImageResource(
                            R.drawable.ic_yes
                        )

                        if (it.DaVehImgOffSideFileName != null) mbinding.ivOffSideImgUp.setImageResource(
                            R.drawable.ic_yes
                        )

                        if (it.DaVehicleAddBlueImage != null) mbinding.ivAddBlueImg.setImageResource(
                            R.drawable.ic_yes
                        )

                        if (it.DaVehImgOilLevelFileName != null) mbinding.ivOilLevel.setImageResource(
                            R.drawable.ic_yes
                        )

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
                loadingDialog.show()
                sendBreakTimeData()
            } else {
                showErrorDialog(fragmentManager, "CTF-02", "Please add valid time information")
                //showToast("Please add valid time information", requireContext())
            }
        }

    }

    private fun sendBreakTimeData() {

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
        loadingDialog.show()
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

    fun clientUniqueID(): String {
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

    fun startInspection() {
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
                            // Show error if required
                            Log.e("messsagesss", "startInspection: " + msg + code)
                            if (isStarted) {
//                        mbinding.uploadll1.visibility = View.GONE
//                        mbinding.clOffSideImgUp.visibility = View.GONE
//                        mbinding.rlFirst.visibility = View.GONE
//                        mbinding.rlSecond.visibility = View.GONE
                            } else {
//                        mbinding.uploadll1.visibility = View.VISIBLE
//                        mbinding.clOffSideImgUp.visibility = View.VISIBLE
//                        mbinding.rlFirst.visibility = View.VISIBLE
//                        mbinding.rlSecond.visibility = View.VISIBLE
                            }
                            if (msg == "Success") {
                                //mbinding.completeTaskFragmentPB.visibility = View.GONE
                                loadingDialog.cancel()
                            }
                            if (!isStarted) {
                                //mbinding.completeTaskFragmentPB.visibility = View.GONE
                                loadingDialog.cancel()
                                Log.e("startedinspection", "onCreateView: " + msg + isStarted)
                                // Dismiss the loading dialog

                            }
                        })
                } catch (_: Exception) {
                    //showToast("Please try again later!!",requireContext())
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
            0 -> {

                mbinding.uploadLayouts.visibility = View.VISIBLE
                mbinding.taskDetails.visibility = View.VISIBLE
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
}
package com.clebs.celerity.fragments

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.BreakTimeAdapter
import com.clebs.celerity.adapters.DriverRouteAdapter
import com.clebs.celerity.adapters.RideAlongAdapter
import com.clebs.celerity.databinding.FragmentCompleteTaskBinding
import com.clebs.celerity.databinding.TimePickerDialogBinding
import com.clebs.celerity.models.requests.SaveBreakTimeRequest
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponse
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponse
import com.clebs.celerity.models.response.GetVehicleImageUploadInfoResponse
import com.clebs.celerity.models.response.RideAlongDriverInfoByDateResponse
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.ui.HomeActivity.Companion.checked
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getLoc
import com.clebs.celerity.utils.getVRegNo
import com.clebs.celerity.utils.navigateTo
import com.clebs.celerity.utils.showErrorDialog
import com.clebs.celerity.utils.showTimePickerDialog
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.toRequestBody
import com.tapadoo.alerter.Alerter
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
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
    lateinit var ivX: ImageView
    var codeX = 0
    private var requestCode: Int = 0
    private var showImageUploadLayout: Boolean = false
    private var isAllImageUploaded: Boolean = false
    private var isInspectionDone: Boolean = false
    private var imagesUploaded: Boolean = false
    lateinit var rideAlongAdapter: RideAlongAdapter
    private var isClockedIn: Boolean = false
    private var isOnRoadHours: Boolean = false
    private var isBreakTimeAdded: Boolean = false
    private var visibilityLevel = -1
    var breakStartTime: String = ""
    var breakEndTime: String = ""
    var scannedvrn: String = ""
    private lateinit var loadingDialog: LoadingDialog
    private var b1 = false
    private var b2 = false
    var breakTimeSent = false
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var fragmentManager: FragmentManager
    private var imageUploadLevel = 0
    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }
    var inspectionOfflineImagesCHeck: Boolean? = null
    private var inspectionstarted: Boolean = false

    companion object {
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,

                ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
//                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    add(Manifest.permission.READ_MEDIA_VIDEO)
//                    add(Manifest.permission.READ_MEDIA_IMAGES)
//                    add(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }.toTypedArray()
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
        val currentDate =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(
                Date()
            )




        loadingDialog = (activity as HomeActivity).loadingDialog
        userId = Prefs.getInstance(requireContext()).clebUserId.toInt()
        mbinding.rlcomtwoBreak.setOnClickListener(clickListener)
        mbinding.addBreakIV.setOnClickListener(clickListener)

        mbinding.downIvsBreak.setOnClickListener(clickListener)
        mbinding.parentBreak.setOnClickListener(clickListener)
        mbinding.h1.setOnClickListener {
            if (mbinding.breakH2.isVisible) {
                mbinding.breakH2.visibility = View.GONE
                mbinding.badgeArrow.setImageResource(R.drawable.grey_right_arrow)
            } else {
                mbinding.breakH2.visibility = View.VISIBLE
                mbinding.badgeArrow.setImageResource(R.drawable.down_arrow)
            }
        }
        mbinding.ivFaceMask.setImageResource(io.clearquote.assessment.cq_sdk.R.drawable.camera_icon)
        Prefs.getInstance(requireContext()).clearNavigationHistory()
        fragmentManager = (activity as HomeActivity).fragmentManager
        cqSDKInitializer = CQSDKInitializer(requireContext())
        cqSDKInitializer.triggerOfflineSync()
        setProgress()

        //inspectionstarted = Prefs.getInstance(requireContext()).getBoolean("Inspection", false)
        inspectionstarted = Prefs.getInstance(requireContext()).isInspectionDoneToday()
        viewModel = (activity as HomeActivity).viewModel
        showDialog()
        viewModel.GetVehicleInfobyDriverId(
            Prefs.getInstance(App.instance).clebUserId.toInt(),
            currentDate
        )
        viewModel.livedataGetVehicleInfobyDriverId.observe(viewLifecycleOwner) {

            if (it != null) {

                scannedvrn = it.vmRegNo
                Prefs.getInstance(App.instance).scannedVmRegNo = it.vmRegNo
                if (!Prefs.getInstance(App.instance).VmID.isNotEmpty()) {
                    Prefs.getInstance(App.instance).VmID = it.vmId.toString()
                }
            }
        }
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.completeTaskFragment)
        viewModel.GetVehicleImageUploadInfo(Prefs.getInstance(requireContext()).clebUserId.toInt())

        observers()
        showDialog()
        viewModel.GetDriverBreakTimeInfo(userId)
        showDialog()
        viewModel.GetDailyWorkInfoById(userId)
        viewModel.GetDriverRouteInfoByDate(userId)
        viewModel.GetRideAlongDriverInfoByDate(userId)
        if (mbinding.startinspection.isVisible) {
            val anim = ValueAnimator.ofFloat(1f, 1.2f)
            anim.setDuration(1000)
            anim.addUpdateListener { animation ->
                mbinding.startinspection.setScaleX(animation.animatedValue as Float)
                mbinding.startinspection.setScaleY(animation.animatedValue as Float)
            }
            anim.repeatCount = 3
            anim.repeatMode = ValueAnimator.REVERSE
            anim.start()
//            mbinding.startinspection.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up));
        }

        /*  BubbleShowCaseBuilder(requireActivity()) //Activity instance
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
              .show()*/
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
            //navigateTo(R.id.rideAlongFragment, requireContext(), findNavController())
            findNavController().popBackStack()
            findNavController().navigate(R.id.rideAlongFragment)
        }

        mbinding.headerTop.icpnUser.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }



        if (checked.equals("0")) {
            //findNavController().navigate(R.id.vechileMileageFragment)
            navigateTo(R.id.vechileMileageFragment, requireContext(), findNavController())
        }
        mbinding.ivFaceMask.setOnClickListener {
            requestCode = 0
            pictureDialogBase64(mbinding.ivFaceMask, requestCode)
        }
        mbinding.clFaceMask.setOnClickListener {
            requestCode = 0
            pictureDialogBase64(mbinding.ivFaceMask, requestCode)
        }
        mbinding.startinspection.setOnClickListener {
            startInspection()
        }
        mbinding.ivOilLevel.setOnClickListener {
            requestCode = 5
            pictureDialogBase64(mbinding.ivOilLevel, requestCode)
        }
        mbinding.clOilLevel.setOnClickListener {
            requestCode = 5
            pictureDialogBase64(mbinding.ivOilLevel, requestCode)
//            startInspection()
        }
        mbinding.ivAddBlueImg.setOnClickListener {
            requestCode = 7
            pictureDialogBase64(mbinding.ivAddBlueImg, requestCode)
        }
        mbinding.clAddBlueImg.setOnClickListener {
            requestCode = 7
            pictureDialogBase64(mbinding.ivAddBlueImg, requestCode)
//            startInspection()
        }

        mbinding.AddRoute.setOnClickListener {
            Prefs.getInstance(requireContext()).saveDriverRouteInfoByDate(null)
            navigateTo(R.id.onRoadHoursFragment, requireContext(), findNavController())
        }

        mbinding.rlcom.setOnClickListener {
            if (isclicked) {
                mbinding.taskDetails.visibility = View.VISIBLE
//                viewVisibleAnimator( mbinding.taskDetails)
                mbinding.downIv.setImageResource(R.drawable.green_down_arrow)
                mbinding.view2.visibility = View.VISIBLE
//                viewVisibleAnimator(mbinding.view2)
            } else {
                mbinding.taskDetails.visibility = View.GONE
//                viewGoneAnimator(mbinding.taskDetails)
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
                mbinding.view2.visibility = View.GONE
//                viewGoneAnimator(mbinding.view2)
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
            if (mbinding.routeLayout.visibility == View.GONE) {
                mbinding.routeLayout.visibility =
                    View.VISIBLE
                mbinding.linerlcomtwo.visibility = View.VISIBLE
                mbinding.downIvsRoad.setImageResource(R.drawable.down_arrow)
            } else {
                mbinding.routeLayout.visibility = View.GONE
                mbinding.linerlcomtwo.visibility = View.GONE
                mbinding.downIvsRoad.setImageResource(R.drawable.grey_right_arrow)
            }
        }


        return mbinding.root
    }

    private fun requestpermissions() {

        activityResultLauncher.launch(CompleteTaskFragment.REQUIRED_PERMISSIONS)

    }

    private fun allPermissionsGranted() = CompleteTaskFragment.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in CompleteTaskFragment.REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast("Permission denied", requireContext())
            } else {
                showPictureDialog(ivX, codeX)
            }
        }

    override fun onResume() {
        super.onResume()

        inspectionstarted = Prefs.getInstance(requireContext()).isInspectionDoneToday()
        Log.d("hdhsdshdsdjshhsds", "Ins $inspectionstarted")
        checkInspection()
        if (inspectionstarted?.equals(true) == true) {
            setVisibiltyLevel()
        } else {
            mbinding.startinspection.visibility = View.VISIBLE
        }
    }

    private fun observers() {
        "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
            mbinding.headerTop.anaCarolin.text = name
        }
        mbinding.headerTop.dxLoc.text = getLoc(prefs = Prefs.getInstance(requireContext()))
        mbinding.headerTop.dxReg.text = getVRegNo(prefs = Prefs.getInstance(requireContext()))

        mbinding.headerTop.dxm5.text = (activity as HomeActivity).date
        val isLeadDriver = (activity as HomeActivity).isLeadDriver
        if (!isLeadDriver) {
            mbinding.rideAlong.visibility = View.GONE
        }

        if (mbinding.headerTop.dxReg.text.isEmpty() || mbinding.headerTop.dxReg.text == "")
            mbinding.headerTop.strikedxRegNo.visibility = View.VISIBLE
        else
            mbinding.headerTop.strikedxRegNo.visibility = View.GONE

        if (mbinding.headerTop.dxLoc.text.isEmpty() || mbinding.headerTop.dxLoc.text == "" || mbinding.headerTop.dxLoc.text == "Not Allocated")
            mbinding.headerTop.strikedxLoc.visibility = View.VISIBLE
        else
            mbinding.headerTop.strikedxLoc.visibility = View.GONE

        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {

            hideDialog()
            if (it != null) {
                if (Prefs.getInstance(requireContext()).currLocationName.isNotEmpty()) {
                    mbinding.headerTop.dxLoc.text =
                        Prefs.getInstance(requireContext()).currLocationName ?: ""
                } else if (Prefs.getInstance(requireContext()).workLocationName.isNotEmpty()) {
                    mbinding.headerTop.dxLoc.text =
                        Prefs.getInstance(requireContext()).workLocationName ?: ""
                } else {
                    mbinding.headerTop.dxLoc.text = it.locationName ?: ""
                }

                if (it.vmId != 0)
                    Prefs.getInstance(requireContext()).vmId = it.vmId

                mbinding.headerTop.dxReg.text =
                    getVRegNo(prefs = Prefs.getInstance(requireContext()))
                if (mbinding.headerTop.dxReg.text.isEmpty())
                    mbinding.headerTop.strikedxRegNo.visibility = View.VISIBLE
                else
                    mbinding.headerTop.strikedxRegNo.visibility = View.GONE
                if (mbinding.headerTop.dxLoc.text.isEmpty() || mbinding.headerTop.dxLoc.text == "")
                    mbinding.headerTop.strikedxLoc.visibility = View.VISIBLE
                else
                    mbinding.headerTop.strikedxLoc.visibility = View.GONE
            }
            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                mbinding.headerTop.anaCarolin.text = name
            }

            mbinding.headerTop.dxm5.text = (activity as HomeActivity).date
        }


        viewModel.livedataSaveBreakTime.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (breakTimeSent) {
                    breakTimeSent = false
                    Alerter.create(requireActivity())
                        .setTitle("")
                        .setIcon(R.drawable.logo_new)
                        .setText("Break Time Added successfully")
                        .setBackgroundColorInt(resources.getColor(R.color.medium_orange))
                        .show()
                }
                showDialog()
                viewModel.GetDriverBreakTimeInfo(userId)
            } else {

            }
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
                    mbinding.clockOutMark.setImageResource(R.drawable.finalclockout)
                    mbinding.rlcomtwoClockOut.isEnabled = false
                    mbinding.clockOutTV.text = "Clocked Out"
                    mbinding.rlcomtwoClockOut.isClickable = false
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

        viewModel.ldcompleteTaskLayoutObserver.observe(viewLifecycleOwner) {
            if (it == -1) {
                mbinding.mainCompleteTask.visibility = View.VISIBLE
                mbinding.searchLayout.visibility = View.GONE
            } else {
                mbinding.mainCompleteTask.visibility = View.GONE
                mbinding.searchLayout.visibility = View.VISIBLE
            }
        }


        viewModel.livedataClockInTime.observe(viewLifecycleOwner) {
            hideDialog()
            viewModel.GetDailyWorkInfoById(userId)
            showDialog()
            if (it != null) {

                isClockedIn = true
//                if (isClockedIn){
//                    Alerter.create(requireActivity())
//                        .setTitle("")
//                        .setIcon(R.drawable.logo_new)
//                        .setText("Clocked in successfully")
//                        .setBackgroundColorInt(resources.getColor(R.color.medium_orange))
//                        .show()
//                }
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
                mbinding.clockOutMark.setImageResource(R.drawable.finalclockout)
                mbinding.rlcomtwoClockOut.isEnabled = false
                mbinding.clockOutTV.text = "Clocked Out"
                mbinding.rlcomtwoClockOut.isClickable = false


            }
        }

        var breakTimeadapter =
            BreakTimeAdapter(GetDriverBreakTimeInfoResponse(), viewModel, showDialog)
        mbinding.BreakTimeRV.adapter = breakTimeadapter
        mbinding.BreakTimeRV.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)


        viewModel.livedataDriverBreakInfo.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                val latestBreakInfo = it.lastOrNull()

                latestBreakInfo?.let { breakInfo ->
                    val breakTimeEnd = breakInfo.BreakTimeEnd
                    val breakTimeStart = breakInfo.BreakTimeStart
                    if (breakTimeStart.isNotEmpty() && breakTimeEnd.isNotEmpty()) {
                        val reversedList = it.reversed()
                        breakTimeadapter.data.clear()
                        breakTimeadapter.data.addAll(reversedList)
                        breakTimeadapter.notifyDataSetChanged()
                        isBreakTimeAdded = true
                        setVisibiltyLevel()
                    } else {
                        showToast("No Break time information added!!", requireContext())
                    }
                } ?: showToast("No Break time information added!!", requireContext())
            } else {
                isBreakTimeAdded = false
                setVisibiltyLevel()
            }
        }
        viewModel.liveDataDeleteBreakTime.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                viewModel.GetDriverBreakTimeInfo(userId)
            }
        }

        viewModel.uploadVehicleImageLiveData.observe(viewLifecycleOwner, Observer {
            hideDialog()
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
                    mbinding.vehiclePicturesIB.setImageResource(R.drawable.cross3)
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
//                            mbinding.taskDetails.visibility = View.VISIBLE
                        } else {
                            imagesUploaded = true
                            setVisibiltyLevel()
                            isAllImageUploaded = true
                        }

                        if (it.DaVehicleAddBlueImage != null && it.DaVehImgOilLevelFileName != null && it.DaVehImgFaceMaskFileName != null) {
                            // All images uploaded
                            imageUploadLevel = 3
                            mbinding.ivAddBlueImg.setImageResource(R.drawable.ic_yes)
                            mbinding.ivFaceMask.setImageResource(R.drawable.ic_yes)
                            mbinding.ivOilLevel.setImageResource(R.drawable.ic_yes)
                        } else if (it.DaVehicleAddBlueImage == null && it.DaVehImgFaceMaskFileName == null) {
                            imageUploadLevel = 0
                        } else if (it.DaVehImgFaceMaskFileName != null && it.DaVehicleAddBlueImage == null) {
                            imageUploadLevel = 1
                            mbinding.ivFaceMask.setImageResource(R.drawable.ic_yes)
                        } else if (it.DaVehicleAddBlueImage != null && it.DaVehImgFaceMaskFileName == null) {
                            imageUploadLevel = 0
                            mbinding.ivAddBlueImg.setImageResource(R.drawable.ic_yes)
                        } else if (it.DaVehImgFaceMaskFileName != null && it.DaVehicleAddBlueImage != null) {
                            imageUploadLevel = 2
                            mbinding.ivFaceMask.setImageResource(R.drawable.ic_yes)
                            mbinding.ivAddBlueImg.setImageResource(R.drawable.ic_yes)
                        } else {
                            imageUploadLevel = 0
                        }

                        setProgress()

                        /*else if (it.DaVehImgFaceMaskFileName != null) {
                            imageUploadLevel = 1
                            setProgress()
                            mbinding.ivFaceMask.setImageResource(
                                R.drawable.ic_yes
                            )
                        } else if (it.DaVehicleAddBlueImage != null) {
                            imageUploadLevel = 2
                            setProgress()

                            mbinding.ivAddBlueImg.setImageResource(
                                R.drawable.ic_yes
                            )
                        }*/

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

        viewModel.liveDataDeleteOnRideAlongRouteInfo.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                showDialog()

                viewModel.GetRideAlongDriverInfoByDate(userId)
            }
        }


        val adapter = DriverRouteAdapter(
            GetDriverRouteInfoByDateResponse(),
            showDialog,
            viewModel,
            findNavController(),
            requireContext(),
            Prefs.getInstance(requireContext())
        )

        mbinding.getDriverRouteId.adapter = adapter
        mbinding.getDriverRouteId.layoutManager = LinearLayoutManager(requireContext())

        viewModel.liveDatadriverInfobyRouteDate.observe(viewLifecycleOwner) { it ->
            hideDialog()
            if (it != null) {
                if (it.size > 0) {
                    mbinding.routeNameTV.visibility = View.VISIBLE
                }
                for (item in it) {
                    if (item.RtFinishMileage > 0 && item.RtNoOfParcelsDelivered > 0) {
                        isOnRoadHours = true
                        setVisibiltyLevel()
                        break
                    }
                }
                /*               adapter.list.clear()
                               adapter.list.addAll(it)
                               adapter.notifyDataSetChanged()*/
                adapter.saveData(it)
            } else {
                mbinding.routeNameTV.visibility = View.GONE
                //adapter.list.clear()
                adapter.saveData(GetDriverRouteInfoByDateResponse())
                //adapter.notifyDataSetChanged()
                isOnRoadHours = false
                setVisibiltyLevel()
            }
        }


        viewModel.liveDataDeleteOnRouteDetails.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                showDialog()
                viewModel.GetDriverRouteInfoByDate(userId)
            }
        }
        rideAlongAdapter = RideAlongAdapter(
            RideAlongDriverInfoByDateResponse(),
            findNavController(),
            Prefs.getInstance(requireContext()),
            viewModel,
            showDialog,
            viewLifecycleOwner,
            requireContext()
        )

        mbinding.questionareRv.adapter = rideAlongAdapter
        mbinding.questionareRv.layoutManager = LinearLayoutManager(requireContext())
        /*        rideAlongAdapter.recyclerViewReadyCallback = this@CompleteTaskFragment

                mbinding.questionareRv.viewTreeObserver.addOnGlobalLayoutListener(object :
                    ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        rideAlongAdapter.recyclerViewReadyCallback?.onLayoutReady()
                        mbinding.questionareRv.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })*/

        viewModel.liveDataRideAlongDriverInfoByDateResponse.observe(viewLifecycleOwner) { rideAlongs ->
            hideDialog()
            rideAlongs.let {
                if (it != null) {
                    /*rideAlongAdapter.data.clear()
                    rideAlongAdapter.data.addAll(it)*/
                    rideAlongAdapter.saveData(it)
                    //rideAlongAdapter.notifyDataSetChanged()
                } else {
                    rideAlongAdapter.data.clear()
                    rideAlongAdapter.saveData(RideAlongDriverInfoByDateResponse())
                    //rideAlongAdapter.notifyDataSetChanged()
                }
            }
        }

    }

    private fun checkNull(res: GetVehicleImageUploadInfoResponse): Boolean {
        return res.DaVehImgFaceMaskFileName == null || res.DaVehicleAddBlueImage == null || res.DaVehImgOilLevelFileName == null
    }

    private fun chkTime(edtBreakstart: TextView, edtBreakend: TextView): Boolean {

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
        b1 = false
        b2 = false
        val dialogBinding = TimePickerDialogBinding.inflate(LayoutInflater.from(requireContext()))
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        deleteDialog.setView(dialogBinding.root)

        deleteDialog.setCanceledOnTouchOutside(true)
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        deleteDialog.show()

        dialogBinding.icCrossOrange.setOnClickListener {
            deleteDialog.cancel()
        }

        dialogBinding.edtBreakstart.doAfterTextChanged {
            b1 = true
            if (b1 && b2) {
                dialogBinding.timeTvNext.isEnabled = true
                dialogBinding.timeTvNext.setTextColor(Color.WHITE)
            }
        }
        dialogBinding.edtBreakend.doAfterTextChanged {
            b2 = true
            if (b1 && b2) {
                dialogBinding.timeTvNext.isEnabled = true
                dialogBinding.timeTvNext.setTextColor(Color.WHITE)
            }
        }

        dialogBinding.edtBreakstart.setOnClickListener {
            b1 = false
            showTimePickerDialog(requireContext(), dialogBinding.edtBreakstart)
        }

        dialogBinding.icBreakstart.setOnClickListener {
            b1 = false
            showTimePickerDialog(requireContext(), dialogBinding.edtBreakstart)
            /* if (b1 && b2) {
                 dialogBinding.timeTvNext.isEnabled = true
                 dialogBinding.timeTvNext.setTextColor(Color.WHITE)
             }*/
        }

        dialogBinding.edtBreakend.setOnClickListener {
            //  b2 = true
            b2 = false
            showTimePickerDialog(requireContext(), dialogBinding.edtBreakend)

        }

        dialogBinding.icBreakend.setOnClickListener {
            b2 = false
            /*b2 = true
            if (b1 && b2) {
                dialogBinding.timeTvNext.isEnabled = true
                dialogBinding.timeTvNext.setTextColor(Color.WHITE)
            }*/
            showTimePickerDialog(requireContext(), dialogBinding.edtBreakend)
        }

        /*     val textWatcher = object : TextWatcher {
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
             dialogBinding.edtBreakend.addTextChangedListener(textWatcher)*/


        dialogBinding.timeTvNext.setOnClickListener {
            if (chkTime(dialogBinding.edtBreakstart, dialogBinding.edtBreakend)) {
                breakStartTime = dialogBinding.edtBreakstart.text.toString()
                breakEndTime = dialogBinding.edtBreakend.text.toString()
                deleteDialog.cancel()
                sendBreakTimeData()
            } else {
                showErrorDialog(fragmentManager, "CTF-02", "Please add valid time information")
            }
        }

    }

    private fun sendBreakTimeData() {
        breakTimeSent = true
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
        /*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
                }*/

        ivX = iv
        if (allPermissionsGranted()) {
            showPictureDialog(iv, codes)
        } else {
            requestpermissions()
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

    fun clientUniqueID(): String {
        val x = Prefs.getInstance(App.instance).clebUserId.toString()
        val y = Prefs.getInstance(App.instance).scannedVmRegNo
        // example string
        val currentDate = LocalDateTime.now()
        val formattedDate = currentDate.format(DateTimeFormatter.ofPattern("ddHHmmss"))

        regexPattern = Regex("${x.take(3)}${y.take(3)}${formattedDate}")
        inspectionID = regexPattern.toString()
        return regexPattern.toString()
        Log.e("resistrationvrnpatterhn", "clientUniqueID: " + inspectionID)
    }

    /*private fun startInspection() {
        if (isAllImageUploaded) {
            mbinding.tvNext.visibility = View.VISIBLE
        }

        //if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
        showDialog()

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
                        hideDialog()
                        Log.e("messsagesss", "startInspection: " + msg + code)
                        if (isStarted) {
//
                        } else {
//
                        }
                        if (msg == "Success") {


                        }
                        if (!isStarted) {
                            Log.e("startedinspection", "onCreateView: " + msg + isStarted)


                        }
                    })
            } catch (_: Exception) {

                showErrorDialog(fragmentManager, "CTF-02", "Please try again later!!")
            }
        }
        *//*        } else {
                    showErrorDialog(
                        fragmentManager,
                        "CTF-1",
                        "We are currently updating our app for Android 13+ devices. Please try again later."
                    )
                }*//*

    }*/

    private fun startInspection() {
//        if (isAllImageUploaded) {
//            mbinding.tvNext.visibility = View.VISIBLE
//        }

//      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
        loadingDialog.show()

        if (cqSDKInitializer.isCQSDKInitialized()) {

            var vmReg = Prefs.getInstance(App.instance).scannedVmRegNo ?: ""
            Log.e(
                "totyototyotoytroitroi",
                "startInspection: " + inspectionID + "VmReg ${Prefs.getInstance(App.instance).vmRegNo}"
            )
            if (vmReg.isEmpty()) {
                vmReg = Prefs.getInstance(App.instance).vmRegNo
            }
            Log.e("sdkskdkdkskdkskd", "onCreateView: ")

            try {
                cqSDKInitializer.startInspection(activityContext = requireActivity(),
                    clientAttrs = ClientAttrs(
                        userName = " ",
                        dealer = " ",
                        dealerIdentifier = " ",
                        client_unique_id = inspectionID
                        //drivers ID +vechile iD + TOdays date dd// mm //yy::tt,mm
                    ),
                    inputDetails = InputDetails(
                        vehicleDetails = VehicleDetails(
                            regNumber = vmReg.replace(
                                " ",
                                ""
                            ), //if sent, user can't edit
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
                    result = { isStarted, msg, code ->
                        Log.e("inspectionIDsssssssss", "startInspection: " + inspectionID)
                        Log.e("messsagesss", "startInspection: " + msg + code)
                        if (isStarted) {
                            Prefs.getInstance(App.instance).inspectionID = inspectionID
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
                BreakTimeTable,
                taskDetails,
                view2
            ).forEach { thisView -> thisView.visibility = View.GONE }
        }
        when (visibilityLevel) {
            -1 -> {
                mbinding.uploadLayouts.visibility = View.VISIBLE
                mbinding.imageUploadView.visibility = View.GONE

                /*mbinding.clFaceMask.visibility = View.GONE
                mbinding.clOilLevel.visibility = View.GONE*/
                /*         mbinding.vehiclePicturesIB.setImageResource(R.drawable.ic_cross)
                         mbinding.uploadLayouts.visibility = View.VISIBLE
                         mbinding.taskDetails.visibility = View.VISIBLE
                         mbinding.imageUploadView.visibility = View.VISIBLE*/
                // mbinding.vehiclePicturesIB.setImageResource(R.drawable.check1)
            }

            0 -> {
                mbinding.uploadLayouts.visibility = View.VISIBLE
//                mbinding.taskDetails.visibility = View.VISIBLE
                mbinding.imageUploadView.visibility = View.VISIBLE
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.not_uploaded)
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.shape_expand_main
                    )
                );
            }

            1 -> {
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.frame__2_)
                mbinding.rlcomtwoClock.visibility = View.VISIBLE
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_complete_task_done
                    )
                );
            }

            2 -> {
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.frame__2_)
                mbinding.onRoadView.visibility = View.VISIBLE
                mbinding.rlcomtwoBreak.visibility = View.VISIBLE
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_complete_task_done
                    )
                );
            }

            3 -> {
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.frame__2_)
                mbinding.onRoadView.visibility = View.VISIBLE
                mbinding.BreakTimeTable.visibility = View.VISIBLE
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_complete_task_done
                    )
                );
            }

            4 -> {
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.frame__2_)
                mbinding.onRoadView.visibility = View.VISIBLE
                mbinding.rlcomtwoBreak.visibility = View.VISIBLE
                mbinding.rlcomtwoClockOut.visibility = View.VISIBLE
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_complete_task_done
                    )
                );
            }

            5 -> {
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.frame__2_)
                mbinding.rlcomtwoClockOut.visibility = View.VISIBLE
                mbinding.onRoadView.visibility = View.VISIBLE
                mbinding.BreakTimeTable.visibility = View.VISIBLE
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_complete_task_done
                    )
                );
            }

        }
    }

    private fun setVisibiltyLevel() {
        visibilityLevel = 0
        if (!inspectionstarted) {
            visibilityLevel = -1
            visibiltyControlls()
            return
        }
        if (!imagesUploaded) {
            visibilityLevel = 0
            visibiltyControlls()
            return
        } else {
            visibilityLevel = 1
        }
        if (isClockedIn) {
            visibilityLevel = 2
        }

        if (isBreakTimeAdded && isOnRoadHours) {
            visibilityLevel = 5
            visibiltyControlls()
            return
        }

        if (isBreakTimeAdded)
            visibilityLevel = 3

        if (isOnRoadHours)
            visibilityLevel = 4

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

    private fun viewGoneAnimator(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
    }

    private fun viewVisibleAnimator(view: View) {
        view.animate()
            .alpha(1f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.VISIBLE
                }
            })
    }

}
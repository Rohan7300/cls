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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.R.style.BottomSheetDialogTheme
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.ViewModel.OSyncViewModel
import com.clebs.celerity.adapters.BreakTimeAdapter
import com.clebs.celerity.adapters.DriverRouteAdapter
import com.clebs.celerity.adapters.RideAlongAdapter
import com.clebs.celerity.database.OfflineSyncEntity
import com.clebs.celerity.databinding.FragmentCompleteTaskBinding
import com.clebs.celerity.databinding.FragmentNewCompleteTaskBinding
import com.clebs.celerity.databinding.TimePickerDialogBinding
import com.clebs.celerity.models.requests.SaveBreakTimeRequest
import com.clebs.celerity.models.response.GetDriverBreakTimeInfoResponse
import com.clebs.celerity.models.response.GetDriverRouteInfoByDateResponse
import com.clebs.celerity.models.response.GetVehicleImageUploadInfoResponse
import com.clebs.celerity.models.response.RideAlongDriverInfoByDateResponse
import com.clebs.celerity.ui.AddInspection
import com.clebs.celerity.ui.App
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.ui.HomeActivity.Companion.checked
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.ui.AddInspectionActivity2
import com.clebs.celerity.ui.FaceScanActivity
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.currentUri
import com.clebs.celerity.utils.DependencyProvider.osData
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.addLeadingZeroIfNeeded
import com.clebs.celerity.utils.bitmapToBase64
import com.clebs.celerity.utils.getCameraURI
import com.clebs.celerity.utils.getCurrentDateTime
import com.clebs.celerity.utils.getLoc
import com.clebs.celerity.utils.getVRegNo
import com.clebs.celerity.utils.logOSEntity
import com.clebs.celerity.utils.navigateTo
import com.clebs.celerity.utils.showErrorDialog
import com.clebs.celerity.utils.showTimePickerDialog
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.startUploadWithWorkManager
import com.clebs.celerity.utils.toRequestBody
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tapadoo.alerter.Alerter
import io.clearquote.assessment.cq_sdk.CQSDKInitializer
import io.clearquote.assessment.cq_sdk.datasources.remote.network.datamodels.createQuoteApi.payload.ClientAttrs
import io.clearquote.assessment.cq_sdk.models.CustomerDetails
import io.clearquote.assessment.cq_sdk.models.InputDetails
import io.clearquote.assessment.cq_sdk.models.VehicleDetails
import okhttp3.MultipartBody
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.TimerTask
import java.util.UUID


class NewCompleteTaskFragment : Fragment() {
    lateinit var mbinding: FragmentNewCompleteTaskBinding
    private var isclicked: Boolean = true
    private var isclickedtwo: Boolean = true
    private lateinit var viewModel: MainViewModel
    private lateinit var imageView: ImageView
    private var clebUserID: Int = 0
    private lateinit var regexPattern: Regex
    private lateinit var inspectionID: String
    private var clockedouttime = String()
    private var isInspectionDoneToday: Boolean = false
    lateinit var ivX: ImageView
    private var codeX = 0
    private var uploadInProgress = false
    private var requestCode: Int = 0

    private var isInspectionDone: Boolean = false
    private var imagesUploaded: Boolean = false
    private lateinit var rideAlongAdapter: RideAlongAdapter
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
    var clockedinTime = String()
    private lateinit var cqSDKInitializer: CQSDKInitializer
    private lateinit var fragmentManager: FragmentManager
    private lateinit var oSyncViewModel: OSyncViewModel
    val showDialog: () -> Unit = {
        (activity as HomeActivity).showDialog()
    }
    val hideDialog: () -> Unit = {
        (activity as HomeActivity).hideDialog()
    }
    var inspectionOfflineImagesCHeck: Boolean? = null
    private var inspectionstarted: Boolean = false
    lateinit var prefs: Prefs

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
            mbinding = FragmentNewCompleteTaskBinding.inflate(inflater, container, false)
        }
        val clickListener = View.OnClickListener {
            showAlert()
        }
        val currentDate =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(
                Date()
            )

        val animFadein: Animation =
            AnimationUtils.loadAnimation(context, R.anim.left2right)
        mbinding.mainCompleteTask.animation = animFadein

        loadingDialog = (activity as HomeActivity).loadingDialog
        oSyncViewModel = (activity as HomeActivity).oSyncViewModel
        prefs = Prefs.getInstance(requireContext())
        clebUserID = Prefs.getInstance(requireContext()).clebUserId.toInt()
        (activity as HomeActivity).ActivityHomeBinding.title.text = ""
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
        isInspectionDoneToday = Prefs.getInstance(requireContext()).isInspectionDoneToday()

        mbinding.ivFaceMask.setImageResource(R.drawable.camera_ivs)
        Prefs.getInstance(requireContext()).clearNavigationHistory()
        fragmentManager = (activity as HomeActivity).fragmentManager
        cqSDKInitializer = CQSDKInitializer(requireContext())
        cqSDKInitializer.triggerOfflineSync()

        inspectionstarted = Prefs.getInstance(requireContext()).isInspectionDoneToday()
        viewModel = (activity as HomeActivity).viewModel
        Log.d("DependencyProvider", DependencyProvider.isComingBackFromFaceScan.toString())
        oSyncViewModel.osData.observe(viewLifecycleOwner) {
            osData = it!!
            showDialog()
            if (it.isIni) {
                hideDialog()
                osData!!.isDefectSheetFilled = true
                // inspectionstarted = osData.isInspectionDoneToday
                logOSEntity("OSData CompleteTaskFragment", osData!!)
                showDialog()
                viewModel.GetVehicleImageUploadInfo(
                    Prefs.getInstance(requireContext()).clebUserId.toInt(),
                    Prefs.getInstance(requireContext()).baseVmID.toInt(),
                    getCurrentDateTime()
                )
                showDialog()
                if (DependencyProvider.isComingBackFromFaceScan) {
                    sendFaceMask()
                }
                viewModel.GetDriverBreakTimeInfo(clebUserID)
                showDialog()
                viewModel.GetDailyWorkInfoById(clebUserID)
                showDialog()
                viewModel.GetDriverRouteInfoByDate(clebUserID)
                showDialog()
                viewModel.GetRideAlongDriverInfoByDate(clebUserID)
                (activity as HomeActivity).GetDriversBasicInformation()
                showDialog()

                viewModel.GetVehicleInfobyDriverId(
                    Prefs.getInstance(App.instance).clebUserId.toInt(),
                    currentDate
                )
                observers()
            }
        }

        viewModel.setLastVisitedScreenId(requireActivity(), R.id.newCompleteTaskFragment)

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
        }

        clientUniqueID()

        mbinding.rlcomtwoClock.setOnClickListener {
            showDialog()
            viewModel.UpdateClockInTime(clebUserID)
        }

        mbinding.rlcomtwoClockOut.setOnClickListener {
            showDialog()
            viewModel.UpdateClockOutTime(clebUserID)
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
            val intent = Intent(requireContext(), AddInspectionActivity2::class.java)
            startActivity(intent)
        }
        mbinding.ivOilLevel.setOnClickListener {
            requestCode = 5
            pictureDialogBase64(mbinding.ivOilLevel, requestCode)
        }
        mbinding.clOilLevel.setOnClickListener {
            requestCode = 5
            pictureDialogBase64(mbinding.ivOilLevel, requestCode)
        }
        mbinding.ivAddBlueImg.setOnClickListener {
            requestCode = 7
            pictureDialogBase64(mbinding.ivAddBlueImg, requestCode)
        }
        mbinding.clAddBlueImg.setOnClickListener {
            requestCode = 7
            pictureDialogBase64(mbinding.ivAddBlueImg, requestCode)
        }

        mbinding.AddRoute.setOnClickListener {
            Prefs.getInstance(requireContext()).saveDriverRouteInfoByDate(null)
            navigateTo(R.id.onRoadHoursFragment, requireContext(), findNavController())
        }

        mbinding.rlcom.setOnClickListener {
            if (isclicked) {
                showTaskBottomSheet()
//                mbinding.taskDetails.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.green_down_arrow)
//                mbinding.view2.visibility = View.VISIBLE
            } else {
                showTaskBottomSheet()
                mbinding.downIv.setImageResource(R.drawable.green_down_arrow)
//                mbinding.taskDetails.visibility = View.GONE
//                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
//                mbinding.view2.visibility = View.GONE
            }
            isclicked = !isclicked
        }
        mbinding.downIv.setOnClickListener {
            if (isclickedtwo) {
                showTaskBottomSheet()
//                mbinding.taskDetails.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.green_down_arrow)

//                mbinding.view2.visibility = View.VISIBLE
            } else {
                showTaskBottomSheet()
                mbinding.downIv.setImageResource(R.drawable.green_down_arrow)
//                showTaskBottomSheet()
//                mbinding.taskDetails.visibility = View.GONE
//                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
//                mbinding.view2.visibility = View.GONE
            }
            isclickedtwo = !isclickedtwo
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
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
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
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
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

        viewModel.livedataGetVehicleInfobyDriverId.observe(viewLifecycleOwner) {
            if (it != null) {
                scannedvrn = it.vmRegNo
                Prefs.getInstance(App.instance).scannedVmRegNo = it.vmRegNo
                if (Prefs.getInstance(App.instance).vmId==0&&it.vmId!=null) {
                    Prefs.getInstance(App.instance).vmId = it.vmId.toString().toInt()
                }
            }
        }

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
                viewModel.GetDriverBreakTimeInfo(clebUserID)
            } else {

            }
        }

        viewModel.livedataDailyWorkInfoByIdResponse.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                if (it.ClockedInTime != null) {
                    clockedinTime = it.ClockedInTime.toString()
                    mbinding.tvClockedIN.text = it.ClockedInTime.toString()
                    isClockedIn = true
                    osData.isClockedInToday = true

                } else {
                    isClockedIn = false
                }

                if (it.ClockedOutTime != null) {
                    mbinding.clockOutMark.setImageResource(R.drawable.finalclockout)
                    mbinding.rlcomtwoClockOut.isEnabled = false
                    mbinding.clockOutTV.text = "Clocked Out"
                    mbinding.rlcomtwoClockOut.isClickable = false
                    mbinding.clockedOutTime.text = it.ClockedOutTime.toString()
                    clockedouttime = it.ClockedOutTime.toString()
                }
            } else {

            }
            setVisibiltyLevel()
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
            viewModel.GetDailyWorkInfoById(clebUserID)
            showDialog()
            if (it != null) {
                osData.isClockedInToday = true
            } else {
                if (uploadInProgress) {
                    showToast("Syncing!! Pls wait", requireContext())
                } else {
                    showToast("Pls Retry!!", requireContext())
                }
            }
            setVisibiltyLevel()
        }

        viewModel.livedataUpdateClockOutTime.observe(viewLifecycleOwner) {
            hideDialog()
            viewModel.GetDailyWorkInfoById(clebUserID)
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
                    try {
                        if (breakTimeStart.isNotEmpty() && breakTimeEnd.isNotEmpty()) {
                            val reversedList = it.reversed()
                            breakTimeadapter.data.clear()
                            breakTimeadapter.data.addAll(reversedList)
                            breakTimeadapter.notifyDataSetChanged()
                            isBreakTimeAdded = true
                            setVisibiltyLevel()
                        } else {
                        }
                    } catch (_: Exception) {
                    }

                }
            } else {
                isBreakTimeAdded = false
                setVisibiltyLevel()
            }
        }

        viewModel.liveDataDeleteBreakTime.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                viewModel.GetDriverBreakTimeInfo(clebUserID)
            }
        }

        viewModel.uploadVehicleImageLiveData.observe(viewLifecycleOwner, Observer {
            hideDialog()
            if (it != null) {
                if (it.Status == "200") {
                    //    showDialog()
                    //   viewModel.GetVehicleImageUploadInfo(clebUserID, getCurrentDateTime())
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
                if (it.DaVehImgFaceMaskFileName != null &&
                    it.DaVehImgOilLevelFileName != null
                ) {

                    prefs.addBlueRequired = false
                    prefs.oilLevelRequired = false
                    prefs.faceMaskRequired = false
                    prefs.updateFaceMaskStatus(true)
                    prefs.faceMaskUri = null
                    prefs.oilLevelUri = null
                    if (it.IsAdBlueRequired == true && it.DaVehicleAddBlueImage == null) {
                        if (prefs.addBlueUri == null) {
                            prefs.addBlueRequired = true
                        } else {
                            prefs.addBlueRequired = false
                            backgroundImageSync()
                        }
                    } else {
                        prefs.addBlueRequired = false

                    }
                } else {
                    if (prefs.faceMaskUri == null ||
                        prefs.oilLevelUri == null
                    ) {
                        mbinding.vehiclePicturesIB.setImageResource(R.drawable.cross3)

                        if (it.DaVehImgOilLevelFileName != null) {
                            prefs.oilLevelRequired = false
                        } else if (prefs.oilLevelUri == null) {
                            prefs.oilLevelRequired = true
                        }else{
                            prefs.oilLevelRequired = false
                        }

                        if (it.IsAdBlueRequired == true) {
                            if (it.DaVehicleAddBlueImage != null)
                                prefs.addBlueRequired = false
                            else {
                                prefs.addBlueRequired = prefs.addBlueUri == null
                            }
                        } else
                            prefs.addBlueRequired = false

                        if (prefs.faceMaskUri != null || it.DaVehImgFaceMaskFileName != null) {
                            prefs.updateFaceMaskStatus(true)
                        } else {
                            prefs.updateFaceMaskStatus(false)
                        }
                    } else {
                        backgroundImageSync()
                    }
                }
            } else {
                if (prefs.addBlueUri != null || prefs.oilLevelUri != null || prefs.faceMaskUri != null)
                    backgroundImageSync()
            }
            setVisibiltyLevel()
        })

        viewModel.liveDataDeleteOnRideAlongRouteInfo.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                showDialog()
                viewModel.GetRideAlongDriverInfoByDate(clebUserID)
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
                        break
                    }
                }
                adapter.saveData(it)
            } else {
                mbinding.routeNameTV.visibility = View.GONE
                adapter.saveData(GetDriverRouteInfoByDateResponse())
                isOnRoadHours = false
            }
            setVisibiltyLevel()
        }


        viewModel.liveDataDeleteOnRouteDetails.observe(viewLifecycleOwner) {
            hideDialog()
            if (it != null) {
                showDialog()
                viewModel.GetDriverRouteInfoByDate(clebUserID)
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

        viewModel.liveDataRideAlongDriverInfoByDateResponse.observe(viewLifecycleOwner) { rideAlongs ->
            hideDialog()
            rideAlongs.let {
                if (it != null) {
                    rideAlongAdapter.saveData(it)
                } else {
                    rideAlongAdapter.data.clear()
                    rideAlongAdapter.saveData(RideAlongDriverInfoByDateResponse())
                }
            }
        }

    }

    private fun backgroundImageSync() {
        uploadInProgress = true

        startUploadWithWorkManager(
            0,
            Prefs.getInstance(requireContext()),
            requireContext()
        )
        inspectionstarted = true
        if (osData.faceMaskImage != null) {
            osData.isImagesUploadedToday = true
            imagesUploaded = true
        }

        setVisibiltyLevel()
    }


    private fun chkTime(edtBreakstart: TextView, edtBreakend: TextView): Boolean {
        val startTime = edtBreakstart.text.toString()
        val endTime = edtBreakend.text.toString()
        print("starttime $startTime")
        print("endtime $endTime")

        // Use 12-hour format with AM/PM
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return try {
            val start = sdf.parse(startTime)
            val end = sdf.parse(endTime)

            if (start != null && end != null) {
                start.before(end)
            } else {
                false
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            false
        }
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
        }

        dialogBinding.edtBreakend.setOnClickListener {
            b2 = false
            showTimePickerDialog(requireContext(), dialogBinding.edtBreakend)
        }

        dialogBinding.icBreakend.setOnClickListener {
            b2 = false
            showTimePickerDialog(requireContext(), dialogBinding.edtBreakend)
        }

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
                UserId = clebUserID.toString(),
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
        //sendFaceMask()
        /* val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
         startActivityForResult(intent, codes)*/
        startActivity(Intent(requireContext(), FaceScanActivity::class.java))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
            osData.faceMaskImage = bitmapToBase64(imageBitmap)
            osData.isImagesUploadedToday = true
            imagesUploaded = true
            oSyncViewModel.insertData(osData)
            //sendImage(imageBitmap, requestCode)
        } else {
            showToast("Failed to fetch image content", requireContext())
        }
    }

    private fun sendFaceMask() {
        Log.d("CurrentURI", " $currentUri")
        if (currentUri != null) {
            if (!osData.isImagesUploadedToday || osData.faceMaskImage == null) {
                osData.faceMaskImage = currentUri.toString()
                osData.isImagesUploadedToday = true
                oSyncViewModel.insertData(osData)
            }
            prefs.faceMaskUri = currentUri.toString()
            prefs.faceMaskRequired = false
            prefs.updateFaceMaskStatus(true)
            imagesUploaded = true
            print("OSData ISImage1 ${osData.isImagesUploadedToday}\n")
            print("OSData ISInspection1 ${osData.isInspectionDoneToday}\n")
            visibilityLevel = 1
            startUploadWithWorkManager(1, Prefs.getInstance(requireContext()), requireContext())
            setVisibiltyLevel()
            DependencyProvider.isComingBackFromFaceScan = false
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

        osData.faceMaskImage = bitmapToBase64(imageBitmap)
        osData.isImagesUploadedToday = true
        oSyncViewModel.insertData(osData)
        imagesUploaded = true
        print("OSData ISImage1 ${osData.isImagesUploadedToday}\n")
        print("OSData ISInspection1 ${osData.isInspectionDoneToday}\n")
        visibilityLevel = 1
        startUploadWithWorkManager(1, Prefs.getInstance(requireContext()), requireContext())
        setVisibiltyLevel()
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


    private fun visibiltyControlls() {
        with(mbinding) {
            listOf(
                uploadLayouts,
                startinspection,
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
                mbinding.startinspection.visibility = View.VISIBLE
                mbinding.imageUploadView.visibility = View.GONE
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.cross3)
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
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_complete_task_done
                    )
                );
                //mbinding.rlcomtwoBreak.visibility = View.VISIBLE
            }

            2 -> {
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.frame__2_)
                mbinding.onRoadView.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
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
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
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
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
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
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
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
        isClockedIn = osData.isClockedInToday
        inspectionstarted =
            prefs.isInspectionDoneToday() && !prefs.oilLevelRequired && !prefs.addBlueRequired
        imagesUploaded = prefs.isFaceMaskAddedToday()
        print("OSData ISImage $imagesUploaded\n")
        print("OSData ISInspection $inspectionstarted\n")
        if (!inspectionstarted) {
            visibilityLevel = -1
            visibiltyControlls()
            oSyncViewModel.insertData(osData)
            return
        }
        if (!imagesUploaded) {
            visibilityLevel = 0
            visibiltyControlls()
            oSyncViewModel.insertData(osData)
            return
        } else {
            visibilityLevel = 1
        }
        if (isClockedIn) {
            visibilityLevel = 2
        }
        if (isBreakTimeAdded && isOnRoadHours && isClockedIn) {
            visibilityLevel = 5
            visibiltyControlls()
            oSyncViewModel.insertData(osData)
            return
        }

        if (isClockedIn && isBreakTimeAdded)
            visibilityLevel = 3

        if (isOnRoadHours && isClockedIn)
            visibilityLevel = 4

        visibiltyControlls()
        oSyncViewModel.insertData(osData)
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

    fun showTaskBottomSheet() {
        val bottomSheetDialog =
            BottomSheetDialog(requireContext(), BottomSheetDialogTheme)
        val showSheet = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.bottom_sheet_dialog,
                requireActivity().findViewById<View>(R.id.bottomSheetRealCL) as ConstraintLayout?
            )
        val cancelIV = showSheet.findViewById<ImageView>(R.id.cancleIV)
        val tvclockedin = showSheet.findViewById<TextView>(R.id.tv_ClockedIN)
        val tvclockedout = showSheet.findViewById<TextView>(R.id.clocked_outTime)
        if (!clockedinTime.isNullOrEmpty()) {
            tvclockedin.setText(clockedinTime)
        } else {
            tvclockedin.setText("- - - -")
        }
        if (!clockedouttime.isNullOrEmpty()) {
            tvclockedout.setText(clockedouttime)
        } else {
            tvclockedout.setText("- - - -")
        }
        cancelIV.setOnClickListener {
            bottomSheetDialog.dismiss()

        }

        val imageView = showSheet.findViewById<ImageView>(R.id.frame)
        when (visibilityLevel) {
            -1 -> {
                imageView.setImageResource(R.drawable.cross3)
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
                imageView.setImageResource(R.drawable.not_uploaded)
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.shape_expand_main
                    )
                );
            }

            1 -> {
                imageView.setImageResource(R.drawable.frame__2_)
                mbinding.rlcomtwoClock.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_complete_task_done
                    )
                );
                //mbinding.rlcomtwoBreak.visibility = View.VISIBLE
            }

            2 -> {
                imageView.setImageResource(R.drawable.frame__2_)
                mbinding.onRoadView.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
                mbinding.rlcomtwoBreak.visibility = View.VISIBLE
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_complete_task_done
                    )
                );
            }

            3 -> {
                imageView.setImageResource(R.drawable.frame__2_)
                mbinding.onRoadView.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
                mbinding.BreakTimeTable.visibility = View.VISIBLE
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_complete_task_done
                    )
                );
            }

            4 -> {
                imageView.setImageResource(R.drawable.frame__2_)
                mbinding.onRoadView.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
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
                imageView.setImageResource(R.drawable.frame__2_)
                mbinding.rlcomtwoClockOut.visibility = View.VISIBLE
                mbinding.onRoadView.visibility = View.VISIBLE
                mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
                mbinding.BreakTimeTable.visibility = View.VISIBLE
                mbinding.complete.setBackground(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.background_complete_task_done
                    )
                );
            }

        }
        bottomSheetDialog.setContentView(showSheet)
        bottomSheetDialog.show()
        bottomSheetDialog.setOnDismissListener {
            mbinding.downIv.setImageResource(R.drawable.grey_right_arrow)
        }
    }


}
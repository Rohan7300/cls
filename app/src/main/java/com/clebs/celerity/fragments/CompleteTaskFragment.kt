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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.FragmentCompleteTaskBinding
import com.clebs.celerity.databinding.TimePickerDialogBinding
import com.clebs.celerity.models.requests.SaveBreakTimeRequest
import com.clebs.celerity.models.response.GetVehicleImageUploadInfoResponse
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.HomeActivity.Companion.checked
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.navigateTo
import com.clebs.celerity.utils.progressBarVisibility
import com.clebs.celerity.utils.showTimePickerDialog
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.toRequestBody
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import okhttp3.MultipartBody
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

class CompleteTaskFragment : Fragment() {
    lateinit var mbinding: FragmentCompleteTaskBinding
    private var isclicked: Boolean = true
    private var isclickedtwo: Boolean = true
    private lateinit var viewModel: MainViewModel
    private lateinit var imageView: ImageView
    private var userId: Int = 0
    private var requestCode: Int = 0
    private var showImageUploadLayout: Boolean = false
    var breakStartTime: String = ""
    var breakEndTime: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentCompleteTaskBinding.inflate(inflater, container, false)
        }
        val clickListener = View.OnClickListener {
            showAlert()
        }
        userId = Prefs.getInstance(requireContext()).userID.toInt()
        mbinding.rlcomtwoBreak.setOnClickListener(clickListener)
        mbinding.downIvsBreak.setOnClickListener(clickListener)
        mbinding.parentBreak.setOnClickListener(clickListener)

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        viewModel =
            ViewModelProvider(this, MyViewModelFactory(mainRepo)).get(MainViewModel::class.java)
        //    viewModel.setLastVisitedScreenId(requireContext(), R.id.completeTaskFragment)

        viewModel.livedataSaveBreakTime.observe(viewLifecycleOwner){
            if(it!=null){
              //  deleteDialog.cancel()
            }else{
                showToast("Something went wrong!!",requireContext())
            }
            progressBarVisibility(
                false,
                mbinding.completeTaskFragmentPB,
                mbinding.overlayViewCompleteTask
            )

        }
        mbinding.icUu.setOnClickListener {

            findNavController().navigate(R.id.profileFragment)
        }
        if (checked.equals("0")) {
            //findNavController().navigate(R.id.vechileMileageFragment)
            navigateTo(R.id.vechileMileageFragment, requireContext(), findNavController())
        }

        viewModel.vehicleImageUploadInfoLiveData.observe(viewLifecycleOwner, Observer {
            println(it)
            if (it!!.Status == "404") {
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.ic_cross)
                showImageUploadLayout = true
            } else {
                if (it.IsVehicleImageUploaded == false) {
                    showImageUploadLayout = true
                    mbinding.vehiclePicturesIB.setImageResource(R.drawable.ic_cross)
                } else {

                    showImageUploadLayout = checkNull(it)

                    if (it.DaVehImgDashBoardFileName != null)
                        mbinding.ivVehicleDashboard.setImageResource(R.drawable.ic_yes)

                    if (it.DaVehImgFaceMaskFileName != null)
                        mbinding.ivFaceMask.setImageResource(R.drawable.ic_yes)

                    if (it.DaVehImgRearFileName != null)
                        mbinding.ivRearImgUp.setImageResource(R.drawable.ic_yes)

                    if (it.DaVehImgFrontFileName != null)
                        mbinding.ivFront.setImageResource(R.drawable.ic_yes)

                    if (it.DaVehImgNearSideFileName != null)
                        mbinding.ivNearSide.setImageResource(R.drawable.ic_yes)

                    if (it.DaVehImgOffSideFileName != null)
                        mbinding.ivOffSideImgUp.setImageResource(R.drawable.ic_yes)

                    if (it.DaVehicleAddBlueImage != null)
                        mbinding.ivAddBlueImg.setImageResource(R.drawable.ic_yes)

                    if (it.DaVehImgOilLevelFileName != null)
                        mbinding.ivOilLevel.setImageResource(R.drawable.ic_yes)

                }
            }


            if (!showImageUploadLayout) {
                mbinding.uploadLayouts.visibility = View.GONE
            } else {
                mbinding.uploadLayouts.visibility = View.VISIBLE
            }
        })


        viewModel.GetVehicleImageUploadInfo(Prefs.getInstance(requireContext()).userID.toInt())

        viewModel.uploadVehicleImageLiveData.observe(viewLifecycleOwner, Observer {
            progressBarVisibility(
                false,
                mbinding.completeTaskFragmentPB,
                mbinding.overlayViewCompleteTask
            )
            if (it != null) {
                if (it.Status == "200") {
                    setImageUploadViews(requestCode, 1)
                } else {
                    setImageUploadViews(requestCode, 0)
                }
            } else {
                setImageUploadViews(requestCode, 0)
            }
        })

        mbinding.clFaceMask.setOnClickListener {
            requestCode = 0
            pictureDialogBase64(mbinding.ivFaceMask, requestCode)
        }
        mbinding.clVehicleDashboard.setOnClickListener {
            requestCode = 1
            pictureDialogBase64(mbinding.ivVehicleDashboard, requestCode)
        }
        mbinding.clFront.setOnClickListener {
            requestCode = 2
            pictureDialogBase64(mbinding.ivFront, requestCode)
        }
        mbinding.clNearSide.setOnClickListener {
            requestCode = 3
            pictureDialogBase64(mbinding.ivNearSide, requestCode)
        }
        mbinding.clRearImgUp.setOnClickListener {
            requestCode = 4
            pictureDialogBase64(mbinding.ivRearImgUp, requestCode)
        }
        mbinding.clOilLevel.setOnClickListener {
            requestCode = 5
            pictureDialogBase64(mbinding.ivOilLevel, requestCode)
        }
        mbinding.clOffSideImgUp.setOnClickListener {
            requestCode = 6
            pictureDialogBase64(mbinding.ivOffSideImgUp, requestCode)
        }
        mbinding.clAddBlueImg.setOnClickListener {
            requestCode = 7
            pictureDialogBase64(mbinding.ivAddBlueImg, requestCode)
        }

        mbinding.AddRoute.setOnClickListener {
            findNavController().navigate(R.id.onRoadHoursFragment)
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
                mbinding.uploadLayouts.visibility = View.VISIBLE
            }
            isclicked = !isclicked
        }
        mbinding.run {
            mbinding.tvNext.isEnabled = !isclicked
            if (tvNext.isEnabled) {
                tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            } else {
                tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
            }
        }
        mbinding.tvNext.setOnClickListener {
            if (isclickedtwo) {

                mbinding.uploadLayouts.visibility = View.GONE
            } else {

                mbinding.uploadLayouts.visibility = View.VISIBLE
            }
            isclickedtwo = !isclickedtwo
        }
        mbinding.taskDetails.getViewTreeObserver()
            .addOnGlobalLayoutListener(OnGlobalLayoutListener { // Check if the view is currently visible or gone
                val isVisible = mbinding.taskDetails.visibility == View.VISIBLE

                // Apply animation based on the visibility
                if (isVisible) {
                    val slideInAnimation: Animation =
                        AnimationUtils.loadAnimation(context, com.clebs.celerity.R.anim.slide_down)
                    mbinding.taskDetails.startAnimation(slideInAnimation)
                } else {
                    val slideOutAnimation: Animation =
                        AnimationUtils.loadAnimation(context, com.clebs.celerity.R.anim.slide_up)
                    mbinding.taskDetails.startAnimation(slideOutAnimation)
                }
            })
        mbinding.rlcomtwoRoad.setOnClickListener {

            mbinding.routeLayout.visibility = View.VISIBLE
        }
        return mbinding.root
    }

    private fun checkNull(res: GetVehicleImageUploadInfoResponse): Boolean {
        return res.DaVehImgDashBoardFileName == null ||
                res.DaVehImgFaceMaskFileName == null ||
                res.DaVehImgRearFileName == null ||
                res.DaVehImgFrontFileName == null ||
                res.DaVehImgNearSideFileName == null ||
                res.DaVehImgOffSideFileName == null ||
                res.DaVehicleAddBlueImage == null ||
                res.DaVehImgOilLevelFileName == null
    }

    private fun chkTime(edtBreakstart: EditText, edtBreakend: EditText): Boolean {

        val startTime = edtBreakstart.text.toString()
        val endTime = edtBreakend.text.toString()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val start = sdf.parse(startTime)
        val end = sdf.parse(endTime)
        if (start != null) {
            if (start.before(end))
                return true
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
                progressBarVisibility(
                    true,
                    mbinding.completeTaskFragmentPB,
                    mbinding.overlayViewCompleteTask
                )
                sendBreakTimeData()
            } else {
                showToast("Please add valid time information", requireContext())
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
        progressBarVisibility(
            true,
            mbinding.completeTaskFragmentPB,
            mbinding.overlayViewCompleteTask
        )
        val uniqueFileName = "image_${UUID.randomUUID()}.jpg"
        val requestBody = imageBitmap.toRequestBody()
        val imagePart = when (requestCode) {
            0 -> {
                MultipartBody.Part.createFormData(
                    "uploadFaceMaskImage",
                    uniqueFileName,
                    requestBody
                )
            }

            1 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleDashBoardImage",
                    uniqueFileName,
                    requestBody
                )
            }

            2 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleFrontImage",
                    uniqueFileName,
                    requestBody
                )
            }

            3 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleNearSideImage",
                    uniqueFileName,
                    requestBody
                )
            }

            4 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleRearImage",
                    uniqueFileName,
                    requestBody
                )
            }

            5 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleOilLevelImage",
                    uniqueFileName,
                    requestBody
                )
            }

            6 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleOffSideImage",
                    uniqueFileName,
                    requestBody
                )
            }

            7 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehicleAddBlueImage",
                    uniqueFileName,
                    requestBody
                )
            }

            else -> throw IllegalArgumentException()
        }
        viewModel.uploadVehicleImage(userId, imagePart, requestCode)
    }

    private fun setImageUploadViews(requestCode: Int, type: Int) {
        var drawableID: Int = R.drawable.ic_yes
        if (type != 1)
            drawableID = R.drawable.refresh

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
}
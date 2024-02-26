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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.ImagesRepo
import com.clebs.celerity.databinding.FragmentCompleteTaskBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.HomeActivity.Companion.checked
import com.clebs.celerity.utils.ImageCodes
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertBitmapToBase64
import com.clebs.celerity.utils.decodeBase64Image
import com.clebs.celerity.utils.navigateTo
import com.clebs.celerity.utils.setImageView
import com.clebs.celerity.utils.toRequestBody
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import okhttp3.MultipartBody
import java.util.UUID

class CompleteTaskFragment : Fragment() {
    lateinit var mbinding: FragmentCompleteTaskBinding
    private var isclicked: Boolean = true
    private var isclickedtwo: Boolean = true
    private lateinit var viewModel: MainViewModel
    private val CAMERA_REQUEST_CODE = 101
    lateinit var imageView: ImageView
    var userId: Int = 0
    var requestCode:Int = 0

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

        if (checked.equals("0")) {
            //findNavController().navigate(R.id.vechileMileageFragment)
            navigateTo(R.id.vechileMileageFragment, requireContext(), findNavController())
        }

        viewModel.vehicleImageUploadInfoLiveData.observe(viewLifecycleOwner, Observer {
            println(it)
            if (it!!.Status == "404")
                mbinding.vehiclePicturesIB.setImageResource(R.drawable.ic_cross)
            else {
                if (it.IsVehicleImageUploaded == false) {
                    mbinding.vehiclePicturesIB.setImageResource(R.drawable.ic_cross)
                } else {
                    if (it.DaVehImgDashBoardFileName != null) {
                        mbinding.ivVehicleDashboard.setImageResource(R.drawable.ic_yes)
                    }
                    if (it.DaVehImgFaceMaskFileName != null) {
                        mbinding.ivFaceMask.setImageResource(R.drawable.ic_yes)
                    }
                    if (it.DaVehImgRearFileName != null) {
                        mbinding.ivRearImgUp.setImageResource(R.drawable.ic_yes)
                    }
                    if (it.DaVehImgFrontFileName != null) {
                        mbinding.ivFront.setImageResource(R.drawable.ic_yes)
                    }
                    if (it.DaVehImgNearSideFileName != null) {
                        mbinding.ivNearSide.setImageResource(R.drawable.ic_yes)
                    }
                    if (it.DaVehImgOffSideFileName != null) {
                        mbinding.ivOffSideImgUp.setImageResource(R.drawable.ic_yes)
                    }
                    if (it.DaVehImgFaceMaskFileName != null) {
                        mbinding.ivAddBlueImg.setImageResource(R.drawable.ic_yes)
                    }
                    if (it.DaVehImgOilLevelFileName != null) {
                        mbinding.ivOilLevel.setImageResource(R.drawable.ic_yes)
                    }
                }
            }
        })

        viewModel.GetVehicleImageUploadInfo(Prefs.getInstance(requireContext()).userID.toInt())



        viewModel.uploadVehicleImageLiveData.observe(viewLifecycleOwner, Observer {
            progressBarVisibility(false)
            if (it != null) {
                if(it.Status=="200"){
                    when(requestCode){
                        0->mbinding.ivFaceMask.setImageResource(R.drawable.ic_yes)
                        1->mbinding.ivVehicleDashboard.setImageResource(R.drawable.ic_yes)
                        2->mbinding.ivFront.setImageResource(R.drawable.ic_yes)
                        3->mbinding.ivNearSide.setImageResource(R.drawable.ic_yes)
                        4->mbinding.ivRearImgUp.setImageResource(R.drawable.ic_yes)
                        5->mbinding.ivOilLevel.setImageResource(R.drawable.ic_yes)
                        6->mbinding.ivOffSideImgUp.setImageResource(R.drawable.ic_yes)
                        7->mbinding.ivOilLevel.setImageResource(R.drawable.ic_yes)
                    }
                }
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
//        val listData : MutableList<ParentData> = ArrayList()
//        val parentData: Array<String> = arrayOf("Completed Task")
//
//        val childDataData1: MutableList<ChildData> = mutableListOf(ChildData("Vehicle Defect Sheet","0"),ChildData("Vehcile Pictures","0"),ChildData("Clocked In","0"))
//
//        val parentObj1 = ParentData(parentTitle = parentData[0], subList = childDataData1)
//        listData.add(parentObj1)
//
//       mbinding.exRecycle.adapter = RecycleAdapter(requireActivity(),listData)
        return mbinding.root
    }

    fun showAlert() {
        val factory = LayoutInflater.from(requireContext())
        val view: View = factory.inflate(R.layout.time_picker_dialog, null)
        val ic_start = view.findViewById<ImageButton>(R.id.ic_breakstart)
        val ic_breakend = view.findViewById<ImageButton>(R.id.ic_breakend)
        val edt_breakstart = view.findViewById<EditText>(R.id.edt_breakstart)
        val edt_breakend = view.findViewById<EditText>(R.id.edt_breakend)
        val deleteDialog: AlertDialog = AlertDialog.Builder(requireContext()).create()
        deleteDialog.setView(view)

        deleteDialog.setCanceledOnTouchOutside(true);
        deleteDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        deleteDialog.show();
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
        progressBarVisibility(true)
        val uniqueFileName = "image_${UUID.randomUUID()}.jpg"
        val requestBody = imageBitmap.toRequestBody()
        val imagePart = when (requestCode) {
            0 -> {
                MultipartBody.Part.createFormData(
                    "uploadFaceMaskDamageVideoFile",
                    uniqueFileName,
                    requestBody
                )
            }
            1 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehDashBoardImage",
                    uniqueFileName,
                    requestBody
                )
            }
            2 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehFrontImage",
                    uniqueFileName,
                    requestBody
                )
            }
            3 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehNearSideImage",
                    uniqueFileName,
                    requestBody
                )
            }
            4 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehRearImage",
                    uniqueFileName,
                    requestBody
                )
            }
            5 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehOilLevelDamageVideoFile",
                    uniqueFileName,
                    requestBody
                )
            }
            6 -> {
                MultipartBody.Part.createFormData(
                    "uploadVehOffSideImage",
                    uniqueFileName,
                    requestBody
                )
            }
            else -> throw IllegalArgumentException()
        }
        viewModel.uploadVehicleImage(userId, imagePart,requestCode)
    }

    fun progressBarVisibility(show:Boolean){
        if(show){
            mbinding.completeTaskFragmentPB.bringToFront()
            mbinding.completeTaskFragmentPB.visibility = View.VISIBLE
        }else{
            mbinding.completeTaskFragmentPB.visibility = View.GONE
        }

    }
}
package com.clebs.celerity.fragments.interior

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.ImageViewModel
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.ImageEntity
import com.clebs.celerity.databinding.FragmentWindScreenBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.ImageTakerActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getFileUri
import com.clebs.celerity.utils.getLoc
import com.clebs.celerity.utils.getVRegNo
import com.clebs.celerity.utils.setImageView
import com.clebs.celerity.utils.showToast
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class WindScreenFragment : Fragment() {

    lateinit var mbinding: FragmentWindScreenBinding
    private var isupload: Boolean = false
    private var isuploadtwo: Boolean = false
    private lateinit var viewModel: MainViewModel
    private val CAMERA_REQUEST_CODE = 101
    var base64: String? = null
    var defectName: String? = null
    lateinit var imageViewModel: ImageViewModel
    var imageEntity = ImageEntity()
    private lateinit var photoFile: File
    var timeStamp = "FileWindDefect"
    private lateinit var photoURI: Uri
    lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentWindScreenBinding.inflate(inflater, container, false)
        }

//        var tooltip = (activity as HomeActivity).tooltips
//        tooltip[0].view = mbinding.lln
//
//                toolTip(tooltip[0].msg,tooltip[0].dec,tooltip[0].id,tooltip[0].view)

        loadingDialog = (activity as HomeActivity).loadingDialog

        val vm_id = arguments?.get("vm_mileage")
        Log.e("vmvmvmv", "onCreateView: $vm_id")
        BubbleShowCaseBuilder(requireActivity())//Activity instance
            .title("Wind screen") //Any title for the bubble view
            .description("Provide Vehicle information") //More detailed description
            .arrowPosition(BubbleShowCase.ArrowPosition.TOP)

            //You can force the position of the arrow to change the location of the bubble.
            .backgroundColor((requireContext().getColor(R.color.very_light_orange)))
            //Bubble background color
            .textColor(requireContext().getColor(R.color.black)) //Bubble Text color
            .titleTextSize(16) //Title text size in SP (default value 16sp)
            .descriptionTextSize(12) //Subtitle text size in SP (default value 14sp)
            .image(requireContext().resources.getDrawable(R.drawable.windscreen_ic)!!) //Bubble main image
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
            ).targetView(mbinding.lln).showOnce("1")
            .highlightMode(BubbleShowCase.HighlightMode.VIEW_SURFACE)
            .backgroundColor(resources.getColor(R.color.very_light_orange)) //View to point out
            .show().finishSequence()
        viewModel = (activity as HomeActivity).viewModel
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.windScreenFragment)
        mbinding.tvNext.visibility = View.GONE
        imageViewModel = (activity as HomeActivity).imageViewModel
        imageEntity = imageViewModel.images.value!!
        imageViewModel.images.value.let {
            if (it != null) {
                if (it.dfNameWindScreen!!.isNotEmpty() && it.dfNameWindScreen != "f") {
                    setImageView(mbinding.windScreenIV, it.inWindScreen.toString(),requireContext())
                    mbinding.edtDefect.setText(it.dfNameWindScreen.toString())
                }
            }
        }
        timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
            mbinding.headerTop.anaCarolin.text = name
        }
        mbinding.headerTop.dxm5.text = (activity as HomeActivity).date/*
                if (Prefs.getInstance(requireContext()).currLocationName.isNotEmpty()) {
                    mbinding.headerTop.dxLoc.text =
                        Prefs.getInstance(requireContext()).currLocationName ?: ""
                } else if (Prefs.getInstance(requireContext()).workLocationName.isNotEmpty()) {
                    mbinding.headerTop.dxLoc.text =
                        Prefs.getInstance(requireContext()).workLocationName ?: ""
                }*/

        mbinding.headerTop.dxLoc.text = getLoc(prefs = Prefs.getInstance(requireContext()))
        mbinding.headerTop.dxReg.text = getVRegNo(prefs = Prefs.getInstance(requireContext()))

        if (mbinding.headerTop.dxReg.text.isEmpty()) mbinding.headerTop.strikedxRegNo.visibility =
            View.VISIBLE
        else mbinding.headerTop.strikedxRegNo.visibility = View.GONE

        if (mbinding.headerTop.dxLoc.text.isEmpty() || mbinding.headerTop.dxLoc.text == "" || mbinding.headerTop.dxLoc.text == "Not Allocated") mbinding.headerTop.strikedxLoc.visibility =
            View.VISIBLE
        else mbinding.headerTop.strikedxLoc.visibility = View.GONE

        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {

            if (Prefs.getInstance(requireContext()).currLocationName.isNotEmpty()) {
                mbinding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).currLocationName ?: ""
            } else if (Prefs.getInstance(requireContext()).workLocationName.isNotEmpty()) {
                mbinding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).workLocationName ?: ""
            } else {
                if (it != null) {
                    mbinding.headerTop.dxLoc.text = it.locationName ?: ""
                    if (it.vmId != 0) Prefs.getInstance(requireContext()).vmId = it.vmId
                }
            }
            if (it != null) {
                Prefs.getInstance(requireContext()).vmRegNo = it.vmRegNo ?: ""
                if (it.vmId != 0) Prefs.getInstance(requireContext()).vmId = it.vmId
            }
            mbinding.headerTop.dxReg.text = getVRegNo(prefs = Prefs.getInstance(requireContext()))

            if (mbinding.headerTop.dxReg.text.isEmpty()) mbinding.headerTop.strikedxRegNo.visibility =
                View.VISIBLE
            else mbinding.headerTop.strikedxRegNo.visibility = View.GONE
            if (mbinding.headerTop.dxLoc.text.isEmpty() || mbinding.headerTop.dxLoc.text == "" || mbinding.headerTop.dxLoc.text == "Not Allocated") mbinding.headerTop.strikedxLoc.visibility =
                View.VISIBLE
            else mbinding.headerTop.strikedxLoc.visibility = View.GONE

            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}".also { name ->
                mbinding.headerTop.anaCarolin.text = name
            }
            mbinding.headerTop.dxm5.text = (activity as HomeActivity).date
        }


        Log.d("Check ", imageViewModel.images.toString())

        mbinding.edtMil.setOnClickListener {
            edtMil()
        }
        mbinding.imageRadio.setOnClickListener {
            edtMil()
        }

        mbinding.windScreenIV.setOnClickListener {
            pictureDialogBase64()
        }

        mbinding.edtMilTwo.setOnClickListener {
            edtMil2()
        }
        mbinding.imageRadioTwo.setOnClickListener {
            edtMil2()
        }

        mbinding.run {
            edtDefect.doAfterTextChanged {
                mbinding.tvNext.isEnabled = (edtDefect.text?.length!! > 0)
                if (edtDefect.text?.length!! > 0) defectName = edtDefect.text?.toString()
                if (tvNext.isEnabled) {
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    tvNext.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                }
            }
            mbinding.tvNext.setOnClickListener {
                if (isupload) {
                    if (!base64.isNullOrEmpty()) {
                        imageEntity.inWindScreen = base64
                        imageViewModel.insertImage(imageEntity)
                    }
                    if (defectName!!.toString().isNotEmpty()) {
                        imageEntity.dfNameWindScreen = defectName!!.toString()
                        imageViewModel.insertDefectName(imageEntity)
                    }
                } else {
                    imageEntity.inWindScreen = "empty"
                    imageViewModel.insertImage(imageEntity)
                    imageEntity.dfNameWindScreen = "f"
                    imageViewModel.insertDefectName(imageEntity)
                }
                loadingDialog.show()
                navigateTo(R.id.windowsGlassFragment)
                //findNavController().navigate(R.id.windowsGlassFragment)
            }
        }
        mbinding.headerTop.headings.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
            //navigateTo(R.id.profileFragment)
        }
        return mbinding.root
    }


    fun toolTip(tittle: String, description: String, id: String, view: LinearLayout?) {
        view?.let {
            BubbleShowCaseBuilder(requireActivity()) //Activity instance
                .title(tittle) //Any title for the bubble view
                .description(description) //More detailed description
                .arrowPosition(BubbleShowCase.ArrowPosition.TOP)
                //You can force the position of the arrow to change the location of the bubble.
                .backgroundColor((requireContext().getColor(R.color.very_light_orange)))
                //Bubble background color
                .textColor(requireContext().getColor(R.color.black)) //Bubble Text color
                .titleTextSize(16) //Title text size in SP (default value 16sp)
                .descriptionTextSize(12) //Subtitle text size in SP (default value 14sp)
                .image(requireContext().resources.getDrawable(R.drawable.ic_info)!!) //Bubble main image
                .closeActionImage(requireContext().resources.getDrawable(R.drawable.cross)!!) //Custom close action image
                .showOnce(id).listener(
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
                ).targetView(it)
                .highlightMode(BubbleShowCase.HighlightMode.VIEW_SURFACE) //View to point out
                .show()
        }
    }

    private fun selectLayout1() {
        if (isupload) {
            mbinding.tvNext.visibility = View.VISIBLE
            mbinding.rlUploadDefect.visibility = View.VISIBLE
            mbinding.edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_filled_textview
                )
            )

            mbinding.imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_orange
                )
            )

            mbinding.edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_textview_button
                )
            )
            mbinding.imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_white
                )
            )
        } else {
            mbinding.rlUploadDefect.visibility = View.GONE
        }
    }

    private fun selectLayout2() {
        if (isuploadtwo) {
            mbinding.rlUploadDefect.visibility = View.GONE
            mbinding.edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_filled_textview
                )
            )
            mbinding.imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_orange
                )
            )

            mbinding.edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_textview_button
                )
            )
            mbinding.imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_white
                )
            )
        }
    }

    fun pictureDialogBase64() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runWithPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO

            ) {
                showPictureDialog()
            }
        } else {
            runWithPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) {
                showPictureDialog()
            }
        }
    }

    private fun showPictureDialog() {
/*        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val m_intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val uniqueFileName = "Defect_$timeStamp.jpg"
        photoFile = File(storageDir, uniqueFileName)
        photoURI = getFileUri(photoFile, (activity as HomeActivity))
        m_intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(m_intent, 101)*/
        val imageTakerActivityIntent  =Intent(requireContext(), ImageTakerActivity::class.java)
        resultLauncher.launch(imageTakerActivityIntent)
    }
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val data = result.data
                    if(data!=null){
                        val outputUri = data.getStringExtra("outputUri")
                        if (outputUri != null) {
                            photoURI = outputUri.toUri()
                            setImageView(mbinding.windScreenIV, outputUri,requireContext())
                            base64 = photoURI.toString()
                        }
                    }
                    Log.d("BaseInterior", "UniqueFileName2 URI ${photoURI.toString()}")
                } catch (_: Exception) {
                    showToast("Something went wrong!!Please retry", requireContext())
                }
            }
        }
/*    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            base64 = photoURI.toString()
            setImageView(mbinding.windScreenIV, base64.toString(),requireContext())
            Log.d("WindScreenFragment", "Base64 : ${base64!!.take(50)}")
        }
    }*/

    private fun tvNextColorUpdate() {
        if (mbinding.tvNext.isEnabled) {
            mbinding.tvNext.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.white
                )
            )

        } else {
            mbinding.tvNext.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.orange
                )
            )
        }
    }

    fun navigateTo(fragmentId: Int) {
        var prefs = Prefs.getInstance(requireContext())
        val fragmentStack = prefs.getNavigationHistory()
        val navOptions = NavOptions.Builder()
            .build()
        fragmentStack.push(fragmentId)
        findNavController().navigate(fragmentId, null, navOptions)
        prefs.saveNavigationHistory(fragmentStack)
    }

    private fun edtMil() {
        isuploadtwo = false
        isupload = !isupload
        selectLayout1()
    }

    private fun edtMil2() {
        isupload = false
        isuploadtwo = !isuploadtwo
        if (isuploadtwo) {
            mbinding.rlUploadDefect.visibility = View.GONE
            //  mbinding.tvNext.isEnabled = true
            tvNextColorUpdate()
            selectLayout2()
        }
        imageEntity.inWindScreen = "empty"
        imageViewModel.insertImage(imageEntity)
        imageEntity.dfNameWindScreen = "f"
        imageViewModel.insertDefectName(imageEntity)
        loadingDialog.show()
        navigateTo(R.id.windowsGlassFragment)
    }
}
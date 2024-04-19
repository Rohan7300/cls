package com.clebs.celerity.fragments.interior

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.ImageViewModel
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.ImageEntity
import com.clebs.celerity.databinding.FragmentWindScreenBinding
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.DBImages
import com.clebs.celerity.utils.DBNames
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertBitmapToBase64
import com.clebs.celerity.utils.setImageView
import com.clebs.celerity.utils.visible
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions


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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::mbinding.isInitialized) {
            mbinding = FragmentWindScreenBinding.inflate(inflater, container, false)
        }

//        var tooltip = (activity as HomeActivity).tooltips
//        tooltip[0].view = mbinding.lln
//
//                toolTip(tooltip[0].msg,tooltip[0].dec,tooltip[0].id,tooltip[0].view)


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
            )
            .targetView(mbinding.lln)
            .showOnce("1")
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
                setImageView(mbinding.windScreenIV, it.inWindScreen.toString())
                if (it.dfNameWindScreen!!.isNotEmpty() && it.dfNameWindScreen != "f") {
                    mbinding.edtDefect.setText(it.dfNameWindScreen.toString())
                }
            }
        }
        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {

            if (Prefs.getInstance(requireContext()).currLocationName != null) {
                mbinding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).currLocationName ?: ""
            } else if (Prefs.getInstance(requireContext()).workLocationName != null) {
                mbinding.headerTop.dxLoc.text =
                    Prefs.getInstance(requireContext()).workLocationName ?: ""
            } else {
                if (it != null) {
                    mbinding.headerTop.dxLoc.text = it.locationName ?: ""
                }
            }
            if (it != null) {
                mbinding.headerTop.dxReg.text = it.vmRegNo ?: ""
            }
            if(mbinding.headerTop.dxReg.text.isEmpty())
                mbinding.headerTop.strikedxRegNo.visibility = View.VISIBLE
            else
                mbinding.headerTop.strikedxRegNo.visibility = View.GONE
            if(mbinding.headerTop.dxLoc.text.isEmpty()||mbinding.headerTop.dxLoc.text=="")
                mbinding.headerTop.strikedxLoc.visibility = View.VISIBLE
            else
                mbinding.headerTop.strikedxLoc.visibility = View.GONE


            "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}"
                .also { name -> mbinding.headerTop.anaCarolin.text = name }
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
                if (edtDefect.text?.length!! > 0)
                    defectName = edtDefect.text?.toString()
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
                .showOnce(id)
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
                .targetView(it)
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
                    requireContext(),
                    R.drawable.shape_filled_textview
                )
            )

            mbinding.imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_circle_orange
                )
            )

            mbinding.edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_textview_button
                )
            )
            mbinding.imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_circle_white
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
                    requireContext(),
                    R.drawable.shape_filled_textview
                )
            )
            mbinding.imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_circle_orange
                )
            )

            mbinding.edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.shape_textview_button
                )
            )
            mbinding.imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.round_circle_white
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

    fun showPictureDialog() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            base64 = convertBitmapToBase64(imageBitmap)
            setImageView(mbinding.windScreenIV, base64.toString())
            Log.d("WindScreenFragment", "Base64 : ${base64!!.take(50)}")
        }
    }

    private fun tvNextColorUpdate() {
        if (mbinding.tvNext.isEnabled) {
            mbinding.tvNext.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

        } else {
            mbinding.tvNext.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orange
                )
            )
        }
    }

    fun navigateTo(fragmentId: Int) {
        var prefs = Prefs.getInstance(requireContext())
        val fragmentStack = prefs.getNavigationHistory()
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_left) // Animation for entering the new fragment
            // Animation for exiting the current fragment
//            .setPopEnterAnim(R.anim.slide_in_right) // Animation for entering the previous fragment when navigating back
//            .setPopExitAnim(R.anim.slide_left) // Animation for exiting the current fragment when navigating back
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
        navigateTo(R.id.windowsGlassFragment)
    }
}
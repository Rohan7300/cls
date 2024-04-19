package com.clebs.celerity.fragments

import android.Manifest
import android.app.Activity
import android.content.ClipDescription
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.ImageViewModel
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.ImageEntity
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.convertBitmapToBase64
import com.clebs.celerity.utils.setImageView
import com.clebs.celerity.utils.showToast
import com.elconfidencial.bubbleshowcase.BubbleShowCase
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener
import java.util.Stack

abstract class BaseInteriorFragment : Fragment() {
    lateinit var viewModel: MainViewModel
    lateinit var imageViewModel: ImageViewModel
    private val CAMERA_REQUEST_CODE = 101
    var base64: String? = null
    private lateinit var imageView: ImageView
    private var nextEnabled: Boolean = false
    var functionalView: Boolean = false
    var defectView: Boolean = false
    var defectName: String? = null
    var imageEntity = ImageEntity()
    lateinit var prefs: Prefs
    lateinit var fragmentStack: Stack<Int>
    lateinit var dxLoc: TextView
    lateinit var dxReg: TextView
    lateinit var dxm5: TextView
    lateinit var ana_carolin: TextView
    lateinit var ivX: ImageView

    companion object{
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.READ_MEDIA_VIDEO)
                    add(Manifest.permission.READ_MEDIA_IMAGES)
                    add(Manifest.permission.READ_MEDIA_AUDIO)
                }
            }.toTypedArray()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as HomeActivity).viewModel
        imageViewModel = (activity as HomeActivity).imageViewModel
        dxLoc = view.findViewById(R.id.dxLoc)
        dxReg = view.findViewById(R.id.dxReg)
        dxm5 = view.findViewById(R.id.dxm5)
        ana_carolin = view.findViewById(R.id.ana_carolin)

        var strikedxRegNo =view.findViewById<LinearLayout>(R.id.strikedxRegNo)
        var strikedxLoc =view.findViewById<LinearLayout>(R.id.strikedxRegNo)

        "${(activity as HomeActivity).firstName} ${(activity as HomeActivity).lastName}"
            .also { name -> ana_carolin.text = name }
        dxm5.text = (activity as HomeActivity).date
        if (Prefs.getInstance(requireContext()).currLocationName != null) {
            dxLoc.text =
                Prefs.getInstance(requireContext()).currLocationName ?: ""
        } else if (Prefs.getInstance(requireContext()).workLocationName != null) {
            dxLoc.text =
                Prefs.getInstance(requireContext()).workLocationName ?: ""
        }

        viewModel.vechileInformationLiveData.observe(viewLifecycleOwner) {
            dxLoc.text = it?.locationName ?: ""
            dxReg.text = it?.vmRegNo ?: ""
            if (Prefs.getInstance(requireContext()).currLocationName != null) {
                dxLoc.text =
                    Prefs.getInstance(requireContext()).currLocationName ?: ""
            } else if (Prefs.getInstance(requireContext()).workLocationName != null) {
                dxLoc.text =
                    Prefs.getInstance(requireContext()).workLocationName ?: ""
            } else {
                if (it != null) {
                    dxLoc.text = it.locationName ?: ""
                }
            }
            if(dxReg.text.isEmpty())
                strikedxRegNo.visibility = View.VISIBLE
            else
                strikedxRegNo.visibility = View.GONE
            if(dxLoc.text.isEmpty()||dxLoc.text=="")
                strikedxLoc.visibility = View.VISIBLE
            else
                strikedxLoc.visibility = View.GONE
        }
//        var tooltip = (activity as HomeActivity).tooltips
//        if (findNavController().currentDestination?.id==R.id.windowsGlassFragment){
//            Log.e("dkjfdkfkjdfjkdjkfdkj1", "onViewCreated: ", )
//            toolTip(tooltip[1].msg,tooltip[1].dec,tooltip[1].id, view.findViewById(R.id.lln))
//        }
//        else{
//            Log.e("dkjfdkfkjdfjkdjkfdkj", "onViewCreated: ", )
//        }

//        when(findNavController().currentDestination?.id){
//            R.id.windowsGlassFragment -> {
//                toolTip(tooltip[1].msg,tooltip[1].dec,tooltip[1].id, view.findViewById(R.id.lln))
//            }
//        }


        prefs = Prefs.getInstance(requireContext())
        fragmentStack = prefs.getNavigationHistory()
    }

    abstract fun clickListeners()
    abstract fun saveNnext()
    private fun saveNnextVisibility(tvNext: TextView, nextEnabled: Boolean) {
        if (nextEnabled) {
            tvNext.isEnabled = true
            //tvNext.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange))
            tvNext.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )

        } else {
            tvNext.isEnabled = false
            //tvNext.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            tvNext.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.orange
                )
            )
        }
    }

    protected fun pictureDialogBase64(iv: ImageView) {
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            runWithPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO

            ) {
                showPictureDialog(iv)

            }
        } else {
            runWithPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE

            ) {
                showPictureDialog(iv)
            }
        }*/
        ivX = iv
        if(allPermissionsGranted()){
            showPictureDialog(iv)
        }else{

            requestpermissions()
        }
    }

    private fun requestpermissions() {

        activityResultLauncher.launch(BaseInteriorFragment.REQUIRED_PERMISSIONS)

    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in BaseInteriorFragment.REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                showToast("Permission denied", requireContext())
            } else {
                showPictureDialog(ivX)
            }
        }

    private fun showPictureDialog(iv: ImageView) {
        imageView = iv
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, 101)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            base64 = convertBitmapToBase64(imageBitmap)
            setImageView(imageView, base64.toString())
        }
    }

    fun editMil1Visibilty(
        tvNext: TextView,
        rlUploadDefect: RelativeLayout,
        edtMil: TextView,
        edtMilTwo: TextView,
        imageRadioTwo: ImageView,
        imageRadio: ImageView
    ) {
        functionalView = false
        defectView = !defectView
        if (defectView) {
            tvNext.visibility = View.VISIBLE
            rlUploadDefect.visibility = View.VISIBLE
            edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_filled_textview
                )
            )
            imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_orange
                )
            )
            edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_textview_button
                )
            )
            imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_white
                )
            )
        } else {
            tvNext.visibility = View.GONE
            rlUploadDefect.visibility = View.GONE
            edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_textview_button
                )
            )
            imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_white
                )
            )
        }
    }


    fun editMil2Visibilty(
        tvNext: TextView,
        rlUploadDefect: RelativeLayout,
        edtMil: TextView,
        edtMilTwo: TextView,
        imageRadioTwo: ImageButton,
        imageRadio: ImageButton
    ) {
        defectView = false
        functionalView = !functionalView
        if (functionalView) {

            nextEnabled = true
            saveNnextVisibility(tvNext, nextEnabled)

            rlUploadDefect.visibility = View.VISIBLE
            edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_filled_textview
                )
            )
            imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_orange
                )
            )
            rlUploadDefect.visibility = View.GONE
            edtMil.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_textview_button
                )
            )
            imageRadio.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_white
                )
            )

        } else {
            nextEnabled = false
            saveNnextVisibility(tvNext, nextEnabled)
            rlUploadDefect.visibility = View.GONE
            edtMilTwo.setBackgroundDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.shape_textview_button
                )
            )
            imageRadioTwo.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(), R.drawable.round_circle_white
                )
            )
        }
    }

    fun editMil2VisibilityNew(
        rlUploadDefect: RelativeLayout,
        edtMil: TextView,
        edtMilTwo: TextView,
        imageRadioTwo: ImageView,
        imageRadio: ImageView
    ) {
        defectView = false
        rlUploadDefect.visibility = View.VISIBLE
        edtMilTwo.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.shape_filled_textview
            )
        )
        imageRadioTwo.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.round_circle_orange
            )
        )
        rlUploadDefect.visibility = View.GONE
        edtMil.setBackgroundDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.shape_textview_button
            )
        )
        imageRadio.setImageDrawable(
            ContextCompat.getDrawable(
                requireContext(), R.drawable.round_circle_white
            )
        )
    }

    fun doAfterTextChanged(tvNext: TextView, edtDefect: EditText) {
        tvNext.isEnabled = (edtDefect.text?.length!! > 0)
        if (edtDefect.text?.length!! > 0)
            defectName = edtDefect.text?.toString()
        if (tvNext.isEnabled) {
            saveNnextVisibility(tvNext, true)
        } else {
            saveNnextVisibility(tvNext, false)
        }
    }

    fun navigateTo(fragmentId: Int) {
        fragmentStack.push(fragmentId)
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_left) // Animation for entering the new fragment
       // Animation for exiting the current fragment
//            .setPopEnterAnim(R.anim.slide_in_right) // Animation for entering the previous fragment when navigating back
//            .setPopExitAnim(R.anim.slide_left) // Animation for exiting the current fragment when navigating back
            .build()
        findNavController().navigate(fragmentId,null,navOptions)
        prefs.saveNavigationHistory(fragmentStack)
    }

    private fun allPermissionsGranted() = BaseInteriorFragment.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }
    fun toolTip(tittle:String,description: String,id:String,view: LinearLayout?) {
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
}
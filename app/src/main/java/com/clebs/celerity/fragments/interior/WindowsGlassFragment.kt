package com.clebs.celerity.fragments.interior

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentWindowsGlassBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.fragments.BaseInteriorFragment
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.decodeBase64Image
import com.clebs.celerity.utils.setImageView

class WindowsGlassFragment : BaseInteriorFragment() {
    private lateinit var mBinding: FragmentWindowsGlassBinding
    lateinit var loadingDialog: LoadingDialog
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.windowsGlassFragment)

        mBinding.tvNext.visibility = View.GONE
        loadingDialog = (activity as HomeActivity).loadingDialog
        loadingDialog.dismiss()
        clickListeners()
        setDefault(mBinding.windowDefectUploadIv,mBinding.edtDefect)
    }
    override fun clickListeners() {
        mBinding.run {
            edtMil.setOnClickListener {
                editMil1Visibilty(
                    mBinding.tvNext,
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )
            }

            imageRadio.setOnClickListener {
                editMil1Visibilty(
                    mBinding.tvNext,
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )
            }

            edtMilTwo.setOnClickListener {
/*                editMil2Visibilty(
                    mBinding.tvNext,
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )*/
                editMil2VisibilityNew(
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )
                functionalView = true
                defectView = false
                saveNnext()
            }

            imageRadioTwo.setOnClickListener {
                editMil2VisibilityNew(
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )
                functionalView = true
                defectView = false
                saveNnext()
            }

            windowDefectUploadIv.setOnClickListener {
                pictureDialogBase64(windowDefectUploadIv)



            }
            edtDefect.doAfterTextChanged {
                doAfterTextChanged(mBinding.tvNext,mBinding.edtDefect)
            }
            tvNext.setOnClickListener {
                saveNnext()
            }

        }

    }

    fun setDefault(defaultIv: ImageView, edtDefect: EditText){
        imageViewModel.images.value.let {
            if(it!=null){
                imageEntity = imageViewModel.images.value!!
                if (it.dfNameWindowGlass!!.isNotEmpty() && it.dfNameWindowGlass != "f") {
                    setImageView(defaultIv, it.inWindowGlass.toString(),requireContext())
                    edtDefect.setText(it.dfNameWindowGlass.toString())
                }
            }
        }
    }

    override fun saveNnext() {
        if(defectView) {
            if (base64 != null) {
                imageEntity.inWindowGlass = base64
                imageViewModel.insertImage(imageEntity)
            }
            if(defectName!!.toString().isNotEmpty()){
                imageEntity.dfNameWindowGlass = defectName!!.toString()
                imageViewModel.insertDefectName(imageEntity)
            }
        }else if(functionalView){
            imageEntity.inWindowGlass = "empty"
            imageViewModel.insertImage(imageEntity)
            imageEntity.dfNameWindowGlass = "f"
            imageViewModel.insertDefectName(imageEntity)

        }
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_left)
            .setExitAnim(R.anim.slide_in_right) // Animation for exiting the current fragment
//            .setPopEnterAnim(R.anim.slide_in_right) // Animation for entering the previous fragment when navigating back
//            .setPopExitAnim(R.anim.slide_left) // Animation for exiting the current fragment when navigating back
            .build()
        loadingDialog.show()
//        Handler().postDelayed(Runnable {  navigateTo(R.id.wipersScreenFragment) }, 1000)
        navigateTo(R.id.wipersScreenFragment)
        //findNavController().navigate(R.id.cabSecurityFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::mBinding.isInitialized) mBinding =
            FragmentWindowsGlassBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    fun setImageViewTwo(im: ImageView, value: String) {
        try {
            val bitmap: Bitmap = decodeBase64Image(value)
            im.setImageBitmap(bitmap)
        } catch (_: Exception) {
        }
    }

}
package com.clebs.celerity.fragments.interior

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentWindowsGlassBinding
import com.clebs.celerity.fragments.BaseInteriorFragment
import com.clebs.celerity.utils.setImageView

class WindowsGlassFragment : BaseInteriorFragment() {
    private lateinit var mBinding: FragmentWindowsGlassBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.windowsGlassFragment)
        mBinding.tvNext.visibility = View.GONE
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
                setImageView(defaultIv, it.inWindowGlass.toString())
                if (it.dfNameWindowGlass!!.isNotEmpty() && it.dfNameWindowGlass != "f") {
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



}
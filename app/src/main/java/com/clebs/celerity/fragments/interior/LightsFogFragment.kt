package com.clebs.celerity.fragments.interior

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentLightsFogBinding
import com.clebs.celerity.fragments.BaseInteriorFragment
import com.clebs.celerity.utils.setImageView


class LightsFogFragment : BaseInteriorFragment() {
    private lateinit var mBinding: FragmentLightsFogBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.lightsFogFragment)
        clickListeners()
        setDefault(mBinding.imageUploadIV,mBinding.edtDefect)
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

            imageUploadIV.setOnClickListener {
                pictureDialogBase64(imageUploadIV)
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
                setImageView(defaultIv, it.inFogLights.toString())
                if (it.dfNameFogLights!!.isNotEmpty() && it.dfNameFogLights != "f") {
                    edtDefect.setText(it.dfNameFogLights.toString())
                }
            }
        }
    }

    override fun saveNnext() {
        if(defectView) {
            if (base64 != null) {
                imageEntity.inFogLights = base64
                imageViewModel.insertImage(imageEntity)
            }
            if(defectName!!.toString().isNotEmpty()){
                imageEntity.dfNameFogLights = defectName!!.toString()
                imageViewModel.insertDefectName(imageEntity)
            }
        }else if(functionalView){
            imageEntity.inFogLights = "empty"
            imageViewModel.insertImage(imageEntity)
            imageEntity.dfNameFogLights = "f"
            imageViewModel.insertDefectName(imageEntity)
        }
        navigateTo(R.id.indicatorsSideRepeatorFragment)
        //findNavController().navigate(R.id.indicatorsSideRepeatorFragment)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::mBinding.isInitialized) mBinding =
            FragmentLightsFogBinding.inflate(inflater, container, false)
        return mBinding.root
    }


}
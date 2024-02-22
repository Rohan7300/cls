package com.clebs.celerity.fragments.interior

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentFuelBinding
import com.clebs.celerity.fragments.BaseInteriorFragment
import com.clebs.celerity.utils.setImageView

class FuelFragment : BaseInteriorFragment() {
    private lateinit var mBinding: FragmentFuelBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.fuelAddBlueFragment)
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
                editMil2Visibilty(
                    mBinding.tvNext,
                    mBinding.rlUploadDefect,
                    mBinding.edtMil,
                    mBinding.edtMilTwo,
                    mBinding.imageRadioTwo,
                    mBinding.imageRadio
                )
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
                setImageView(defaultIv, it.inFuelAdBlueLevel.toString())
                if (it.dfNameFuelAdBlueLevel!!.isNotEmpty() && it.dfNameFuelAdBlueLevel != "f") {
                    edtDefect.setText(it.dfNameFuelAdBlueLevel.toString())
                }
            }
        }
    }

    override fun saveNnext() {
        if(defectView) {
            if (base64 != null) {
                imageEntity.inFuelAdBlueLevel = base64
                imageViewModel.insertImage(imageEntity)
            }
            if(defectName!!.toString().isNotEmpty()){
                imageEntity.dfNameFuelAdBlueLevel = defectName!!.toString()
                imageViewModel.insertDefectName(imageEntity)
            }
        }else if(functionalView){
            imageEntity.inFuelAdBlueLevel = "empty"
            imageViewModel.insertImage(imageEntity)
            imageEntity.dfNameFuelAdBlueLevel = "f"
            imageViewModel.insertDefectName(imageEntity)
        }
        navigateTo(R.id.oilCoolantFragment)
        //findNavController().navigate(R.id.oilCoolantFragment)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::mBinding.isInitialized) mBinding =
            FragmentFuelBinding.inflate(inflater, container, false)
        return mBinding.root
    }


}
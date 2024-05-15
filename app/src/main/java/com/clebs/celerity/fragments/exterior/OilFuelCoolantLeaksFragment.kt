package com.clebs.celerity.fragments.exterior

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.doAfterTextChanged
import com.clebs.celerity.R
import com.clebs.celerity.databinding.FragmentOilFuelCoolantLeaksBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.fragments.BaseInteriorFragment
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.utils.setImageView

class OilFuelCoolantLeaksFragment : BaseInteriorFragment() {

    private lateinit var mBinding: FragmentOilFuelCoolantLeaksBinding
    lateinit var loadingDialog: LoadingDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!this::mBinding.isInitialized) mBinding =
            FragmentOilFuelCoolantLeaksBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setLastVisitedScreenId(requireActivity(), R.id.oilFuelCoolantFragment)
        mBinding.tvNext.visibility = View.GONE
        clickListeners()
        loadingDialog = (activity as HomeActivity).loadingDialog
        loadingDialog.dismiss()
        setDefault(mBinding.imageUploadIV, mBinding.edtDefect)
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

            imageUploadIV.setOnClickListener {
                pictureDialogBase64(imageUploadIV)
            }
            edtDefect.doAfterTextChanged {
                doAfterTextChanged(mBinding.tvNext, mBinding.edtDefect)
            }
            tvNext.setOnClickListener {
                saveNnext()
            }
        }
    }

    fun setDefault(defaultIv: ImageView, edtDefect: EditText) {
        imageViewModel.images.value.let {
            if (it != null) {
                imageEntity = imageViewModel.images.value!!
                setImageView(defaultIv, it.exOilFuelCoolantLeaks.toString())
                if (it.dfNameOilFuelCoolantLeaks!!.isNotEmpty() && it.dfNameOilFuelCoolantLeaks != "f") {
                    edtDefect.setText(it.dfNameOilFuelCoolantLeaks.toString())
                }
            }
        }
    }

    override fun saveNnext() {
        if (defectView) {
            if (base64 != null) {
                imageEntity.exOilFuelCoolantLeaks = base64
                imageViewModel.insertImage(imageEntity)
            }
            if (defectName!!.toString().isNotEmpty()) {
                imageEntity.dfNameOilFuelCoolantLeaks = defectName!!.toString()
                imageViewModel.insertDefectName(imageEntity)
            }
        } else if (functionalView) {
            imageEntity.exOilFuelCoolantLeaks = "empty"
            imageViewModel.insertImage(imageEntity)
            imageEntity.dfNameOilFuelCoolantLeaks = "f"
            imageViewModel.insertDefectName(imageEntity)
        }
        loadingDialog.show()
        //findNavController().navigate(R.id.excessiveSmokeFragment)
        navigateTo(R.id.excessiveSmokeFragment)
    }
}
package com.clebs.celerity_admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity_admin.databinding.ActivitySubmitWeeklyDefectBinding
import com.clebs.celerity_admin.factory.MyViewModelFactory
import com.clebs.celerity_admin.network.ApiService
import com.clebs.celerity_admin.network.RetrofitService
import com.clebs.celerity_admin.repo.MainRepo
import com.clebs.celerity_admin.utils.DependencyClass.currentWeeklyDefectItem
import com.clebs.celerity_admin.viewModels.MainViewModel

class SubmitWeeklyDefectActivity : AppCompatActivity() {
    lateinit var binding: ActivitySubmitWeeklyDefectBinding
    lateinit var vm: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        vm = ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_submit_weekly_defect)
        if (currentWeeklyDefectItem != null)
            vm.GetWeeklyDefectCheckImages(currentWeeklyDefectItem!!.vdhCheckId)

        vm.lDGetWeeklyDefectCheckImages.observe(this) {
            if (it != null) {
                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthFrontNs,
                    binding.tyreDepthFrontImageUploadBtn,
                    binding.tyreDepthFrontImageFileName
                )

                setRadioCard(
                    it.TyrePressureFrontNS,
                    binding.tyrePressureFrontFullRB,
                    binding.tyrePressureFrontBelowRB
                )

                setUploadCardBtn(
                    it.VdhDefChkImgTyrethreaddepthRearNs,
                    binding.tyreDepthRearImageUploadBtn,
                    binding.tyreDepthRearImageUploadFileName
                )

                setRadioCard(
                    it.TyrePressureFrontNS,
                    binding.tyrePressureRearNSFullRB,
                    binding.tyrePressureRearNSBelowRB
                )

            }
        }
    }

    private fun setUploadCardBtn(
        vdhDefChkImgTyrethreaddepthFrontNs: String,
        tyreDepthFrontImageUploadBtn: AppCompatButton,
        tyreDepthFrontImageFileName: TextView
    ) {
        if (vdhDefChkImgTyrethreaddepthFrontNs.isNotBlank()) {
            tyreDepthFrontImageUploadBtn.text = "Upload Again"
            tyreDepthFrontImageFileName.text = vdhDefChkImgTyrethreaddepthFrontNs
            tyreDepthFrontImageFileName.setTextColor(ContextCompat.getColor(this, R.color.blue_hex))
            tyreDepthFrontImageUploadBtn.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.greenBtn)
        }
    }

    private fun setRadioCard(
        tyrePressureFrontNS: Boolean,
        tyrePressureFrontFullRB: RadioButton,
        tyrePressureFrontBelowRB: RadioButton
    ) {
        if (tyrePressureFrontNS) {
            tyrePressureFrontFullRB.isChecked = true
        } else {
            tyrePressureFrontBelowRB.isChecked = true
        }
    }
}
package com.clebs.celerity.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.DeductionListListener
import com.clebs.celerity.adapters.OutstandingDeductionAdapter
import com.clebs.celerity.databinding.ActivityDownloadDeductionsBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.models.response.GetDAOutStandingDeductionListResponse
import com.clebs.celerity.utils.DependencyProvider.currentDeductionHistory
import com.clebs.celerity.utils.DependencyProvider.getMainVM
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.noInternetCheck

class OutstandingDeductionActivity : AppCompatActivity(), DeductionListListener {
    lateinit var binding: ActivityDownloadDeductionsBinding
    lateinit var vm: MainViewModel
    lateinit var prefs: Prefs
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_download_deductions)
        vm = getMainVM(this)
        prefs = Prefs.getInstance(this)
        loadingDialog = LoadingDialog(this)
        val adapter = OutstandingDeductionAdapter(this, 1)
        val adapter2 = OutstandingDeductionAdapter(this, 2)
        var dedList = GetDAOutStandingDeductionListResponse()
        var dedList2 = GetDAOutStandingDeductionListResponse()
        binding.deductionsRV.adapter = adapter
        binding.deductionsthirdPartyRV.adapter = adapter2
        binding.deductionsRV.layoutManager = LinearLayoutManager(this)
        binding.deductionsthirdPartyRV.layoutManager = LinearLayoutManager(this)
        noInternetCheck(this,binding.nointernetLL,this)
        binding.backIcon.setOnClickListener {
            finish()
        }
        loadingDialog.show()
        vm.GetDAOutStandingDeductionList(prefs.clebUserId.toInt(), 0)
        vm.liveDataGetDAOutStandingDeductionList.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                binding.nodata2.visibility = View.GONE
                dedList.clear()
                dedList.add(it)
                adapter.submitList(dedList)
                dedList2.clear()
                dedList2.add(it)
                adapter2.submitList(dedList)
            } else {
                binding.nodata2.visibility = View.VISIBLE
            }
        }
        vm.liveDataGetDriverDeductionHistory.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                currentDeductionHistory = it
                startActivity(
                    Intent(
                        this@OutstandingDeductionActivity,
                        DeductionHistoryActivity::class.java
                    )
                )
            }
        }
    }

    override fun onClick(i: Int) {
        loadingDialog.show()
        vm.GetDriverDeductionHistory(prefs.clebUserId.toInt(), i)
    }
}
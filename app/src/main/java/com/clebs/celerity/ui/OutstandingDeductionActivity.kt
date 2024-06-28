package com.clebs.celerity.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.DeductionListListener
import com.clebs.celerity.adapters.OutstandingDeductionAdapter
import com.clebs.celerity.databinding.ActivityDownloadDeductionsBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider.currentDeductionHistory
import com.clebs.celerity.utils.DependencyProvider.getMainVM
import com.clebs.celerity.utils.Prefs

class OutstandingDeductionActivity : AppCompatActivity(), DeductionListListener {
    lateinit var binding: ActivityDownloadDeductionsBinding
    lateinit var vm:MainViewModel
    lateinit var prefs: Prefs
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_download_deductions)
        vm = getMainVM(this)
        prefs = Prefs.getInstance(this)
        loadingDialog = LoadingDialog(this)
        val adapter = OutstandingDeductionAdapter(this)
        binding.deductionsRV.adapter = adapter
        binding.deductionsRV.layoutManager = LinearLayoutManager(this)
        binding.backIcon.setOnClickListener {
            finish()
        }
        vm.GetDAOutStandingDeductionList(prefs.clebUserId.toInt())
        vm.liveDataGetDAOutStandingDeductionList.observe(this){
            if(it!=null){
                adapter.submitList(it)
            }
        }
        vm.liveDataGetDriverDeductionHistory.observe(this){
            if(it!=null){
                currentDeductionHistory = it
                startActivity(Intent(this@OutstandingDeductionActivity,DeductionHistoryActivity::class.java))
            }
        }
    }

    override fun onClick() {
        vm.GetDriverDeductionHistory(prefs.clebUserId.toInt())
    }
}
package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.DADeductionHistoryAdapter
import com.clebs.celerity.databinding.ActivityDeductionHistoryBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.DependencyProvider.currentDeductionHistory
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.noInternetCheck
import com.clebs.celerity.utils.showToast

class DeductionHistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityDeductionHistoryBinding
    lateinit var vm: MainViewModel
    lateinit var prefs: Prefs
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_deduction_history)
        vm = DependencyProvider.getMainVM(this)
        prefs = Prefs.getInstance(this)
        loadingDialog = LoadingDialog(this)
        var adapter = DADeductionHistoryAdapter()
        binding.deductionsHistoryRV.adapter = adapter
        binding.backIcon.setOnClickListener {
            finish()
        }
        noInternetCheck(this,binding.nointernetLL,this)

        binding.deductionsHistoryRV.layoutManager = LinearLayoutManager(this)
        if (currentDeductionHistory != null)
            adapter.submitList(currentDeductionHistory!!)
        else {
            showToast("Deductions Not Found!!", this)
            finish()
        }
    }
}
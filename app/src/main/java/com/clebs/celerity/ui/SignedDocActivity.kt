package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.adapters.SignedDocsListAdapter
import com.clebs.celerity.databinding.ActivitySignedDocActivtyBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider.getMainVM
import com.clebs.celerity.utils.Prefs

class SignedDocActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignedDocActivtyBinding
    lateinit var vm:MainViewModel
    lateinit var loadingDialog: LoadingDialog
    lateinit var prefs: Prefs
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@SignedDocActivity,
            R.layout.activity_signed_doc_activty
        )
        vm = getMainVM(this)
        prefs = Prefs.getInstance(this)
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        val adapter = SignedDocsListAdapter(this)
        binding.policyGridRV.adapter = adapter
        binding.backIcon.setOnClickListener {
            finish()
        }
        binding.policyGridRV.layoutManager = LinearLayoutManager(this)
        vm.GetCompanySignedDocumentList(prefs.clebUserId.toInt())
        vm.liveDataGetCompanySignedDocumentList.observe(this){
            loadingDialog.dismiss()
            if(it!=null){
                adapter.submitList(it)
            }
        }



    }
}
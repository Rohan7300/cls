package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.clebs.celerity.R
import com.clebs.celerity.databinding.ActivityEmergencyContactBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider.getMainVM
import com.clebs.celerity.utils.Prefs

class EmergencyContactActivity : AppCompatActivity() {
    lateinit var binding: ActivityEmergencyContactBinding
    lateinit var loadingDialog: LoadingDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@EmergencyContactActivity,
            R.layout.activity_emergency_contact
        )
        loadingDialog = LoadingDialog(this)
        var mainVM =getMainVM(this)
        var prefs = Prefs.getInstance(this)
        mainVM.GetDAEmergencyContact(prefs.clebUserId.toInt())
        loadingDialog.show()
        mainVM.liveDataGetDAEmergencyContact.observe(this){
            loadingDialog.dismiss()
            if(it!=null){
                binding.noDataLL.visibility = View.GONE
                binding.webViewEmContact.visibility = View.VISIBLE
                binding.webViewEmContact.loadData(it, "text/html", "UTF-8")
            }else{
                binding.noDataLL.visibility = View.VISIBLE
                binding.webViewEmContact.visibility = View.GONE
            }
        }
    }
}
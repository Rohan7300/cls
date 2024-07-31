package com.clebs.celerity.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.clebs.celerity.R
import com.clebs.celerity.databinding.ActivityEmergencyContactBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.utils.DependencyProvider.getMainVM
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.noInternetCheck

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
        binding.backIcon.setOnClickListener {
            finish()
        }
        loadingDialog.show()
        noInternetCheck(this,binding.nointernetLL,this)
        val inputStream = this.assets.open("emergency_contact_info.html")
        val dfHTML = inputStream.bufferedReader().use { it.readText() }


        mainVM.liveDataGetDAEmergencyContact.observe(this){
            loadingDialog.dismiss()
            if(it!=null){
                binding.noDataLL.visibility = View.GONE
                binding.webViewEmContact.visibility = View.VISIBLE
                try {
                    binding.webViewEmContact.loadDataWithBaseURL(null,it, "text/html", "UTF-8",null)
                }catch (_:Exception){
                    binding.webViewEmContact.loadDataWithBaseURL(null,dfHTML, "text/html", "UTF-8",null)
                }
            }else{
                binding.noDataLL.visibility = View.GONE
                binding.webViewEmContact.visibility = View.VISIBLE
                binding.webViewEmContact.loadDataWithBaseURL(null,dfHTML, "text/html", "UTF-8",null)
            }
        }
    }
}
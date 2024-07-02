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
        binding.backIcon.setOnClickListener {
            finish()
        }
        loadingDialog.show()
        mainVM.liveDataGetDAEmergencyContact.observe(this){
            loadingDialog.dismiss()
            if(it!=null){
                binding.noDataLL.visibility = View.GONE
                binding.webViewEmContact.visibility = View.VISIBLE
                binding.webViewEmContact.loadData(it, "text/html", "UTF-8")
            }else{
                binding.noDataLL.visibility = View.GONE
                binding.webViewEmContact.visibility = View.VISIBLE
                binding.webViewEmContact.loadData(" <style> a{text-decoration:none;} </style>\n" +
                        "<h3>Contact Numbers:</h3>\n" +
                        "<p>\n" +
                        "    Head Office – <a href=\"tel:02036371700\">020 3637 1700 </a><br />\n" +
                        "    Out of hours call your local On-Site Manager number as given to you.<br />\n" +
                        "    Non-Emergency service - call <a href=\"tel:101\">101</a> To report an incident that will not require immediate help.<br />\n" +
                        "    Emergency services–call <a href=\"tel:999\">999</a> or <a href=\"tel:111\">111</a> for direct response of Ambulance or medical assistance.<br />\n" +
                        "</p>\n" +
                        "<h3>What to do: Theft Threats and (attempted) Robbery</h3>\n" +
                        "<p>\n" +
                        "    <b>Step 1</b><br />\n" +
                        "    Emergency Service: Call <a href=\"tel:999\">999 </a> or <a href=\"tel:111\">111 </a> For Direct response of Ambulance or medical assistance.<br />\n" +
                        "    Non - Emergency service call <a href=\"tel:101\">101</a> to report an incident that will not require immediate help.<br /><br />\n" +
                        "    <b>Step 2</b><br />\n" +
                        "    <a href=\"tel:+448081646718\">+44 808 164 6718 </a> and receive 24/7 immediate assistance<br />\n" +
                        "    Central Office: <a href=\"tel:02036371700\">02036371700</a><br />\n" +
                        "    OOH Phone Number: <a href=\"tel:07860865333\">07860865333</a><br /><br /><br />\n" +
                        "    <b>Head Office Address: </b><br />\n" +
                        "    Arch View House Building 1<br />\n" +
                        "    The Mirage Centre, First Way <br />\n" +
                        "    Wembley, HA9 0JD<br />\n" +
                        "</p>", "text/html", "UTF-8")
      /*          binding.noDataLL.visibility = View.VISIBLE
                binding.webViewEmContact.visibility = View.GONE*/
            }
        }
    }
}
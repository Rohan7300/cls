package com.clebs.celerity.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.ActivityPolicyDocsBinding
import com.clebs.celerity.models.requests.CompanySignedDocX
import com.clebs.celerity.models.requests.DriverHireAgreementX
import com.clebs.celerity.models.requests.UpdateDriverAgreementSignatureRequest
import com.clebs.celerity.models.response.GetDriverSignatureInformationResponse
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.CustDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.SignatureListener
import com.clebs.celerity.utils.bitmapToBase64
import com.clebs.celerity.utils.convertImageFileToBase64
import com.clebs.celerity.utils.showToast

class PolicyDocsActivity : AppCompatActivity() {
    lateinit var mbinding: ActivityPolicyDocsBinding
    lateinit var viewModel: MainViewModel
    private var dataLoaded  = false
    private var driverSignatureInfo:GetDriverSignatureInformationResponse? = null
    var userId = 0

    companion object {
        var path = Path()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mbinding = DataBindingUtil.setContentView(this, R.layout.activity_policy_docs)
        if (!Prefs.getInstance(App.instance).getBoolean("IsamazonSign", false)) {
            mbinding.llAmazon.visibility = View.GONE
        } else {
            mbinding.llAmazon.visibility = View.VISIBLE
        }

        if (!Prefs.getInstance(App.instance).getBoolean("isother", false)) {
            mbinding.llTrucks.visibility = View.GONE
        } else {
            mbinding.llAmazon.visibility = View.VISIBLE
        }

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]

        viewModel.liveDataGetDriverSignatureInformation.observe(this){
            if(it!=null){
                driverSignatureInfo = it
            }
        }
        userId =  Prefs.getInstance(this).userID.toInt()

        viewModel.GetDriverSignatureInformation(userId)

        mbinding.checkbox.addOnCheckedStateChangedListener { checkBox, _ ->
            if (checkBox.isChecked) {
                if (mbinding.llTrucks.visibility == View.GONE) {
                    showAlert()
                } else {
                    if (!mbinding.checkbox2.isChecked) {
                        Toast.makeText(
                            this,
                            "Please check the trucks agreement to proceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        showAlert()
                    }
                }
            }
        }
        mbinding.checkbox2.addOnCheckedStateChangedListener { checkBox, _ ->
            if (checkBox.isChecked) {
                if (mbinding.llAmazon.visibility == View.GONE) {
                    showAlert()
                } else {
                    if (!mbinding.checkbox.isChecked) {
                        Toast.makeText(
                            this,
                            "Please check the amazon agreement to proceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }

    private fun showAlert() {
        val dialog = CustDialog()
        dialog.setSignatureListener(object :SignatureListener{
            override fun onSignatureSaved(bitmap: Bitmap) {
                Log.d("Sign","Bitmap $bitmap")
                updateSignatureInfoApi(bitmap)
            }

        })
        dialog.show(supportFragmentManager, "sign")
    }

    private fun updateSignatureInfoApi(bitmap: Bitmap) {
        viewModel.livedataupdateDriverAgreementSignature.observe(this){
            if(it!=null){
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
        if(driverSignatureInfo!=null){
           
            
            val companyDocIds = driverSignatureInfo!!.OtherCompanyDocuments.flatMap { company ->
                company.DocumentList.map { document ->
                    document.CompanyDocId
                }
            }
            val companyIDS  = arrayListOf<CompanySignedDocX>()
                driverSignatureInfo!!.OtherCompanyDocuments.map {
                companyIDS.add(CompanySignedDocX(it.CompanyID,it.DocumentList.map { docs->docs.CompanyDocId }))
            }

            val driverVanHire = DriverHireAgreementX(
                Accidents = false,
                HireAgrDOB = "2024-02-27T11:58:30.668Z",
                VehType = "HR",
                AgrLicenceNo = "HR9897896",
                AgrLicenceStartDate = "2024-02-27T11:58:30.668Z",
                DaHireLicenceEndDate = "2024-02-27T11:58:30.668Z",
                Conviction = true,
                Comments = "",
                Address = "",
                Signature = "",
                UserID = 0,
            )

            viewModel.UpdateDriverAgreementSignature(UpdateDriverAgreementSignatureRequest(
                Address = driverSignatureInfo!!.PreviousAddress,
                CompanyDocId = companyDocIds,
                CompanySignedDocs = companyIDS,
                DriverHireAgreement = driverVanHire,
                HasAgreement = true,
                IsDAVanHireChecked = driverSignatureInfo!!.DAVanHireSectionReq,
                IsDAHandbookChecked = driverSignatureInfo!!.DAHandbookSectionReq,
                IsGDPRChecked = driverSignatureInfo!!.GDPRSectionReq,
                IsSLAChecked = driverSignatureInfo!!.SLASectionReq,
                Signature =  bitmapToBase64(bitmap),
                UserID = userId,
                IsAmazonSignatureUpdated = driverSignatureInfo!!.isAmazonSignatureReq,
                IsDAEngagementChecked = driverSignatureInfo!!.DAEngagementSectionReq
            ))

        }else{
            showToast("Pls Wait!!",this)
        }


    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        Toast.makeText(
            applicationContext,
            "Please sign the policy documents to continue",
            Toast.LENGTH_SHORT
        ).show()
    }
}
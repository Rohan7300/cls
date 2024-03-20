package com.clebs.celerity.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Path
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.isVisible
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
import com.clebs.celerity.utils.LoadingDialog
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.SignatureListener
import com.clebs.celerity.utils.bitmapToBase64
import com.clebs.celerity.utils.showToast

class PolicyDocsActivity : AppCompatActivity() {
    lateinit var mbinding: ActivityPolicyDocsBinding
    lateinit var viewModel: MainViewModel
    private var driverSignatureInfo: GetDriverSignatureInformationResponse? = null
    private var userId = 0
    lateinit var loadingDialog: LoadingDialog


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
        loadingDialog = LoadingDialog(this)
        if (!Prefs.getInstance(App.instance).getBoolean("isother", false)) {
            mbinding.llTrucks.visibility = View.GONE
        } else {
            mbinding.llAmazon.visibility = View.VISIBLE
        }

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        val mainRepo = MainRepo(apiService)
        viewModel = ViewModelProvider(this, MyViewModelFactory(mainRepo))[MainViewModel::class.java]

        viewModel.liveDataGetDriverSignatureInformation.observe(this) {
            if (it != null) {
                driverSignatureInfo = it
            }
        }
        userId = Prefs.getInstance(this).userID.toInt()

        viewModel.GetDriverSignatureInformation(userId)

        mbinding.amazonHeader.setOnClickListener {

                setVisibility(mbinding.amazonLayout, !mbinding.amazonLayout.isVisible)

        }

        mbinding.truckHeaderLL.setOnClickListener {

                setVisibility(mbinding.truckLayout,!mbinding.truckLayout.isVisible)

        }

        mbinding.checkbox.addOnCheckedStateChangedListener { checkBox, _ ->
            if (checkBox.isChecked) {
                mbinding.amazonLayout.visibility = View.GONE
                if (mbinding.llTrucks.visibility == View.GONE) {
                    showAlert()
                } else {
                    if (!mbinding.checkbox2.isChecked) {
                        showToast("Please check the trucks agreement to proceed", this)
                    } else {
                        showAlert()
                    }
                }
            } else {
                mbinding.amazonLayout.visibility = View.VISIBLE
            }
        }
        mbinding.checkbox2.addOnCheckedStateChangedListener { checkBox, _ ->
            if (checkBox.isChecked) {
                mbinding.truckLayout.visibility = View.GONE
                if (mbinding.llAmazon.visibility == View.GONE) {
                    showAlert()
                } else {
                    if (!mbinding.checkbox.isChecked) {
                        showToast("Please check the amazon agreement to proceed", this)
                    } else {
                        showAlert()
                    }
                }
            } else {
                mbinding.truckLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun showAlert() {
        // mbinding.llAmazon.visibility = View.GONE
        // mbinding.llTrucks.visibility = View.GONE

        //mbinding.scanll.visibility = View.VISIBLE

        val dialog = CustDialog()
        dialog.setSignatureListener(object : SignatureListener {
            override fun onSignatureSaved(bitmap: Bitmap) {
                Log.d("Sign", "Bitmap $bitmap")
                /*  progressBarVisibility(true,mbinding.policyDocPB,mbinding.overlayViewPolicyActivity)*/
                loadingDialog.show()
                updateSignatureInfoApi(bitmap)
            }
        })
        dialog.isCancelable = false
        dialog.show(supportFragmentManager, "sign")
    }

    private fun updateSignatureInfoApi(bitmap: Bitmap) {
        viewModel.livedataupdateDriverAgreementSignature.observe(this) {
            /*progressBarVisibility(false,mbinding.policyDocPB,mbinding.overlayViewPolicyActivity)*/
            loadingDialog.cancel()
            if (it != null) {
                if (it.Status == "200") {
                    Prefs.getInstance(applicationContext)
                        .saveBoolean("isSignatureReq", false)
                }
                val intent = Intent(this, HomeActivity::class.java)

                startActivity(intent)

            }
        }
        if (driverSignatureInfo != null) {


            val companyDocIds = driverSignatureInfo!!.OtherCompanyDocuments?.flatMap { company ->
                company.DocumentList.map { document ->
                    document.CompanyDocId
                }
            } ?: emptyList<Int>()
            val companyIDS = arrayListOf<CompanySignedDocX>()
            driverSignatureInfo!!.OtherCompanyDocuments?.map {
                companyIDS.add(
                    CompanySignedDocX(
                        it.CompanyID,
                        it.DocumentList.map { docs -> docs.CompanyDocId })
                )
            } ?: emptyArray<CompanySignedDocX>()

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
            val bse64 = "data:image/png;base64," + bitmapToBase64(bitmap)
            Log.d("Base64", bse64)
            viewModel.UpdateDriverAgreementSignature(
                UpdateDriverAgreementSignatureRequest(
                    Address = driverSignatureInfo!!.PreviousAddress,//GRTTOwer
                    CompanyDocId = companyDocIds,//[]
                    CompanySignedDocs = companyIDS,//[]
                    DriverHireAgreement = driverVanHire,
                    HasAgreement = true,
                    IsDAVanHireChecked = driverSignatureInfo!!.DAVanHireSectionReq,
                    IsDAHandbookChecked = driverSignatureInfo!!.DAHandbookSectionReq,
                    IsGDPRChecked = driverSignatureInfo!!.GDPRSectionReq,
                    IsSLAChecked = driverSignatureInfo!!.SLASectionReq,
                    Signature = bse64,
                    UserID = userId,
                    IsAmazonSignatureUpdated = driverSignatureInfo!!.isAmazonSignatureReq,
                    IsDAEngagementChecked = driverSignatureInfo!!.DAEngagementSectionReq
                )
            )

        } else {
            showToast("Pls Wait!!", this)
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


    /* fun progressBarVisibility(show: Boolean) {
         if (show) {
             mbinding.policyDocPB.bringToFront()
             mbinding.policyDocPB.visibility = View.VISIBLE
         } else {
             mbinding.policyDocPB.visibility = View.GONE
         }
     }*/

    private fun setVisibility(ll: LinearLayout, visibility: Boolean) {
        if (visibility)
            ll.visibility = View.VISIBLE
        else
            ll.visibility = View.GONE
    }
}
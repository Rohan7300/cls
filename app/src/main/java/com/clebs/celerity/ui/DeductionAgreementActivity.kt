package com.clebs.celerity.ui

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.DrawViewClass
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.DeducationAgreementBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.models.requests.SaveTicketDataRequestBody
import com.clebs.celerity.models.requests.UpdateDeductioRequest
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.bitmapToBase64
import com.clebs.celerity.utils.getCurrentDateTime
import com.clebs.celerity.utils.showToast

class DeductionAgreementActivity : AppCompatActivity() {
    lateinit var binding: DeducationAgreementBinding
    lateinit var viewmodel: MainViewModel
    lateinit var repo: MainRepo
    lateinit var pref: Prefs
    lateinit var loadingDialog: LoadingDialog

    private var DaDedAggrDaId = 0
    private var DaUserName = " "
    var isEnabled = false
    var type = 0
    private var FromLocation = " "
    private var PaymentKey = " "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DeducationAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        repo = MainRepo(apiService)
        pref = Prefs(this)
        viewmodel =
            ViewModelProvider(this, MyViewModelFactory(repo))[MainViewModel::class.java]
        loadingDialog = LoadingDialog(this)

        viewmodel.liveDataDeductionAgreement.observe(this) {
            if (it != null) {
                isEnabled = true
                binding.daUserName.text = it.DaUserName
                binding.tvTotalAmount.text = it.TotalAdvanceAmt.toString()
                binding.tvWeeklyAmount.text = it.WeeklyDeductionAmt.toString()
                binding.tvDateTime.text = it.AgreementDate
                binding.tvText.text = it.DeductionComment
                DaDedAggrDaId = it.DaDedAggrId
                DaUserName = it.DaUserName
                FromLocation = it.FromLocation
                PaymentKey = it.PaymentKey
            } else {
                isEnabled = false
                showToast("Failed to fetch data!! Pls try again", this)
                onBackPressed()
            }
        }
        viewmodel.GetDeductionAgreement(pref.clebUserId.toInt())
        binding.disputeSection.visibility = View.GONE
        binding.signSection.visibility = View.GONE
        binding.rbAcceptClose.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                type=0
                if (isEnabled) {
                    binding.rbAcceptDispute.isChecked = false
                    binding.deductionTXT.visibility = View.GONE
                    binding.dedctionContent.visibility = View.GONE
                    binding.disputeSection.visibility = View.GONE
                    binding.signSection.visibility = View.VISIBLE
                } else {
                    showToast("Failed to fetch data!! Pls try again", this)
                }
            }

        }
        binding.rbAcceptDispute.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (isEnabled) {
                    type=1
                    binding.rbAcceptClose.isChecked = false
                    binding.dedctionContent.visibility = View.GONE
                    binding.deductionTXT.visibility = View.GONE
                    binding.disputeSection.visibility = View.VISIBLE
                    binding.signSection.visibility = View.VISIBLE
                } else {
                    showToast("Failed to fetch data!! Pls try again", this)
                }
            }
        }

        /*        binding.saveNdispute.setOnClickListener {
                    disputeCondition()
                }*/

        binding.backIcon.setOnClickListener {
            onBackPressed()
        }

        val retry = binding.signSV.RetryLay
        val save = binding.signSV.sv
        val testIV = binding.signSV.textIV
        val drawView = binding.signSV.paintView.drawView

        save.setOnClickListener {
            if (DrawViewClass.pathList.isEmpty()) {
                showToast("Please sign before saving", this)
            } else {
                val signatureBitmap: Bitmap = drawView.getBitmap()
                testIV.setImageBitmap(signatureBitmap)

                loadingDialog.show()
                if (type == 0)
                    submitCondition(signatureBitmap)
                if (type == 1)
                    disputeCondition(signatureBitmap)
            }
        }
        retry.setOnClickListener {
            drawView.clearSignature()
        }

    }

    private fun submitCondition(signatureBitmap: Bitmap) {
        loadingDialog.show()
        val bse64 = "data:image/png;base64," + bitmapToBase64(signatureBitmap)
        viewmodel.UpdateDaDeduction(
            UpdateDeductioRequest(
                DaDedAggrDaId = 197251,
                DaUserName = DaUserName,
                FromLocation = FromLocation,
                IsDaDedAggAccepted = true,
                PaymentKey = PaymentKey,
                RejectionComment = "null",
                Signature = bse64
            )
        )

        viewmodel.liveDataUpdateDeducton.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                startActivity(Intent(this, HomeActivity::class.java))
            }
        }
    }

    private fun disputeCondition(signatureBitmap: Bitmap) {
        val bse64 = "data:image/png;base64," + bitmapToBase64(signatureBitmap)
        if (binding.etDisputeDis.text.isNullOrEmpty()) {
            showToast("Please add dispute reason", this)
        } else {
            var disputeComment = binding.etDisputeDis.text.toString() ?: " "
            loadingDialog.show()
            viewmodel.UpdateDaDeduction(
                UpdateDeductioRequest(
                    DaDedAggrDaId = pref.clebUserId.toInt(),
                    DaUserName = DaUserName,
                    FromLocation = FromLocation,
                    IsDaDedAggAccepted = false,
                    PaymentKey = PaymentKey,
                    RejectionComment = disputeComment,
                    Signature = bse64
                )
            )

            generateTicketInBackground(disputeComment)

            viewmodel.liveDataUpdateDeducton.observe(this) {
                loadingDialog.dismiss()
                if (it != null) {
                    onBackPressed()
                }
            }
        }
    }

    private fun generateTicketInBackground(RejectionComment: Any?) {
        var currDt = getCurrentDateTime()
        val request = SaveTicketDataRequestBody(
            AssignedToUserIDs = listOf(),
            BadgeComment = "undefined",
            BadgeReturnedStatusId = 0,
            DaTestDate = currDt,
            DaTestTime = currDt,
            Description = RejectionComment.toString(),
            DriverId = pref.clebUserId.toInt(),
            EstCompletionDate = currDt,
            KeepDeptInLoop = true,
            NoofPeople = 0,
            ParentCompanyID = 0,
            PriorityId = 0,
            RequestTypeId = 17,
            TicketDepartmentId = 1,
            TicketId = 0,
            TicketUTRNo = "undefined",
            Title = RejectionComment.toString(),
            UserStatusId = 0,
            UserTicketRegNo = "undefined",
            VmId = 0,
            WorkingOrder = 0
        )
        viewmodel.SaveTicketData(
            pref.clebUserId.toInt(),
            request
        )
    }

}
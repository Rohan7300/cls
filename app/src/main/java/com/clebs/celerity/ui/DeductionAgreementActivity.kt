package com.clebs.celerity.ui

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.DeducationAgreementBinding
import com.clebs.celerity.dialogs.CustDialogDeduction
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.models.requests.SaveTicketDataRequestBody
import com.clebs.celerity.models.requests.UpdateDeductioRequest
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo
import com.clebs.celerity.utils.DeductionSignatureListener
import com.clebs.celerity.utils.DependencyProvider
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.bitmapToBase64
import com.clebs.celerity.utils.convertDateFormat
import com.clebs.celerity.utils.getCurrentDateTime
import com.clebs.celerity.utils.noInternetCheck
import com.clebs.celerity.utils.parseToInt
import com.clebs.celerity.utils.showToast

class DeductionAgreementActivity : AppCompatActivity() {
    lateinit var binding: DeducationAgreementBinding
    lateinit var viewmodel: MainViewModel
    lateinit var repo: MainRepo
    lateinit var pref: Prefs
    lateinit var loadingDialog: LoadingDialog
    var actionID = 0
    private var DaDedAggrDaId = 0
    private var DaUserName = " "
    var isEnabled = false
    private var notificationID = 0
    var type = 0
    private var FromLocation = " "
    private var PaymentKey = " "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DeducationAgreementBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionID = intent.getIntExtra("actionID", 0)
        Log.d("ActionID", "$actionID")
        val apiService = RetrofitService.getInstance().create(ApiService::class.java)
        repo = MainRepo(apiService)
        notificationID = intent.getIntExtra("notificationID", 0)
        pref = Prefs(this)
        viewmodel =
            ViewModelProvider(this, MyViewModelFactory(repo))[MainViewModel::class.java]
        loadingDialog = LoadingDialog(this)
        loadingDialog.show()
        noInternetCheck(this, binding.nointernetLL, this)
        DependencyProvider.handlingDeductionNotification = true
        viewmodel.liveDataDeductionAgreement.observe(this) {
            loadingDialog.dismiss()
            if (it != null) {
                isEnabled = true
                binding.daUserName.text = it.DaUserName
                binding.tvTotalAmount.text = it.TotalAdvanceAmt.toString()
                binding.tvWeeklyAmount.text = it.WeeklyDeductionAmt.toString()
                binding.tvDateTime.text = convertDateFormat(it.AgreementDate)
                binding.tvText.text = it.DeductionComment
                DaDedAggrDaId = it.DaDedAggrId
                DaUserName = it.DaUserName
                FromLocation = it.FromLocation
                PaymentKey = it.PaymentKey
            } else {
                isEnabled = false
                showToast("Failed to fetch data!! Pls try again", this)
                viewmodel.MarkNotificationAsRead(notificationID)
                finish()
            }
        }
        viewmodel.GetDeductionAgreement(pref.clebUserId.toInt(), actionID!!.toInt())
//        try {
        Log.d("NOTIFICATIONID", "$actionID  $notificationID")
        val notificationIdX = try {
            parseToInt(actionID.toString()) ?: 0
        } catch (_: Exception) {
            notificationID
        }
        try {

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationIdX)
            notificationManager.cancel(notificationID)
        } catch (_: Exception) {

        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showToast(
                    "Please submit the Deduction Agreement first!!",
                    this@DeductionAgreementActivity
                )
            }
        })

        binding.rbAcceptClose.setOnClickListener {
            if (isEnabled) {
                type = 0
                binding.rbAcceptDispute.isChecked = false
                binding.rbAcceptClose.isChecked = true
                showSignDialog(type)
            } else {
                showToast("Failed to fetch data!! Pls try again", this)
            }
        }

        binding.acceptncloseMainLL.setOnClickListener {
            if (isEnabled) {
                type = 0
                binding.rbAcceptDispute.isChecked = false
                binding.rbAcceptClose.isChecked = true
                showSignDialog(type)
            } else {
                showToast("Failed to fetch data!! Pls try again", this)
            }
        }

        binding.rbAcceptDispute.setOnClickListener {
            if (isEnabled) {
                type = 1
                binding.rbAcceptClose.isChecked = false
                binding.rbAcceptDispute.isChecked = true
                showSignDialog(type)
            } else {
                showToast("Failed to fetch data!! Pls try again", this)
            }
        }

        binding.AcceptnDisputemainLL.setOnClickListener {
            if (isEnabled) {
                type = 1
                binding.rbAcceptClose.isChecked = false
                binding.rbAcceptDispute.isChecked = true
                showSignDialog(type)
            } else {
                showToast("Failed to fetch data!! Pls try again", this)
            }
        }

        /*        binding.backIcon.setOnClickListener {
                    finish()
                }*/

        /*   val retry = binding.signSV.RetryLay
           val save = binding.signSV.sv
           val testIV = binding.signSV.textIV
           val drawView = binding.signSV.paintView.drawView
           if (DrawViewClass.pathList.isNotEmpty())
               drawView.clearSignature()

           save.setOnClickListener {
               if (DrawViewClass.pathList.isEmpty()) {
                   showToast("Please sign before saving", this)
               } else {
                   val signatureBitmap: Bitmap = drawView.getBitmap()
                   testIV.setImageBitmap(signatureBitmap)

                   loadingDialog.show()

               }
           }
           retry.setOnClickListener {
               drawView.clearSignature()
           }*/

    }

    private fun submitCondition(signatureBitmap: Bitmap) {
        loadingDialog.show()
        val bse64 = "data:image/png;base64," + bitmapToBase64(signatureBitmap)
        viewmodel.UpdateDaDeduction(
            UpdateDeductioRequest(
                DaDedAggrDaId = pref.clebUserId.toInt(),
                DaUserName = DaUserName,
                FromLocation = FromLocation,
                IsDaDedAggAccepted = true,
                PaymentKey = PaymentKey,
                DisputeComment = null,
                Signature = bse64
            )
        )

        viewmodel.liveDataUpdateDeducton.observe(this) {
            loadingDialog.dismiss()
            viewmodel.MarkNotificationAsRead(notificationID)
            finish()
            if (it != null) {

            } else {
                showToast("Something went wrong!!", this)
            }
        }
    }

    private fun disputeCondition(signatureBitmap: Bitmap, disputeDesciption: String?) {
        val bse64 = "data:image/png;base64," + bitmapToBase64(signatureBitmap)
        if (disputeDesciption.isNullOrEmpty()) {
            showToast("Please add dispute reason", this)
        } else {
            var disputeComment = binding.etDisputeDis.text.toString() ?: " "
            loadingDialog.show()
            viewmodel.UpdateDaDeduction(
                UpdateDeductioRequest(
                    DaDedAggrDaId = pref.clebUserId.toInt(),
                    DaUserName = DaUserName,
                    FromLocation = FromLocation,
                    IsDaDedAggAccepted = true,
                    PaymentKey = PaymentKey,
                    DisputeComment = disputeDesciption ?: "Disputed",
                    Signature = bse64
                )
            )

            generateTicketInBackground(disputeDesciption)

            viewmodel.liveDataUpdateDeducton.observe(this) {
                viewmodel.MarkNotificationAsRead(notificationID)
                loadingDialog.dismiss()
                finish()
                if (it != null) {
                    //onBackPressed()
                } else {
                    showToast("Something went wrong!!", this)
                }
            }
        }
    }

    private fun generateTicketInBackground(RejectionComment: String) {
        var currDt = getCurrentDateTime()
        val request = SaveTicketDataRequestBody(
            AssignedToUserIDs = listOf(),
            BadgeComment = "",
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
            //RequestTypeId = 17,
            RequestTypeId = 21,
            //TicketDepartmentId = 1,
            TicketDepartmentId = 2,
            TicketId = 0,
            TicketUTRNo = " ",
            Title = "Agreement Dispute [Aggr: $actionID]",
            UserStatusId = 0,
            UserTicketRegNo = "",
            VmId = 0,
            WorkingOrder = 0
        )
        viewmodel.SaveTicketData(
            pref.clebUserId.toInt(),
            actionID,
            request
        )
    }

    private fun showSignDialog(type: Int) {
        val dialog = CustDialogDeduction(type)
        dialog.setSignatureListener(object : DeductionSignatureListener {
            override fun onDeductionSignatureSaved(bitmap: Bitmap, disputeDesciption: String?) {
                if (type == 0)
                    submitCondition(bitmap)
                if (type == 1)
                    disputeCondition(bitmap, disputeDesciption)
            }
        })

        dialog.show(supportFragmentManager, "sign")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        showToast(
            "Please submit the Deduction Agreement first!!",
            this@DeductionAgreementActivity
        )
    }

}
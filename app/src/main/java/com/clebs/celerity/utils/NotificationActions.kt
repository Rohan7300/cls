package com.clebs.celerity.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.DailyrotaapprovaldialogBinding
import com.clebs.celerity.databinding.DialogvehicleadvancepaymentBinding
import com.clebs.celerity.dialogs.ExpiredDocDialog
import com.clebs.celerity.dialogs.InvoiceReadytoViewDialog
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.models.requests.ApproveDaDailyRotaRequest
import com.clebs.celerity.models.response.NotificationResponseItem
import com.clebs.celerity.ui.DeductionAgreementActivity
import com.clebs.celerity.ui.ExpiringDocumentsActivity
import com.clebs.celerity.ui.WeeklyRotaApprovalActivity

fun deductions(context: Context, notificationActionId: Int, notificationId: Int) {
    val intent = Intent(context, DeductionAgreementActivity::class.java)
    intent.putExtra("actionID", notificationActionId)
    intent.putExtra("notificationID", notificationId)
    context.startActivity(intent)
}

fun showDailyRotaDialog(
    notificationId: Int,
    rotaName: String,
    rotaDay: String,
    rotaWeek: Int,
    rotaYear: Int,
    rotaLocation: String,
    dailyRotatoken: String,
    context: Context,
    viewModel: MainViewModel,
    viewLifecycleOwner: LifecycleOwner,
) {
    val pref = Prefs.getInstance(context)
    val loadingDialog = LoadingDialog(context)
    val dailyRotaDialog = AlertDialog.Builder(context).create()
    val dailyRotaDialogBinding =
        DailyrotaapprovaldialogBinding.inflate(LayoutInflater.from(context))
    dailyRotaDialog.setView(dailyRotaDialogBinding.root)
    dailyRotaDialog.setCanceledOnTouchOutside(true)
    dailyRotaDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    dailyRotaDialogBinding.rotaname.text = rotaName
    dailyRotaDialogBinding.rotaday.text = rotaDay
    dailyRotaDialogBinding.rotaweek.text = rotaWeek.toString()
    dailyRotaDialogBinding.rotayear.text = rotaYear.toString()
    dailyRotaDialogBinding.rotalocation.text = rotaLocation
    var acceptRBChecked = false
    var rejectRNcheck = false
    var selectedItem = 0
    dailyRotaDialogBinding.acceptRB.setOnClickListener {
        if (!acceptRBChecked) {
            dailyRotaDialogBinding.acceptRB.isChecked = true
            dailyRotaDialogBinding.rejectRB.isChecked = false
            acceptRBChecked = true
            selectedItem = 1
        } else {
            selectedItem = 0
            acceptRBChecked = false
            dailyRotaDialogBinding.acceptRB.isChecked = false
            dailyRotaDialogBinding.rejectRB.isChecked = false
        }
    }

    dailyRotaDialogBinding.rejectRB.setOnClickListener {
        if (!rejectRNcheck) {
            dailyRotaDialogBinding.acceptRB.isChecked = false
            dailyRotaDialogBinding.rejectRB.isChecked = true
            rejectRNcheck = true
            selectedItem = 2
        } else {
            dailyRotaDialogBinding.acceptRB.isChecked = false
            dailyRotaDialogBinding.rejectRB.isChecked = false
            rejectRNcheck = false
            selectedItem = 0
        }
    }

    dailyRotaDialogBinding.submit.setOnClickListener {
        if (selectedItem == 0) {
            showToast("Please select first!!", context)
        } else {
            var comment = ""
            var isApproved = false
            if (selectedItem == 1) {
                comment = ""
                isApproved = true
            } else {
                comment = "Rejected "
                isApproved = false
            }

            loadingDialog.show()
            viewModel.ApproveDailyRotabyDA(
                ApproveDaDailyRotaRequest(
                    Comment = comment,
                    IsApproved = isApproved,
                    Token = dailyRotatoken,
                    UserId = pref.clebUserId.toInt()
                )
            )
            viewModel.liveDataApproveDailyRotabyDA.observe(viewLifecycleOwner) {
                loadingDialog.dismiss()
                dailyRotaDialog.dismiss()
                viewModel.MarkNotificationAsRead(notificationId)
                if (it != null) {
                    showToast("Submitted Successfully!!", context)
                } else {
                    showToast("Failed to submit!!", context)
                }
            }
        }
    }

    if (!dailyRotaDialog.isShowing)
        dailyRotaDialog.show()
}

fun dailyRota(
    viewModel: MainViewModel,
    dailyRotatoken: String,
    viewLifecycleOwner: LifecycleOwner,
    context: Context,
    notificationId: Int
) {
    val pref = Prefs.getInstance(context)
    viewModel.GetDaDailyLocationRota(pref.clebUserId.toInt(), dailyRotatoken)
    val loadingDialog = LoadingDialog(context)
    loadingDialog.show()
    viewModel.liveDataDaDailyLocationRota.observe(viewLifecycleOwner) {
        loadingDialog.dismiss()
        if (it != null) {
            showDailyRotaDialog(
                notificationId.toInt(),
                it.DriverName,
                it.DayOfWeek,
                it.WeekNo,
                it.YearNo,
                it.LocationName,
                dailyRotatoken,
                context,
                viewModel,
                viewLifecycleOwner
            )
        } else {
            showToast("Failed to fetch Data!!", context)
        }
    }
}

fun invoiceReadyToView(notificationId: Int, fragmentManager: FragmentManager) {
    val dialog = InvoiceReadytoViewDialog.newInstance(
        getCurrentWeek().toString(),
        getCurrentYear().toString(),
        notificationId
    )
    dialog.showDialog(fragmentManager)
}

fun weeklyLocationRota(context: Context, notificationId: Int, notificationActionId: Int) {
    val intent = Intent(context, WeeklyRotaApprovalActivity::class.java)
    intent.putExtra("actionID", notificationActionId)
    intent.putExtra("notificationID", notificationId)
    context.startActivity(intent)
}

fun expiredDocuments(
    viewModel: MainViewModel,
    viewLifecycleOwner: LifecycleOwner,
    context: Context,
    fragmentManager: FragmentManager,
    notificationId: Int
) {
    val loadingDialog = LoadingDialog(context)
    val pref = Prefs.getInstance(context)
    loadingDialog.show()
    viewModel.GetDAVehicleExpiredDocuments(pref.clebUserId.toInt())
    viewModel.liveDataGetDAVehicleExpiredDocuments.observe(viewLifecycleOwner) {
        loadingDialog.dismiss()
        val dialog = ExpiredDocDialog(pref, context)

        if (it != null) {
            pref.saveExpiredDocuments(it)
            dialog.showDialog(fragmentManager)
            viewModel.MarkNotificationAsRead(notificationId)
        } else {
            showToast("No expired document founds", context)
            //dialog.showDialog(fragmentManager)
        }
    }
}

fun showAdvancePaymentDialog(
    amount: String,
    date: String,
    comment: String,
    notificationId: Int,
    context: Context,
    viewModel: MainViewModel,
    viewLifecycleOwner: LifecycleOwner
) {
    val pref = Prefs.getInstance(context)
    val loadingDialog = LoadingDialog(context)
    val advancePaymentDialog = AlertDialog.Builder(context).create()
    val advancePaymentBinding =
        DialogvehicleadvancepaymentBinding.inflate(LayoutInflater.from(context))
    advancePaymentDialog.setView(advancePaymentBinding.root)
    advancePaymentDialog.setCanceledOnTouchOutside(false)
    advancePaymentDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    advancePaymentBinding.agreementAmount.text = amount
    advancePaymentBinding.agreementDate.text = date
    advancePaymentBinding.tvText.text = comment

    advancePaymentDialog.show()

    advancePaymentBinding.approve.setOnClickListener {
        advancePaymentDialog.dismiss()
        advancePaymentDialog.cancel()
        loadingDialog.show()
        viewModel.MarkNotificationAsRead(notificationId)
        viewModel.ApproveVehicleAdvancePaymentAgreement(pref.clebUserId.toInt(), true)
        viewModel.liveDataApproveVehicleAdvancePaymentAgreement.observe(viewLifecycleOwner) {
            loadingDialog.dismiss()
            if (it != null) {
                showToast("Approved✔✔", context)
            } else {
                showToast("Failed to approve!!", context)
            }
        }
    }
}

fun vehicleAdvancePaymentAgreement(
    context: Context,
    notificationId: Int,
    viewModel: MainViewModel,
    viewLifecycleOwner: LifecycleOwner
) {
    val pref = Prefs.getInstance(context)
    val loadingDialog = LoadingDialog(context)
    loadingDialog.show()
    viewModel.GetVehicleAdvancePaymentAgreement(pref.clebUserId.toInt())
    viewModel.liveDataGetAdvancePaymentAgreement.observe(viewLifecycleOwner) {
        loadingDialog.dismiss()
        if (it != null) {
            val amount = it.VehAdvancePaymentAgreementAmount ?: "null"
            val date = it.AgreementDate ?: "null"
            val comment = it.VehicleAdvancePaymentContent ?: "null"

            showAdvancePaymentDialog(
                amount.toString(),
                date.toString(),
                comment.toString(),
                notificationId,
                context,
                viewModel,
                viewLifecycleOwner
            )
        } else {
            showToast("Failed to fetch data", context)
        }
    }
}

fun expiringDocument(
    context: Context,
    notificationId: Int
) {
    val intent = Intent(context, ExpiringDocumentsActivity::class.java)
    intent.putExtra("notificationID", notificationId)
    context.startActivity(intent)
}

fun parseToInt(value: String): Int {
    return try {
        val floatValue = value.toFloat() // Convert to float first
        floatValue.toInt()
    } catch (e: NumberFormatException) {
        // Handle if the string cannot be converted to float or integer
        0 // Default value in case of error
    }
}




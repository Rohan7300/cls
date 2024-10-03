package com.clebs.celerity.utils

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.clebs.celerity.ui.DeductionAgreementActivity
import com.clebs.celerity.ui.ExpiringDocumentsActivity
import com.clebs.celerity.ui.HomeActivity
import com.clebs.celerity.ui.VehicleExpiringDocuments
import com.clebs.celerity.ui.ViewTicketsActivity
import com.clebs.celerity.ui.WeeklyRotaApprovalActivity
import com.clebs.celerity.utils.DependencyProvider.dailyRotaNotificationShowing
import com.clebs.celerity.utils.DependencyProvider.handlingDeductionNotification
import com.clebs.celerity.utils.DependencyProvider.handlingExpiredDialogNotification
import com.clebs.celerity.utils.DependencyProvider.handlingRotaNotification

fun deductions(context: Context, notificationActionId: Int, notificationId: Int) {
    handlingDeductionNotification = true
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
    var dailyRotaDialog = AlertDialog.Builder(context).create()
    var dailyRotaDialogBinding =
        DailyrotaapprovaldialogBinding.inflate(LayoutInflater.from(context))
    dailyRotaDialog.setView(dailyRotaDialogBinding.root)
    dailyRotaDialog.setCanceledOnTouchOutside(true)
    dailyRotaDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    if (dailyRotaDialog == null && dailyRotaDialogBinding == null) {
        dailyRotaDialog = AlertDialog.Builder(context).create()
        dailyRotaDialogBinding =
            DailyrotaapprovaldialogBinding.inflate(LayoutInflater.from(context))
        dailyRotaDialog!!.setView(dailyRotaDialogBinding!!.root)
        dailyRotaDialog!!.setCanceledOnTouchOutside(true)
        dailyRotaDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    } else {
        dailyRotaDialogBinding!!.acceptRB.isChecked = false
        dailyRotaDialogBinding!!.rejectRB.isChecked = false
    }

    dailyRotaDialogBinding!!.rotaname.text = rotaName
    dailyRotaDialogBinding!!.rotaday.text = rotaDay
    dailyRotaDialogBinding!!.rotaweek.text = rotaWeek.toString()
    dailyRotaDialogBinding!!.rotayear.text = rotaYear.toString()
    dailyRotaDialogBinding!!.rotalocation.text = rotaLocation
    if (!dailyRotaDialog!!.isShowing)
        dailyRotaDialog!!.show()
    var selectedItem = 0

    dailyRotaDialogBinding!!.acceptRB.setOnClickListener {
        dailyRotaDialogBinding!!.acceptRB.isChecked = true
        dailyRotaDialogBinding!!.rejectRB.isChecked = false
        selectedItem = 1
    }

    dailyRotaDialogBinding!!.rejectRB.setOnClickListener {
        dailyRotaDialogBinding!!.acceptRB.isChecked = false
        dailyRotaDialogBinding!!.rejectRB.isChecked = true
        selectedItem = 2
    }
    dailyRotaDialog!!.setOnCancelListener {
        dailyRotaNotificationShowing = false
    }

    dailyRotaDialogBinding!!.submit.setOnClickListener {
        dailyRotaNotificationShowing = false
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
                dailyRotaDialog!!.dismiss()
                viewModel.MarkNotificationAsRead(notificationId)
                if (it != null) {
                    showToast("Submitted Successfully!!", context)
                } else {
                    showToast("Failed to submit!!", context)
                }
            }
        }
    }
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
            if (!dailyRotaNotificationShowing) {
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
                dailyRotaNotificationShowing = true
            }

        } else {
            viewModel.MarkNotificationAsRead(notificationId)
            showToast("Daily Rota not found!!", context)
        }
    }
}

fun invoiceReadyToView(
    notificationId: Int,
    fragmentManager: FragmentManager,
    notificationBody: String
) {

    val dialog = InvoiceReadytoViewDialog.newInstance(
        getCurrentWeek().toString(),
        getCurrentYear().toString(),
        notificationId,
        notificationBody
    )
    dialog.showDialog(fragmentManager)
}

fun weeklyLocationRota(context: Context, notificationId: Int, notificationActionId: Int) {
    handlingRotaNotification = true
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
    handlingExpiredDialogNotification = true
    val loadingDialog = LoadingDialog(context)
    val pref = Prefs.getInstance(context)
    loadingDialog.show()
    viewModel.GetDAVehicleExpiredDocuments(pref.clebUserId.toInt())
    viewModel.liveDataGetDAVehicleExpiredDocuments.observe(viewLifecycleOwner) {
        loadingDialog.dismiss()
        val dialog = ExpiredDocDialog()
        viewModel.MarkNotificationAsRead(notificationId)
        if (it != null) {
            pref.saveExpiredDocuments(it)
            dialog.showDialog(fragmentManager)
        } else {
            showToast("No expired document founds", context)
        }
    }
}

fun breakDown(viewModel: MainViewModel, prefs: Prefs) {
    viewModel.GetVehBreakDownInspectionInfobyDriver(prefs.clebUserId.toInt())
}

fun handleTicketNotification(
    viewModel: MainViewModel,
    ticketId: Int,
    viewLifecycleOwner: LifecycleOwner,
    context: Context,
    prefs: Prefs,
    notificationId: Int
) {
    val loadingDialog = LoadingDialog(context)
    viewModel.GetTicketInfoById(ticketId).observe(viewLifecycleOwner){
        loadingDialog.dismiss()
        if(it!=null){
            prefs.saveCurrentTicket(it.Docs[0])
            val intent = Intent(context, ViewTicketsActivity::class.java)
            context.startActivity(intent)
        }else{
            showToast("Ticket not found for this TicketID",context)
        }
        viewModel.MarkNotificationAsRead(notificationId)
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


fun vehicleExpiringDocuments(
    context: Context,
    notificationId: Int
) {
    val intent = Intent(context, VehicleExpiringDocuments::class.java)
    intent.putExtra("notificationID", notificationId)
    context.startActivity(intent)
}

fun thirdPartyAcessRequest(context: Context, notificationId: Int) {
    val intent = Intent(context, HomeActivity::class.java)
    intent.putExtra("destinationFragment", "ThirdPartyAcess")
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




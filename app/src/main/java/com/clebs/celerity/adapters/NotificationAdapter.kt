package com.clebs.celerity.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.DailyrotaapprovaldialogBinding
import com.clebs.celerity.databinding.DialogvehicleadvancepaymentBinding
import com.clebs.celerity.databinding.NotificationAdapterDialogBinding
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.dialogs.VehicleAdvancePaymentDialog
import com.clebs.celerity.models.requests.ApproveDaDailyRotaRequest
import com.clebs.celerity.models.response.NotificationResponseItem
import com.clebs.celerity.utils.DependencyProvider.dailyRotaNotificationShowing
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.breakDown
import com.clebs.celerity.utils.deductions
import com.clebs.celerity.utils.expiredDocuments
import com.clebs.celerity.utils.expiringDocument
import com.clebs.celerity.utils.invoiceReadyToView
import com.clebs.celerity.utils.showToast
import com.clebs.celerity.utils.vehicleExpiringDocuments
import com.clebs.celerity.utils.weeklyLocationRota
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

interface NotificationAdapterCallback {
    fun refresh()
}

class NotificationAdapter(
    var navController: NavController,
    var fragmentManager: FragmentManager,
    var context: Context,
    var loadingDialog: LoadingDialog,
    var viewModel: MainViewModel,
    var pref: Prefs,
    var viewLifecycleOwner: LifecycleOwner,
    var callback: NotificationAdapterCallback
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private var dailyRotaDialog: AlertDialog? = null
    private var dailyRotaDialogBinding: DailyrotaapprovaldialogBinding? = null

    private val diffUtil = object : DiffUtil.ItemCallback<NotificationResponseItem>() {
        override fun areItemsTheSame(
            oldItem: NotificationResponseItem,
            newItem: NotificationResponseItem
        ): Boolean {
            return oldItem.NotificationId == newItem.NotificationId && oldItem.NotificationBody == newItem.NotificationBody
        }

        override fun areContentsTheSame(
            oldItem: NotificationResponseItem,
            newItem: NotificationResponseItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val asyncListDiffer = AsyncListDiffer(this, diffUtil)
    fun saveData(data: List<NotificationResponseItem>) {
        asyncListDiffer.submitList(data)
    }

    inner class NotificationViewHolder(val binding: NotificationAdapterDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationResponseItem) {
            val dailyRotatoken = item.NotificationUrl.replace(" ", "")

            when (item.NotificationTitle) {
                "UserExpiringDocuments" -> binding.title.setText("User Expiring Documents")
                "VehicleExpiringDocuments" -> binding.title.setText("Vehicle Expiring Documents")
                "ExpiredDocuments" -> binding.title.setText("Expired Documents")
                "WeeklyRotaApproval" -> binding.title.setText("Weekly Rota Approval")
                "DailyRotaApproval" -> binding.title.setText("Daily Rota Approval")
                "InvoiceReadyToReview" -> binding.title.setText("Invoice Ready To Review")
                "DriverDeductionWithAgreement" -> binding.title.setText("Driver Deduction With Agreement")
                "ThirdPartyAccessRequestNotification" -> binding.title.setText("Third Party Access Request Notification")
                "VehicleBreakDownInspectionNotification" -> binding.title.setText("Complete BreakDown Vehicle Inspection")
            }
//            binding.title.text = item.NotificationTitle
            Log.e("jhdjsfhjfdjnotification", "bind: " + item.NotificationTitle)

            binding.descripotionX.text = item.NotificationBody

            if (item.ActionToPerform == "Deductions" ||
                item.ActionToPerform == "Driver Deduction with Agreement" ||
                item.ActionToPerform == "DriverDeductionWithAgreement" ||
                item.ActionToPerform == "Daily Location Rota" ||
                item.ActionToPerform == "Invoice Ready To Review" ||
                item.ActionToPerform == "Invoice Ready to Review" ||
                item.ActionToPerform == "InvoiceReadyToReview" ||
                item.ActionToPerform == "Weekly Location Rota" ||
                item.ActionToPerform == "Expired Document" ||
                item.ActionToPerform == "ExpiredDocuments" ||
                item.ActionToPerform == "Vehicle Advance Payment Aggrement" ||
                item.ActionToPerform == "Vehicle Advance Payment Agreement" ||
                item.ActionToPerform == "Expiring Document" ||
                item.ActionToPerform == "ExpiringDocuments" ||
                item.ActionToPerform == "Weekly Rota Approval" ||
                item.ActionToPerform == "WeeklyRotaApproval" ||
                item.ActionToPerform == "DailyRotaApproval" ||
                item.ActionToPerform == "ThirdPartyAccessRequestNotification" ||
                item.ActionToPerform == "VehicleExpiringDocuments" ||
                item.ActionToPerform == "UserExpiringDocuments" ||
                item.ActionToPerform == "Daily Rota Approval"
            ) {
                binding.notficationArrow.visibility = View.VISIBLE
            }

            var formattedDate = item.NotificationSentOn
            var formattedTime = "00:00"
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val truncatedString =
                    item.NotificationSentOn.substring(0, 19)
                val dateTime = LocalDateTime.parse(truncatedString, formatter)

                val outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH)
                formattedDate = dateTime.format(outputFormatter).toUpperCase()

                val outputTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
                formattedTime = dateTime.format(outputTimeFormatter)
                binding.time.text = "$formattedTime  ($formattedDate)"

            } catch (_: Exception) {
                binding.time.text = item.NotificationSentOn
            }


            binding.overallNotification.setOnClickListener {
                if (item.ActionToPerform == "Deductions" ||
                    item.ActionToPerform == "Driver Deduction with Agreement" ||
                    item.ActionToPerform == "DriverDeductionWithAgreement"
                ) {
                    deductions(context, item.NotificationActionId, item.NotificationId)
                    /*val intent = Intent(context, DeductionAgreementActivity::class.java)
                    intent.putExtra("actionID", item.NotificationActionId)
                    intent.putExtra("notificationID", item.NotificationId)
                    context.startActivity(intent)*/
                } else if (item.ActionToPerform.equals("Daily Location Rota") ||
                    item.ActionToPerform.equals("Daily Rota Approval") ||
                    item.ActionToPerform.equals("DailyRotaApproval")
                ) {
                    /*                  dailyRota(
                                          viewModel,
                                          dailyRotatoken,
                                          viewLifecycleOwner,
                                          context,
                                          item.NotificationId
                                      )*/

                    viewModel.GetDaDailyLocationRota(pref.clebUserId.toInt(), dailyRotatoken)
                    loadingDialog.show()
                    viewModel.liveDataDaDailyLocationRota.observe(viewLifecycleOwner) {
                        loadingDialog.dismiss()
                        if (it != null) {
                            if (!dailyRotaNotificationShowing) {
                                showDailyRotaDialog(
                                    item.NotificationId,
                                    it.DriverName,
                                    it.DayOfWeek,
                                    it.WeekNo,
                                    it.YearNo,
                                    it.LocationName,
                                    dailyRotatoken
                                )
                                dailyRotaNotificationShowing = true
                            }

                        } else {
                            viewModel.MarkNotificationAsRead(item.NotificationId)
                            //   callback.refresh()
                            showToast("Daily Rota not found!!", context)
                        }
                    }
                } else if (item.ActionToPerform.equals("Invoice Ready To Review")
                    || item.ActionToPerform.equals("Invoice Ready to Review") ||
                    item.ActionToPerform.equals("InvoiceReadyToReview")
                ) {
                    invoiceReadyToView(item.NotificationId, fragmentManager,item.NotificationBody)
                    /*            val dialog = InvoiceReadytoViewDialog.newInstance(
                                    getCurrentWeek().toString(),
                                    getCurrentYear().toString(),
                                    item.NotificationId
                                )
                                dialog.showDialog(fragmentManager)*/
                    //viewModel.MarkNotificationAsRead(item.NotificationId)
                } else if (item.ActionToPerform == "Weekly Location Rota" ||
                    item.ActionToPerform == "Weekly Rota Approval" ||
                    item.ActionToPerform.equals("WeeklyRotaApproval")
                ) {
                    weeklyLocationRota(context, item.NotificationId, item.NotificationActionId)
                    /*                   val intent = Intent(context, WeeklyRotaApprovalActivity::class.java)
                                       intent.putExtra("actionID", item.NotificationActionId)
                                       intent.putExtra("notificationID", item.NotificationId)
                                       context.startActivity(intent)*/
                } else if (
                    item.ActionToPerform == "Expired Document" ||
                    item.ActionToPerform == "ExpiredDocuments"
                ) {
                    expiredDocuments(
                        viewModel,
                        viewLifecycleOwner,
                        context,
                        fragmentManager,
                        item.NotificationId
                    )
                    /*                  loadingDialog.show()
                                      viewModel.GetDAVehicleExpiredDocuments(pref.clebUserId.toInt())
                                      viewModel.liveDataGetDAVehicleExpiredDocuments.observe(viewLifecycleOwner) {
                                          loadingDialog.dismiss()
                                          val dialog = ExpiredDocDialog(pref, context)
                                          viewModel.MarkNotificationAsRead(item.NotificationId)
                                          if (it != null) {
                                              pref.saveExpiredDocuments(it)
                                              dialog.showDialog(fragmentManager)
                                          } else {
                                              showToast("No expired document founds", context)
                                              //dialog.showDialog(fragmentManager)
                                          }
                                      }*/
                }
                else if (item.ActionToPerform.equals("Vehicle Advance Payment Aggrement") ||
                    item.ActionToPerform.equals("Vehicle Advance Payment Agreement")
                ) {
                    /*                    vehicleAdvancePaymentAgreement(
                                            context,item.NotificationId,viewModel,viewLifecycleOwner
                                        )*/

                    loadingDialog.show()
                    viewModel.GetVehicleAdvancePaymentAgreement(pref.clebUserId.toInt())
                    viewModel.liveDataGetAdvancePaymentAgreement.observe(viewLifecycleOwner) {
                        loadingDialog.dismiss()
                        if (it != null) {
                            val amount = it.VehAdvancePaymentAgreementAmount ?: "null"
                            val date = it.AgreementDate ?: "null"
                            val comment = it.VehicleAdvancePaymentContent ?: "null"
                            val dialog = VehicleAdvancePaymentDialog.newInstance(
                                amount.toString(),
                                date.toString()
                            )
                            showAdvancePaymentDialog(
                                amount.toString(),
                                date.toString(),
                                comment.toString(),
                                item.NotificationId
                            )

                            // dialog.showDialog(fragmentManager)
                        } else {
                            showToast("Failed to fetch data", context)
                        }
                    }
                }
                else if (item.ActionToPerform.equals("Expiring Document") ||
                    item.ActionToPerform.equals("UserExpiringDocuments")
                ) {
                    expiringDocument(context, item.NotificationId)
                    /*                val intent = Intent(context, ExpiringDocumentsActivity::class.java)
                                    intent.putExtra("notificationID", item.NotificationId)
                                    context.startActivity(intent)*/
                }
                else if (item.ActionToPerform == "VehicleExpiringDocuments") {
                    vehicleExpiringDocuments(context, item.NotificationId)
                }
                else if (item.ActionToPerform == "ThirdPartyAccessRequestNotification") {
                    viewModel.MarkNotificationAsRead(item.NotificationId)
                    navController.navigate(R.id.profileFragment)
                }
                else if(item.ActionToPerform =="VehicleBreakDownInspectionNotification"){
                    breakDown(viewModel,pref)
                }
                else {
                    //viewModel.MarkNotificationAsRead(item.NotificationId)
                    viewModel.MarkNotificationAsRead(item.NotificationId)
                    callback.refresh()
                    binding.notficationArrow.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = NotificationAdapterDialogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }


    override fun getItemCount(): Int {
        return asyncListDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = asyncListDiffer.currentList[position]
        holder.bind(item)
    }

    fun showAdvancePaymentDialog(
        amount: String,
        date: String,
        comment: String,
        notificationId: Int
    ) {
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
                callback.refresh()
                loadingDialog.dismiss()
                if (it != null) {
                    showToast("Approved✔✔", context)
                } else {
                    showToast("Failed to approve!!", context)
                }
            }
        }

        /*        uploadDialogBinding.cancel.setOnClickListener {
                    uploadDialog.dismiss()
                    uploadDialog.cancel()
                }*/

    }

    fun showDailyRotaDialog(
        notificationId: Int,
        rotaName: String,
        rotaDay: String,
        rotaWeek: Int,
        rotaYear: Int,
        rotaLocation: String,
        dailyRotatoken: String
    ) {
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
                    callback.refresh()
                    if (it != null) {
                        showToast("Submitted Successfully!!", context)
                    } else {
                        showToast("Failed to submit!!", context)
                    }
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}
package com.clebs.celerity.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.DialogvehicleadvancepaymentBinding
import com.clebs.celerity.databinding.NotificationAdapterDialogBinding
import com.clebs.celerity.databinding.UploadexpiringdocdialogBinding
import com.clebs.celerity.dialogs.DailyRotaApprovalDialog
import com.clebs.celerity.dialogs.ExpiredDocDialog
import com.clebs.celerity.dialogs.InvoiceReadytoViewDialog
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.dialogs.VehicleAdvancePaymentDialog
import com.clebs.celerity.models.response.NotificationResponseItem
import com.clebs.celerity.ui.DeductionAgreementActivity
import com.clebs.celerity.ui.ExpiringDocumentsActivity
import com.clebs.celerity.ui.WeeklyRotaApprovalActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.getCurrentWeek
import com.clebs.celerity.utils.getCurrentYear
import com.clebs.celerity.utils.showToast
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotificationAdapter(
    var navController: NavController,
    var fragmentManager: FragmentManager,
    var context: Context,
    var loadingDialog: LoadingDialog,
    var viewModel: MainViewModel,
    var pref: Prefs,
    var viewLifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

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
            binding.title.text = item.NotificationTitle
            binding.descripotionX.text = item.NotificationBody

            if (item.ActionToPerform == "Deductions" ||
                item.ActionToPerform == "Daily Location Rota" ||
                item.ActionToPerform == "Invoice Ready To Review" ||
                item.ActionToPerform == "Weekly Location Rota" ||
                item.ActionToPerform == "Expired Document" ||
                item.ActionToPerform == "Vehicle Advance Payment Aggrement" ||
                item.ActionToPerform == "Expiring Document" ||
                item.ActionToPerform == "Weekly Rota Approval"
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
                binding.time.text = "$formattedTime  $formattedDate"
                binding.notficationArrow.setOnClickListener {

                }
            } catch (_: Exception) {
                binding.time.text = item.NotificationSentOn
            }



            binding.notficationArrow.setOnClickListener {
                if (item.ActionToPerform == "Deductions") {
                    //navController.navigate(R.id.deductionFragment)
                    val intent = Intent(context, DeductionAgreementActivity::class.java)
                    context.startActivity(intent)
                } else if (item.ActionToPerform.equals("Daily Location Rota")) {

                    val dialog = DailyRotaApprovalDialog()
                    dialog.showDialog(fragmentManager)

                } else if (item.ActionToPerform.equals("Invoice Ready To Review")) {
                    val dialog = InvoiceReadytoViewDialog.newInstance(
                        getCurrentWeek().toString(),
                        getCurrentYear().toString()
                    )
                    dialog.showDialog(fragmentManager)
                    //navController.navigate(R.id.CLSInvoicesFragment)
                } else if (item.ActionToPerform.equals("Weekly Location Rota") || item.ActionToPerform == "Weekly Rota Approval") {
                    val intent = Intent(context, WeeklyRotaApprovalActivity::class.java)
                    context.startActivity(intent)
                } else if (item.ActionToPerform.equals("Expired Document")) {
                    loadingDialog.show()
                    viewModel.GetDAVehicleExpiredDocuments(pref.clebUserId.toInt())
                    viewModel.liveDataGetDAVehicleExpiredDocuments.observe(viewLifecycleOwner) {
                        loadingDialog.dismiss()
                        val dialog = ExpiredDocDialog(pref, context)
                        if (it != null) {
                            pref.saveExpiredDocuments(it)
                            dialog.showDialog(fragmentManager)
                        } else {
                            dialog.showDialog(fragmentManager)
                        }
                    }
                } else if (item.ActionToPerform.equals("Vehicle Advance Payment Aggrement")) {
                    loadingDialog.show()
                    viewModel.GetVehicleAdvancePaymentAgreement(pref.clebUserId.toInt())
                    viewModel.liveDataGetAdvancePaymentAgreement.observe(viewLifecycleOwner) {
                        loadingDialog.dismiss()
                        if (it != null) {
                            val amount = it.VehAdvancePaymentAgreementAmount ?: "null"
                            val date = it.AgreementDate ?: "null"
                            val comment = it.VehicleAdvancePaymentContent?:"null"
     /*                       val dialog = VehicleAdvancePaymentDialog.newInstance(
                                amount.toString(),
                                date.toString()
                            )*/
                            showAdvancePaymentDialog(amount.toString(), date.toString(),comment.toString())
                           // dialog.showDialog(fragmentManager)
                        } else {
                            showToast("Failed to fetch data", context)
                        }
                    }
                } else if (item.ActionToPerform.equals("Expiring Document")) {
                    val intent = Intent(context, ExpiringDocumentsActivity::class.java)
                    context.startActivity(intent)
                } else {
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

    fun showAdvancePaymentDialog(amount: String, date: String,comment:String) {
        val advancePaymentDialog = AlertDialog.Builder(context).create()
        val advancePaymentBinding =
            DialogvehicleadvancepaymentBinding.inflate(LayoutInflater.from(context))
        advancePaymentDialog.setView(advancePaymentBinding.root)
        advancePaymentDialog.setCanceledOnTouchOutside(false)
        advancePaymentDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        advancePaymentBinding.agreementAmount.text = amount
        advancePaymentBinding.agreementDate.text =  date
        advancePaymentBinding.tvText.text =comment

        advancePaymentDialog.show()

        advancePaymentBinding.approve.setOnClickListener {
            advancePaymentDialog.dismiss()
            advancePaymentDialog.cancel()
            loadingDialog.show()
            viewModel.ApproveVehicleAdvancePaymentAgreement(pref.clebUserId.toInt(), true)
            viewModel.liveDataApproveVehicleAdvancePaymentAgreement.observe(viewLifecycleOwner) {
                loadingDialog.dismiss()
                if (it != null) {
                    showToast("Approved✔✔",context)
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
}
package com.clebs.celerity.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.DeductionAgreement
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.databinding.NotificationAdapterDialogBinding
import com.clebs.celerity.dialogs.DailyRotaApprovalDialog
import com.clebs.celerity.dialogs.ExpiredDocDialog
import com.clebs.celerity.dialogs.LoadingDialog
import com.clebs.celerity.dialogs.VehicleAdvancePaymentDialog
import com.clebs.celerity.models.response.NotificationResponseItem
import com.clebs.celerity.ui.DeductionAgreementActivity
import com.clebs.celerity.ui.ExpiringDocumentsActivity
import com.clebs.celerity.ui.WeeklyRotaApprovalActivity
import com.clebs.celerity.utils.Prefs
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

                if (item.ActionToPerform.equals("Deductions")) {
                    //navController.navigate(R.id.deductionFragment)
                    val intent = Intent(context, DeductionAgreementActivity::class.java)
                    context.startActivity(intent)
                }

                if (item.ActionToPerform.equals("Daily Location Rota")) {
                    val dialog = DailyRotaApprovalDialog()
                    dialog.showDialog(fragmentManager)
                }

                if (item.ActionToPerform.equals("Invoice Ready To Review")) {
                    navController.navigate(R.id.CLSInvoicesFragment)
                }

                if (item.ActionToPerform.equals("Weekly Location Rota")) {
                    val intent = Intent(context, WeeklyRotaApprovalActivity::class.java)
                    context.startActivity(intent)
                }

                if (item.ActionToPerform.equals("Expired Document")) {
                    val dialog = ExpiredDocDialog()
                    loadingDialog.show()
                    viewModel.GetDAVehicleExpiredDocuments(pref.clebUserId.toInt())
                    viewModel.liveDataGetDAVehicleExpiredDocuments.observe(viewLifecycleOwner){
                        loadingDialog.show()
                        if(it!=null){
                            pref.saveExpiredDocuments(it)
                            dialog.init(pref)
                            dialog.showDialog(fragmentManager)
                        }
                    }

                }

                if (item.ActionToPerform.equals("Vehicle Advance Payment Aggrement")) {
                    loadingDialog.show()

                    viewModel.GetVehicleAdvancePaymentAgreement(pref.clebUserId.toInt())
                    viewModel.liveDataGetAdvancePaymentAgreement.observe(viewLifecycleOwner) {
                        loadingDialog.dismiss()
                        if (it != null) {
                            val amount = it.VehAdvancePaymentAgreementAmount ?: "amount"
                            val date = it.AgreementDate ?: "date"
                            val dialog = VehicleAdvancePaymentDialog.newInstance(
                                amount.toString(),
                                date.toString()
                            )
                            dialog.showDialog(fragmentManager)
                        } else {
                            showToast("Failed to fetch data", context)
                        }
                    }
                }

                if (item.ActionToPerform.equals("Expiring Document")) {
                    val intent = Intent(context, ExpiringDocumentsActivity::class.java)
                    context.startActivity(intent)
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
}
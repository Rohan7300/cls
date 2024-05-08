package com.clebs.celerity.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import com.clebs.celerity.R
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.utils.Prefs

class VehicleAdvancePaymentDialog() : DialogFragment() {

    companion object {
        const val TAG = "VehicleAdvancePaymentDialog"
        const val AMOUNT = "amount"
        const val DATE = "date"

        fun newInstance(amount: String, date: String): VehicleAdvancePaymentDialog {
            val dialog = VehicleAdvancePaymentDialog()
            val args = Bundle()
            args.putString(AMOUNT, amount)
            args.putString(DATE, date)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val amount = arguments?.getString(AMOUNT) ?: "amount"
        val date = arguments?.getString(DATE) ?: "date"

        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialogvehicleadvancepayment)
        val amountTV = dialog.findViewById<TextView>(R.id.agreementAmount)
        val dateTV = dialog.findViewById<TextView>(R.id.agreementDate)

        amountTV.text = amount
        dateTV.text = date

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        return dialog
    }

    fun showDialog(fragmentManager: FragmentManager) {
        if (!isVisible)
            show(fragmentManager, TAG)
    }

    fun hideDialog() {
        if (dialog != null)
            if (dialog!!.isShowing)
                dismiss()
    }
}
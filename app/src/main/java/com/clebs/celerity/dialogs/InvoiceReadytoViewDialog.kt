package com.clebs.celerity.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.clebs.celerity.R
import com.clebs.celerity.ui.HomeActivity

class InvoiceReadytoViewDialog() : DialogFragment() {

    companion object {
        const val TAG = "InvoiceReadyToView"
        const val WEEK = "week"
        const val YEAR = "year"
        const val NOTID = "notificaionID"
        const val NOTIBODY = "notificationBody"

        fun newInstance(week: String, year: String, notificationID: Int, notificationBody: String): InvoiceReadytoViewDialog {
            val dialog = InvoiceReadytoViewDialog()
            val args = Bundle()
            args.putString(WEEK, week)
            args.putString(YEAR, year)
            args.putInt(NOTID, notificationID)
            args.putString(NOTIBODY,notificationBody)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val week = arguments?.getString(WEEK) ?: "week"
        val year = arguments?.getString(YEAR) ?: "year"
        val notificationID = arguments?.getInt(NOTID) ?: 0
        val notificationBody = arguments?.getString(NOTIBODY)?:"Your Invoice is ready to view. Click the button below"

        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dialoginvoiceready)
        val viewInvoiceBtn = dialog.findViewById<Button>(R.id.viewinvoicebtn)
        val weekyearTV = dialog.findViewById<TextView>(R.id.weekYearText)
        weekyearTV.text = notificationBody
        viewInvoiceBtn.setOnClickListener {
            (activity as HomeActivity).viewModel
                .MarkNotificationAsRead(notificationID)
            dialog.dismiss()
            findNavController().navigate(R.id.CLSInvoicesFragment)
        }

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
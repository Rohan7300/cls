package com.clebs.celerity.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.R

class DailyRotaApprovalDialog() : DialogFragment() {

    companion object {
        const val TAG = "DailyRotaApprovalDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.dailyrotaapprovaldialog)
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
package com.clebs.celerity.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.R
import com.clebs.celerity.models.response.GetDAVehicleExpiredDocumentsResponse
import com.clebs.celerity.utils.Prefs

class ExpiredDocDialog() : DialogFragment() {

    lateinit var prefs: Prefs
    var data: GetDAVehicleExpiredDocumentsResponse? = null

    companion object {
        const val TAG = "ExpiredDocuments"


    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.expired_doc_dialog)
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

    fun init(prefs: Prefs) {
        this.prefs = prefs
        if (!prefs.getExpiredDocuments().isNullOrEmpty())
            data = prefs.getExpiredDocuments()!!
    }
}
package com.clebs.celerity.utils

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.clebs.celerity.R

class NoInternetDialog : DialogFragment() {
    companion object {
        const val TAG = "NoInternetDialogFragment"
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it,R.style.CustomDialog)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_no_internet, null)
            val tryAgain = view.findViewById<CardView>(R.id.noIntTryAgain)
            tryAgain.setOnClickListener {
                dialog?.dismiss()
            }
            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
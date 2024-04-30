package com.clebs.celerity.utils

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.clebs.celerity.R


interface BackgroundUploadDialogListener{
    fun onSaveClick()
}
class BackgroundUploadDialog :DialogFragment(){
    companion object{
        const val TAG = "BackgroundUploadDialog"
    }
    private var listener: BackgroundUploadDialogListener? = null

    fun setListener(listener: BackgroundUploadDialogListener) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.CustomDialog)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.layout_background, null)
            val tryAgain = view.findViewById<CardView>(R.id.noIntTryAgain)



            tryAgain.setOnClickListener {
                listener?.onSaveClick()
                dialog?.dismiss()
            }


            val dialog = builder.setView(view).create()
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
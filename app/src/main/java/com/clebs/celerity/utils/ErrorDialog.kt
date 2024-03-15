package com.clebs.celerity.utils

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.clebs.celerity.R

class ErrorDialog:DialogFragment() {
    companion object {
        const val TAG = "ErrorDialogFragment"
        private const val ARG_MSG = "msg"
        private const val ARG_CODE = "code"

        fun newInstance(msg:String,code:String):ErrorDialog{
            val dialog = ErrorDialog()
            val args = Bundle()
            args.putString(ARG_MSG,msg)
            args.putString(ARG_CODE,code)
            dialog.arguments = args
            return  dialog
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val code = arguments?.getString(ARG_CODE)?:"Unknown"
        val msg = arguments?.getString(ARG_MSG)?:"Unknown Error"
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.CustomDialog)

            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_error, null)
            val tryAgain = view.findViewById<CardView>(R.id.noIntTryAgain)
            val codeTV = view.findViewById<TextView>(R.id.errorCode)
            val msgTV = view.findViewById<TextView>(R.id.errorMsg)

            codeTV.text = code
            msgTV.text = msg

            tryAgain.setOnClickListener {
                dialog?.dismiss()
            }

            val dialog = builder.setView(view).create()
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
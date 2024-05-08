package com.clebs.celerity.utils

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.R

interface InspectionIncompleteListener{
    fun onButtonClick()
}

class InspectionIncompleteDialog() : DialogFragment() {
    companion object {
        const val TAG = "NoInternetDialog"
    }

    private var listener:InspectionIncompleteListener? = null
    fun setListener(listener: InspectionIncompleteListener){
        this.listener = listener
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it,R.style.CustomDialog)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.inspection_incomplete_dialog, null)
            val continueInspectionCV = view.findViewById<CardView>(R.id.continueInspectionCV)
            continueInspectionCV.setOnClickListener {
                listener?.onButtonClick()
                dismiss()
            }
            isCancelable = false
            builder.setView(view).create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun showDialog(fragmentManager: FragmentManager){
        if(!isVisible){
            show(fragmentManager, TAG)
        }
    }
    fun hideDialog(){
        if(dialog!=null){
            if(dialog!!.isShowing){
                dismiss()
            }
        }
    }
}
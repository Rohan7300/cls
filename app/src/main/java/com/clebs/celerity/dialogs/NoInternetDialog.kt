package com.clebs.celerity.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.R

class NoInternetDialog() : DialogFragment() {
    companion object {
        const val TAG = "NoInternetDialog"
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it,R.style.CustomDialog)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.dialog_no_internet, null)
            val cross = view.findViewById<ImageView>(R.id.cross)
            cross.setOnClickListener {
                dismiss()
            }
            isCancelable = true
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
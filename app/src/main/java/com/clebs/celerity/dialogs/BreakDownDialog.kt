package com.clebs.celerity.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.R

class BreakDownDialog :DialogFragment(){

    private lateinit var dialogContext: Context
    companion object{
        const val TAG = "BreakdownDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.break_down_dialog)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(R.color.semi_transparent_color)
        }
        return dialog
    }

    fun showDialog(fragmentManager: FragmentManager){
        val fragment = fragmentManager.findFragmentByTag(TAG)
        if(!isVisible&&fragment==null)
            show(fragmentManager,TAG)
    }

    fun hideDialog(){
    }
}
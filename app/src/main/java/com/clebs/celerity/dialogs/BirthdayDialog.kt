package com.clebs.celerity.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.adapters.ExpiredDocAdapter
import com.clebs.celerity.models.response.GetDAVehicleExpiredDocumentsResponse
import com.clebs.celerity.utils.Prefs
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BirthdayDialog(val prefs: Prefs) : DialogFragment() {


    companion object {
        const val TAG = "BirthDayDialog"


    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.birthdayalert)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(R.color.semi_transparent_color)
        }
        val fab: FloatingActionButton = dialog.findViewById(R.id.fab)
        fab.setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.birthdaytvMain).text =
            "Happy Birthday :${prefs.userName}"
        return dialog
    }

    fun showDialog(fragmentManager: FragmentManager) {
        prefs.isBirthdayCardShown = true

        val fragment = fragmentManager.findFragmentByTag(TAG)
        if (!isVisible && fragment == null) {
            show(fragmentManager, TAG)
        }
    }

    fun hideDialog() {
        if (dialog != null)
            if (dialog!!.isShowing)
                dismiss()
    }

}
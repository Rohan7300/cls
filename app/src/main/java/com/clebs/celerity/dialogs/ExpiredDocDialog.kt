package com.clebs.celerity.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.adapters.ExpiredDocAdapter
import com.clebs.celerity.models.response.GetDAVehicleExpiredDocumentsResponse
import com.clebs.celerity.utils.Prefs

class ExpiredDocDialog(val prefs: Prefs,val rvContext: Context) : DialogFragment() {

    var data: GetDAVehicleExpiredDocumentsResponse? = null
    lateinit var expiredRV: RecyclerView

    companion object {
        const val TAG = "ExpiredDocuments"


    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.expired_doc_dialog)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(R.color.semi_transparent_color)
        }
        expiredRV = dialog.findViewById(R.id.expiredRV)
        init()
        return dialog
    }

    fun showDialog(fragmentManager: FragmentManager) {
        val fragment = fragmentManager.findFragmentByTag(TAG)
        if (!isVisible && fragment == null){
            show(fragmentManager, TAG)
        }
    }

    fun hideDialog() {
        if (dialog != null)
            if (dialog!!.isShowing)
                dismiss()
    }

    fun init() {
        if (!prefs.getExpiredDocuments().isNullOrEmpty()) {
            data = prefs.getExpiredDocuments()!!
            val adapter = ExpiredDocAdapter()
            adapter.saveData(data!!)
            expiredRV.adapter = adapter
            expiredRV.layoutManager = LinearLayoutManager(rvContext)
        }
    }
}
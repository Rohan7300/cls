package com.clebs.celerity.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clebs.celerity.R
import com.clebs.celerity.adapters.ExpiredDocAdapter
import com.clebs.celerity.models.response.GetDAVehicleExpiredDocumentsResponse
import com.clebs.celerity.ui.CreateTicketsActivity
import com.clebs.celerity.utils.DependencyProvider.blockCreateTicket
import com.clebs.celerity.utils.DependencyProvider.isComingToRaiseTicketforExpiredDocs
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.ShowToast
import com.clebs.celerity.utils.showToast
import com.google.android.material.button.MaterialButton

class ExpiredDocDialog: DialogFragment() {

    private lateinit var rvContext: Context
    var data: GetDAVehicleExpiredDocumentsResponse? = null
    private lateinit var expiredRV: RecyclerView
    lateinit var prefs:Prefs

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
        val raiseTicketBtn = dialog.findViewById<MaterialButton>(R.id.raiseTicket)

/*        if(prefs.isTicketRaisedToday()){
            raiseTicketBtn.isEnabled = false
        }*/

        prefs = Prefs(rvContext)
        raiseTicketBtn.setOnClickListener {
            if(prefs.isTicketRaisedToday()){
                showToast("Ticket has been already raised. Please retry after 24 hours.",rvContext)
            }else{
                blockCreateTicket = true
                isComingToRaiseTicketforExpiredDocs = true
                rvContext.startActivity(Intent(rvContext, CreateTicketsActivity::class.java))
            }
        }
        expiredRV = dialog.findViewById(R.id.expiredRV)
        init()
        return dialog
    }

    fun showDialog(fragmentManager: FragmentManager) {
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

    fun init() {
        if (!prefs.getExpiredDocuments().isNullOrEmpty()) {
            data = prefs.getExpiredDocuments()!!
            val adapter = ExpiredDocAdapter()
            adapter.saveData(data!!)
            expiredRV.adapter = adapter
            expiredRV.layoutManager = LinearLayoutManager(rvContext)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        rvContext = context
    }
}
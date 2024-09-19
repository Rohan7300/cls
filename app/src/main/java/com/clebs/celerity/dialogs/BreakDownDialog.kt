package com.clebs.celerity.dialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.clebs.celerity.R
import com.clebs.celerity.ui.BreakDownInspectionActivity
import com.clebs.celerity.utils.Prefs
import com.clebs.celerity.utils.showToast
import com.google.android.material.button.MaterialButton

class BreakDownDialog : DialogFragment() {

    private lateinit var dialogContext: Context

    companion object {
        const val TAG = "BreakdownDialog"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.break_down_dialog)
        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawableResource(R.color.semi_transparent_color)
        }
        dialog.findViewById<MaterialButton>(R.id.raiseTicket).setOnClickListener {
            if (Prefs.getInstance(dialogContext).isBreakDownImagesAreUploading && !Prefs.getInstance(
                    dialogContext
                ).isBreakDownInspectionAllowed()
            ) {
                showToast("Please wait!! Last Inspection Images are Uploading.", dialogContext)
            } else {
                dialogContext.startActivity(
                    Intent(
                        dialogContext,
                        BreakDownInspectionActivity::class.java
                    )
                )
                hideDialog()
            }
        }
        return dialog
    }

    fun showDialog(fragmentManager: FragmentManager) {
        val fragment = fragmentManager.findFragmentByTag(TAG)
        if (!isVisible && fragment == null)
            show(fragmentManager, TAG)
    }

    fun hideDialog() {
        if (dialog != null) {
            if (dialog!!.isShowing)
                dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialogContext = context
    }
}
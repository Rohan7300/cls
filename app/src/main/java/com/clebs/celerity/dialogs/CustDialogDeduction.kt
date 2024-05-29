package com.clebs.celerity.dialogs

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.clebs.celerity.DrawViewClass
import com.clebs.celerity.DrawViewClass.Companion.pathList
import com.clebs.celerity.R
import com.clebs.celerity.utils.DeductionSignatureListener
import com.clebs.celerity.utils.SignatureListener
import com.clebs.celerity.utils.showToast

class CustDialogDeduction(var type: Int) : DialogFragment() {
    private lateinit var drawView: DrawViewClass
    private var signatureListener: DeductionSignatureListener? = null


    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams = dialog?.window!!.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog?.window?.attributes = params as WindowManager.LayoutParams
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corners)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.deduction_signaturedialog, container, false)
        val retry = rootView.findViewById<RelativeLayout>(R.id.RetryLay)
        val close = rootView.findViewById<TextView>(R.id.cl)
        val save = rootView.findViewById<AppCompatButton>(R.id.sv)
        drawView = rootView.findViewById(R.id.drawView)
        drawView.clearSignature()
        val disputeSection = rootView.findViewById<LinearLayout>(R.id.disputeSectionDialog)
        val etDisputeSection = rootView.findViewById<EditText>(R.id.et_dispute_dis_dialog)
        if (type != 1)
            disputeSection.visibility = View.GONE
        save.setOnClickListener {
            if (type == 1 && etDisputeSection.text.isNullOrEmpty()) {
                showToast("Please add comment & signature before saving", requireContext())
            } else {
                if (pathList.isEmpty()) {
                    showToast("Please sign before saving", requireContext())
                } else {
                    val signatureBitmap: Bitmap = drawView.getBitmap()
                    if (type == 1)
                        signatureListener?.onDeductionSignatureSaved(
                            signatureBitmap,
                            etDisputeSection.text.toString()
                        )
                    else
                        signatureListener?.onDeductionSignatureSaved(
                            signatureBitmap,
                            null
                        )
                    dismiss()
                }
            }
        }

        retry.setOnClickListener {
            clearSignature()
        }

        close.setOnClickListener { dismiss() }
        return rootView
    }

    fun setSignatureListener(listener: DeductionSignatureListener) {
        signatureListener = listener
    }

    fun clearSignature() {
        if (pathList.isNotEmpty())
            drawView.clearSignature()
    }
}
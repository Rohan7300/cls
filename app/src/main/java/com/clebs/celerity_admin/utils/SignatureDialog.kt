package com.clebs.celerity_admin.utils

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import com.clebs.celerity_admin.R
import com.clebs.celerity_admin.utils.DrawViewClass.Companion.pathList

class SignatureDialog : DialogFragment() {
    private lateinit var drawView: DrawViewClass
    private var signatureListener: SignatureDialogListener? = null


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
    ): View {
        val rootView: View = inflater.inflate(R.layout.signature_dialog, container, false)
        val retry = rootView.findViewById<RelativeLayout>(R.id.RetryLay)
        val close = rootView.findViewById<TextView>(R.id.cl)
        val save = rootView.findViewById<AppCompatButton>(R.id.sv)
        drawView = rootView.findViewById(R.id.drawView)
        drawView.clearSignature()
        save.setOnClickListener {
            if (pathList.isEmpty()) {
                showToast("Please sign before saving", requireContext())
            } else {
                val signatureBitmap: Bitmap = drawView.getBitmap()
                signatureListener?.onSignatureSaved(
                    signatureBitmap
                )
                dismiss()
            }
        }

        retry.setOnClickListener {
            clearSignature()
        }

        close.setOnClickListener { dismiss() }
        return rootView
    }

    fun setSignatureListener(listener: SignatureDialogListener) {
        signatureListener = listener
    }

    private fun clearSignature() {
        if (pathList.isNotEmpty())
            drawView.clearSignature()
    }
}
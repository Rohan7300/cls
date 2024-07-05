package com.clebs.celerity_admin.dialogs

import android.app.Dialog
import android.content.Context
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import com.clebs.celerity_admin.R


class LoadingDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.custom_progress_dialog)
        window?.setBackgroundDrawableResource(R.color.transparent)

        window?.statusBarColor = ContextCompat.getColor(context, R.color.very_very_light_red)
        setCancelable(false)
        setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                dismiss()
                true
            } else {
                false
            }
        }


    }

    override fun show() {
        if (!isShowing)
            super.show()
    }

    override fun dismiss() {
        if (isShowing)
            super.dismiss()
    }
}
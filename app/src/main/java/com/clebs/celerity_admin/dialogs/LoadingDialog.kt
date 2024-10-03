package com.clebs.celerity_admin.dialogs

import android.app.Dialog
import android.content.Context
import android.view.KeyEvent
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.clebs.celerity_admin.R


class LoadingDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.custom_progress_dialog)
        val imageView: ImageView = findViewById<ImageView>(R.id.img_animx)

        Glide.with(context).load(R.raw.celerity_loader).into(imageView)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        window?.statusBarColor = ContextCompat.getColor(context, R.color.medium_orange)
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
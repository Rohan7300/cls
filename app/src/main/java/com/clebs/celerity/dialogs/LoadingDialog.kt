package com.clebs.celerity.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.clebs.celerity.R

class LoadingDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.custom_progress_dialog)
        val imageView: ImageView = findViewById<ImageView>(R.id.img_anim)

        Glide.with(context).load(R.raw.loader).into(imageView)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        window?.statusBarColor = ContextCompat.getColor(context, R.color.medium_orange)
        setCancelable(false)


        setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                // Handle back press
                dismiss()
                true
            } else {
                false
            }
        }
//        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        // Set the system UI visibility

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
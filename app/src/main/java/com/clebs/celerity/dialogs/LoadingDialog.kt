package com.clebs.celerity.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.clebs.celerity.R

class LoadingDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.custom_progress_dialog)

        val imageView: ImageView = findViewById<ImageView>(R.id.img_anim)

        Glide.with(context).load(R.raw.celerity_loader).into(imageView)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        window?.setStatusBarColor(context.resources.getColor(R.color.medium_orange))
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

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
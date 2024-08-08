package com.clebs.celerity.utils

import android.content.Context
import android.widget.Toast


class ShowToast {
    private var toast: Toast? = null

    fun show(mcontext: Context?, text: String?) {
        if (toast != null)
            toast!!.cancel()
        toast = Toast.makeText(mcontext, text, Toast.LENGTH_SHORT)
        try{
            toast?.show()
        }catch (_:Exception){

        }

    }
}
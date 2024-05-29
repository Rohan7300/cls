package com.clebs.celerity.utils

import android.graphics.Bitmap

interface SignatureListener {
    fun onSignatureSaved(bitmap: Bitmap)
}
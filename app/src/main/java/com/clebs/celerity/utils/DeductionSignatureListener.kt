package com.clebs.celerity.utils

import android.graphics.Bitmap

interface DeductionSignatureListener {
    fun onDeductionSignatureSaved(bitmap: Bitmap,disputeDesciption:String?)
}
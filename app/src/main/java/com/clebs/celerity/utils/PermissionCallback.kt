package com.clebs.celerity.utils

interface PermissionCallback {
    fun requestStoragePermission()
    fun onStoragePermissionResult(granted: Boolean)

    fun dowloadPDF(invoiceID:Int,fileName:String)
}
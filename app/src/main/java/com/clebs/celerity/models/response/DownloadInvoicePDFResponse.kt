package com.clebs.celerity.models.response

data class DownloadInvoicePDFResponse(
    val Invoices: List<Invoice>,
    val Message: String,
    val Status: String
)
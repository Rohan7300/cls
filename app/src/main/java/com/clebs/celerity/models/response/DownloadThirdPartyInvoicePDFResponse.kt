package com.clebs.celerity.models.response

data class DownloadThirdPartyInvoicePDFResponse(
    val Invoices: List<InvoiceX>,
    val Message: String,
    val Status: String
)
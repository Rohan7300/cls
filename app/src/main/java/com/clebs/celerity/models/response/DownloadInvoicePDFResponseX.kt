package com.clebs.celerity.models.response

data class DownloadInvoicePDFResponseX(
    val Invoices: List<InvoiceXXX>,
    val Message: String,
    val Status: String
)
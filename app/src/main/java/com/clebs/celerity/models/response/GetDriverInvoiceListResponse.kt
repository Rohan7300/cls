package com.clebs.celerity.models.response

data class GetDriverInvoiceListResponse(
    val Invoices: List<InvoiceXX>,
    val Message: String,
    val Status: String
)
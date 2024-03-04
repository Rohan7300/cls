package com.clebs.celerity.models.response

data class OtherCompanyDocument(
    val CompanyID: Int,
    val CompanyName: String,
    val DocumentList: List<Document>
)
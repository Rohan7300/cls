package com.clebs.celerity.models.requests

data class CompanySignedDocX(
    val CompanyID: Int,
    val SignedDocument: List<Int>
)
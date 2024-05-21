package com.clebs.celerity.models.response

data class GetDriverOtherCompaniesPolicyResponse(
    val CompanyDocuments: List<CompanyDocument>,
    val Message: String,
    val Status: String
)
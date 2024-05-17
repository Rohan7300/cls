package com.clebs.celerity.models

data class DownloadDriverOtherCompaniesPolicyResponse(
    val CompanyDocuments: List<CompanyDocument>,
    val Message: String,
    val Status: String
)
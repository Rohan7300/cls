package com.clebs.celerity.models

data class CompanyDocument(
    val CompanyDocId: Int,
    val CompanyId: Int,
    val CompanyName: String,
    val DocumentName: String,
    val FileContent: String,
    val FileName: String,
    val FileType: String
)
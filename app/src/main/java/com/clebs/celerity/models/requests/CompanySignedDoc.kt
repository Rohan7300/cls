package com.clebs.celerity.models.requests


import com.google.gson.annotations.SerializedName

data class CompanySignedDoc(
    @SerializedName("CompanyID")
    val companyID: Int,
    @SerializedName("SignedDocument")
    val signedDocument: List<Int>
)
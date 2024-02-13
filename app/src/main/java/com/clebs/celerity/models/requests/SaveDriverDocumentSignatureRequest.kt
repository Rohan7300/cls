package com.clebs.celerity.models.requests


import com.google.gson.annotations.SerializedName

data class SaveDriverDocumentSignatureRequest(
    @SerializedName("Address")
    val address: String,
    @SerializedName("CompanyDocId")
    val companyDocId: List<Int>,
    @SerializedName("CompanySignedDocs")
    val companySignedDocs: List<CompanySignedDoc>,
    @SerializedName("DriverHireAgreement")
    val driverHireAgreement: DriverHireAgreement,
    @SerializedName("HasAgreement")
    val hasAgreement: Boolean,
    @SerializedName("IsAmazonSignatureUpdated")
    val isAmazonSignatureUpdated: Boolean,
    @SerializedName("IsDAEngagementChecked")
    val isDAEngagementChecked: Boolean,
    @SerializedName("IsDAHandbookChecked")
    val isDAHandbookChecked: Boolean,
    @SerializedName("IsDAVanHireChecked")
    val isDAVanHireChecked: Boolean,
    @SerializedName("IsGDPRChecked")
    val isGDPRChecked: Boolean,
    @SerializedName("IsSLAChecked")
    val isSLAChecked: Boolean,
    @SerializedName("Signature")
    val signature: String,
    @SerializedName("UserID")
    val userID: Int
)
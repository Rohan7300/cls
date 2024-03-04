package com.clebs.celerity.models.requests

data class UpdateDriverAgreementSignatureRequest(
    val Address: String,
    val CompanyDocId: List<Int>,
    val CompanySignedDocs: List<CompanySignedDocX>,
    val DriverHireAgreement: DriverHireAgreementX,
    val HasAgreement: Boolean,
    val IsAmazonSignatureUpdated: Boolean,
    val IsDAEngagementChecked: Boolean,
    val IsDAHandbookChecked: Boolean,
    val IsDAVanHireChecked: Boolean,
    val IsGDPRChecked: Boolean,
    val IsSLAChecked: Boolean,
    val Signature: String,
    val UserID: Int
)
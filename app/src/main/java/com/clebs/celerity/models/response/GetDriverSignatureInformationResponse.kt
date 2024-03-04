package com.clebs.celerity.models.response

data class GetDriverSignatureInformationResponse(
    val DAEngagementSectionReq: Boolean,
    val DAHandbookSectionReq: Boolean,
    val DAVanHireSectionReq: Boolean,
    val GDPRSectionReq: Boolean,
    val IsDriverActive: Boolean,
    val IsOtherCompanySignatureReq: Boolean,
    val IsSignatureReq: Boolean,
    val OtherCompanyDocuments: List<OtherCompanyDocument>?,
    val PreviousAddress: String,
    val SLASectionReq: Boolean,
    val UserID: Int,
    val UserRole: String,
    val isAmazonSignatureReq: Boolean
)
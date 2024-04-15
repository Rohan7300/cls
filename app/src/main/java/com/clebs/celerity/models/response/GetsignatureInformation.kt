package com.clebs.celerity.models.response


import com.google.gson.annotations.SerializedName

data class GetsignatureInformation(
    @SerializedName("DAEngagementSectionReq")
    val dAEngagementSectionReq: Boolean,
    @SerializedName("DAHandbookSectionReq")
    val dAHandbookSectionReq: Boolean,
    @SerializedName("DAVanHireSectionReq")
    val dAVanHireSectionReq: Boolean,
    @SerializedName("GDPRSectionReq")
    val gDPRSectionReq: Boolean,
    @SerializedName("isAmazonSignatureReq")
    val isAmazonSignatureReq: Boolean,
    @SerializedName("IsDriverActive")
    val isDriverActive: Boolean,
    @SerializedName("IsOtherCompanySignatureReq")
    val isOtherCompanySignatureReq: Boolean,
    @SerializedName("IsSignatureReq")
    val isSignatureReq: Boolean,
    @SerializedName("OtherCompanyDocuments")
    val otherCompanyDocuments: Any,
    @SerializedName("PreviousAddress")
    val previousAddress: String,
    @SerializedName("SLASectionReq")
    val sLASectionReq: Boolean,
    @SerializedName("UserID")
    val userID: Int,
    @SerializedName("UserRole")
    val userRole: String,
    @SerializedName("HandbookId")
    val handbookId:Int
)
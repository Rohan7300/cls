package com.clebs.celerity.models.response

data class GetCompanySignedDocumentListResponseItem(
    val DriverCompanyName: String,
    val DriverName: String,
    val HBAddress: String,
    val HBDriverId: Int,
    val HBEngagementFileName: String,
    val HBEngagementIdValid: Boolean,
    val HBGDRPFileName: String,
    val HBGDRPIsValid: Boolean,
    val HBIsHBValid: Boolean,
    val HBIsRead: Any,
    val HBIsValid: Boolean,
    val HBReadDate: String,
    val HBSLAFileName: String,
    val HBSLAIsValid: Boolean,
    val HBSignFileName: String,
    val HandBookId: Int,
    val HireAggrementFileName: String,
    val VehicleId: Int
)
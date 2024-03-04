package com.clebs.celerity.models.requests

data class DriverHireAgreementX(
    val Accidents: Boolean,
    val Address: String,
    val AgrLicenceNo: String,
    val AgrLicenceStartDate: String,
    val Comments: String,
    /*val CompanyID: Int,*/
    val Conviction: Boolean,
    val DaHireLicenceEndDate: String,
    val HireAgrDOB: String,
    val Signature: String,
    val UserID: Int,
    val VehType: String
)
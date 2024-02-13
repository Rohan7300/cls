package com.clebs.celerity.models.requests


import com.google.gson.annotations.SerializedName

data class DriverHireAgreement(
    @SerializedName("Accidents")
    val accidents: Boolean,
    @SerializedName("Address")
    val address: String,
    @SerializedName("AgrLicenceNo")
    val agrLicenceNo: String,
    @SerializedName("AgrLicenceStartDate")
    val agrLicenceStartDate: String,
    @SerializedName("Comments")
    val comments: String,
    @SerializedName("CompanyID")
    val companyID: Int,
    @SerializedName("Conviction")
    val conviction: Boolean,
    @SerializedName("DaHireLicenceEndDate")
    val daHireLicenceEndDate: String,
    @SerializedName("HireAgrDOB")
    val hireAgrDOB: String,
    @SerializedName("Signature")
    val signature: String,
    @SerializedName("UserID")
    val userID: Int,
    @SerializedName("VehType")
    val vehType: String
)
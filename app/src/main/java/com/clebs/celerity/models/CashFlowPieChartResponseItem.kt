package com.clebs.celerity.models


import com.google.gson.annotations.SerializedName

data class CashFlowPieChartResponseItem(
    @SerializedName("CharterHireDeduction")
    val charterHireDeduction: Int,
    @SerializedName("DriverName")
    val driverName: String,
    @SerializedName("InvoiceCount")
    val invoiceCount: Int,
    @SerializedName("InvoiceId")
    val invoiceId: Int,
    @SerializedName("IsAmazonVerified")
    val isAmazonVerified: Boolean,
    @SerializedName("MilesRateperLt")
    val milesRateperLt: Double,
    @SerializedName("RouteCounter")
    val routeCounter: Int,
    @SerializedName("TotalDeduction")
    val totalDeduction: Int,
    @SerializedName("ThirdPartyDeduction")
    val ThirdPartyDeduction:Double,
    @SerializedName("TotalEarning")
    val totalEarning: Double,
    @SerializedName("TotalMileage")
    val totalMileage: Int,
    @SerializedName("UserId")
    val userId: Int,
    @SerializedName("WeekNo")
    val weekNo: Int
)
package com.clebs.celerity.models.response

data class GetDAOutStandingDeductionListResponseItem(
    val DriverId: Int,
    val DriverName: String,
    val TotalDeductionAmount: String
)
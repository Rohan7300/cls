package com.clebs.celerity.models.response

data class GetDriverDeductionHistoryResponseItem(
    val Comment: String,
    val CostTypeID: Int,
    val DeductionAmount: Int,
    val DeductionId: Int,
    val DeductionTypeName: String,
    val DriverName: String,
    val InvoiceId: Int,
    val ParentCompanyId: Int,
    val RegNo: Any,
    val WeekNo: Int,
    val YearNo: Int
)
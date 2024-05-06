package com.clebs.celerity.models.response

data class DeductionAgreementResponse(
    val AgreementDate: String,
    val AlreadySigned: Boolean,
    val CostTypeId: Int,
    val DaDedAggrId: Int,
    val DaUserName: String,
    val DeductionComment: String,
    val FromLocation: String,
    val PaymentKey: String,
    val TotalAdvanceAmt: Int,
    val WeeklyDeductionAmt: Int
)
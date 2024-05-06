package com.clebs.celerity.models.requests

data class UpdateDeductioRequest(
    val DaDedAggrDaId: Int,
    val DaUserName: String,
    val FromLocation: String,
    val IsDaDedAggAccepted: Boolean,
    val PaymentKey: String,
    val RejectionComment: String,
    val Signature: String
)
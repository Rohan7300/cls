package com.clebs.celerity.models.requests

data class ApproveDaDailyRotaRequest(
    val Comment: String,
    val IsApproved: Boolean,
    val Token: String,
    val UserId: Int
)

package com.clebs.celerity.models.requests

data class SaveBreakTimeRequest(
    val BreakFinishTime: String,
    val BreakStartTime: String,
    val DawDriverBreakId: String,
    val UserId: String
)
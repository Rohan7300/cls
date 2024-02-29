package com.clebs.celerity.models.response

data class GetDriverBreakTimeInfoResponseItem(
    val BreakAddedBy: Int,
    val BreakAddedOn: String,
    val BreakTimeEnd: String,
    val BreakTimeStart: String,
    val DawDate: String,
    val DawDriverBreakId: Int,
    val UsrId: Int
)
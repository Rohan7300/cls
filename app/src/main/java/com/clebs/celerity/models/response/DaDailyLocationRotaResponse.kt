package com.clebs.celerity.models.response

data class DaDailyLocationRotaResponse(
    val DayOfWeek: String,
    val DriverName: String,
    val LocationName: String,
    val LrnId: Int,
    val RotaDate: String,
    val Token: String,
    val WeekNo: Int,
    val YearNo: Int
)
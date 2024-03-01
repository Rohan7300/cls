package com.clebs.celerity.models.response

data class DailyWorkInfoByIdResponse(
    val ClockedInTime: Any?,
    val ClockedOutTime: Any,
    val DailyWorkDate: String,
    val DailyWorkFaceMaskFileName: String,
    val DailyWorkId: Int,
    val DailyWorkRegNo: Any,
    val DailyWorkUsrId: Int,
    val DwOillevelFilename: Any,
    val LeadDriverId: Any
)
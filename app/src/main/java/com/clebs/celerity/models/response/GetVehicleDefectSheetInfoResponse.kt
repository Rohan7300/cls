package com.clebs.celerity.models.response

data class GetVehicleDefectSheetInfoResponse(
    val DefSheetID: Int,
    val IsDefected: Boolean,
    val IsSubmited: Boolean,
    val LmID: Int,
    val LocName: Any,
    val RegNo: Any,
    val SubmittedON: Any,
    val VmID: Int,
    val WeekNo: Int,
    val YearNo: Int
)
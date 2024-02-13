package com.clebs.celerity.models.response


import com.google.gson.annotations.SerializedName

data class GetDefectSheetBasicInfoResponse(
    @SerializedName("LmID")
    val lmID: Int,
    @SerializedName("LocName")
    val locName: String,
    @SerializedName("PreviousRecordedMileage")
    val previousRecordedMileage: Int,
    @SerializedName("RegNo")
    val regNo: String,
    @SerializedName("UserID")
    val userID: Int,
    @SerializedName("VmID")
    val vmID: Int,
    @SerializedName("WeekNo")
    val weekNo: Int,
    @SerializedName("YearNo")
    val yearNo: Int
)
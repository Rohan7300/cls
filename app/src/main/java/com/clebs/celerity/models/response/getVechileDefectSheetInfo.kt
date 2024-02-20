package com.clebs.celerity.models.response


import com.google.gson.annotations.SerializedName

data class getVechileDefectSheetInfo(
    @SerializedName("DefSheetID")
    val defSheetID: Int,
    @SerializedName("IsDefected")
    val isDefected: Boolean,
    @SerializedName("IsSubmited")
    val isSubmited: Boolean,
    @SerializedName("LmID")
    val lmID: Int,
    @SerializedName("LocName")
    val locName: Any,
    @SerializedName("RegNo")
    val regNo: Any,
    @SerializedName("SubmittedON")
    val submittedON: Any,
    @SerializedName("VmID")
    val vmID: Int,
    @SerializedName("WeekNo")
    val weekNo: Int,
    @SerializedName("YearNo")
    val yearNo: Int
)
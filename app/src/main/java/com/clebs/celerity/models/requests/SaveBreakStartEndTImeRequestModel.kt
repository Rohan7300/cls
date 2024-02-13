package com.clebs.celerity.models.requests


import com.google.gson.annotations.SerializedName

data class SaveBreakStartEndTImeRequestModel(
    @SerializedName("BreakFinishTime")
    val breakFinishTime: String,
    @SerializedName("BreakStartTime")
    val breakStartTime: String,
    @SerializedName("DawDriverBreakId")
    val dawDriverBreakId: String,
    @SerializedName("UserId")
    val userId: String
)
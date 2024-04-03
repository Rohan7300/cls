package com.clebs.celerity.models.response


import com.google.gson.annotations.SerializedName

data class GetAvgScoreResponse(
    @SerializedName("AvgTotalScore")
    val avgTotalScore: String,
    @SerializedName("Status")
    val status: String
)
package com.clebs.celerity.models

import com.google.gson.annotations.SerializedName

data class GetLastWeekScore(
    @SerializedName("LastWeekScore")
    val avgTotalScore: String,
    @SerializedName("Status")
    val status: String
)

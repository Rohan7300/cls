package com.clebs.celerity.models


import com.google.gson.annotations.SerializedName

data class GetWeekYear(
    @SerializedName("WeekNO")
    val weekNO: Int,
    @SerializedName("Year")
    val year: Int
)
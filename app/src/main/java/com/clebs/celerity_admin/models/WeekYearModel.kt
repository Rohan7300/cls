package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class WeekYearModel(
    @SerializedName("WeekNO")
    val weekNO: Int,
    @SerializedName("Year")
    val year: Int
)
package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class GetvehicleOilLevelListItem(
    @SerializedName("VehOilLevelId")
    val vehOilLevelId: Int,
    @SerializedName("VehOilLevelName")
    val vehOilLevelName: String
)
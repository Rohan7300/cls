package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class GetVehicleFuelLevelListItem(
    @SerializedName("VehFuelLevelId")
    val vehFuelLevelId: Int,
    @SerializedName("VehFuelLevelName")
    val vehFuelLevelName: String
)
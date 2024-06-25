package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class GetVehicleLocationItem(
    @SerializedName("LocId")
    val locId: Int,
    @SerializedName("LocationName")
    val locationName: String
)
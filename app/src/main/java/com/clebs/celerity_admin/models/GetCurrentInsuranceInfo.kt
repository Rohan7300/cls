package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class GetCurrentInsuranceInfo(
    @SerializedName("Status")
    val status: String,
    @SerializedName("VehicleInfo")
    val vehicleInfo: VehicleInfo5X
)
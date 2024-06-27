package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class DDAMandateModel(
    @SerializedName("Status")
    val status: String,
    @SerializedName("VehicleInfo")
    val vehicleInfo: VehicleInfo
)
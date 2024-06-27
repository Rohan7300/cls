package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class RepoInfoModel(
    @SerializedName("Status")
    val status: String,
    @SerializedName("VehicleInfo")
    val vehicleInfo: VehicleInfoXX
)
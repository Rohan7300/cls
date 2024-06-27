package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class GetReturnVmID(
    @SerializedName("Status")
    val status: String,
    @SerializedName("VehicleInfo")
    val vehicleInfo: VehicleInfoX
)
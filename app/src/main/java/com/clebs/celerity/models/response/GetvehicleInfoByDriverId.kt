package com.clebs.celerity.models.response


import com.google.gson.annotations.SerializedName

data class GetvehicleInfoByDriverId(
    @SerializedName("VmId")
    val vmId: Int,
    @SerializedName("VmRegNo")
    val vmRegNo: String
)
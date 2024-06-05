package com.clebs.celerity.models.requests


import com.google.gson.annotations.SerializedName

data class SaveVehicleInspectionInfo(
    @SerializedName("DriverId")
    val driverId: Int,
    @SerializedName("InspectionDate")
    val inspectionDate: String,
    @SerializedName("ClientUniqueId")
    val inspectionId: String,
    @SerializedName("LocationId")
    val inspectionLmId: Int,
    @SerializedName("VmId")
    val inspectionVmId: Int
)
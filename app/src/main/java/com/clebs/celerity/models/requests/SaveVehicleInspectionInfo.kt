package com.clebs.celerity.models.requests


import com.google.gson.annotations.SerializedName

data class SaveVehicleInspectionInfo(
    @SerializedName("DriverId")
    val driverId: Int,
    @SerializedName("InspectionDate")
    val inspectionDate: String,
    @SerializedName("InspectionId")
    val inspectionId: String,
    @SerializedName("InspectionLmId")
    val inspectionLmId: Int,
    @SerializedName("InspectionVmId")
    val inspectionVmId: Int
)
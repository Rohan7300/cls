package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class VehicleReturnModelListItem(
    @SerializedName("VehicleId")
    val vehicleId: Int,
    @SerializedName("VehicleName")
    val vehicleName: String,
    @SerializedName("VehicleRegNo")
    val vehicleRegNo: String,
    @SerializedName("VehicleType")
    val VehicleType:String,
    @SerializedName("LocationId")
    val LocationId:Int,
    @SerializedName("LocationName")
    val LocationName:String
)
package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class VehicleInfo(
    @SerializedName("Accident")
    val accident: Boolean,
    @SerializedName("ChMessage")
    val chMessage: String,
    @SerializedName("ChResp")
    val chResp: String,
    @SerializedName("Comments")
    val comments: String,
    @SerializedName("ConAccComment")
    val conAccComment: String,
    @SerializedName("Conviction")
    val conviction: Boolean,
    @SerializedName("CurrentVehicleRegNo")
    val currentVehicleRegNo: String,
    @SerializedName("DOB")
    val dOB: String,
    @SerializedName("LisanceEnddate")
    val lisanceEnddate: String,
    @SerializedName("LisanceStartDate")
    val lisanceStartDate: String,
    @SerializedName("LisenceNumber")
    val lisenceNumber: String,
    @SerializedName("VehicleLocation")
    val vehicleLocation: String,
    @SerializedName("VmLocation")
    val vmLocation: String,
    @SerializedName("VmRegId")
    val vmRegId: Int,
    @SerializedName("VmRegNo")
    val vmRegNo: String
)
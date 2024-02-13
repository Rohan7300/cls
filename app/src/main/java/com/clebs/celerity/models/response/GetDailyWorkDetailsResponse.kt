package com.clebs.celerity.models.response


import com.google.gson.annotations.SerializedName

data class GetDailyWorkDetailsResponse(
    @SerializedName("DwBreakFinish")
    val dwBreakFinish: String,
    @SerializedName("DwBreakStart")
    val dwBreakStart: String,
    @SerializedName("DwComment")
    val dwComment: String,
    @SerializedName("DwDate")
    val dwDate: String,
    @SerializedName("DwDcpName")
    val dwDcpName: String,
    @SerializedName("DwDslmEnd")
    val dwDslmEnd: String,
    @SerializedName("DwDslmStart")
    val dwDslmStart: String,
    @SerializedName("DwFoodCost")
    val dwFoodCost: Int,
    @SerializedName("DwId")
    val dwId: Int,
    @SerializedName("DwIsVehChecked")
    val dwIsVehChecked: Boolean,
    @SerializedName("DwIsVehDefected")
    val dwIsVehDefected: Boolean,
    @SerializedName("DwOillevelFilename")
    val dwOillevelFilename: String,
    @SerializedName("DwOvernightCharges")
    val dwOvernightCharges: Int,
    @SerializedName("DwParkingCharges")
    val dwParkingCharges: Int,
    @SerializedName("DwRegNo")
    val dwRegNo: String,
    @SerializedName("DwTollCharges")
    val dwTollCharges: Int,
    @SerializedName("DwUsrId")
    val dwUsrId: Int,
    @SerializedName("DwVehDefectComment")
    val dwVehDefectComment: String,
    @SerializedName("DwVideoName")
    val dwVideoName: String,
    @SerializedName("LeadDriverId")
    val leadDriverId: Int
)
package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class WeeklyDefectChecksModelItem(
    var title: String = "Dveh reg", var titletwo: String="Daname", val titlethree: String,

    @SerializedName("DAName")
    val dAName: String,
    @SerializedName("LocationName")
    val locationName: String,
    @SerializedName("VdhCheckDaId")
    val vdhCheckDaId: Int,
    @SerializedName("VdhCheckId")
    val vdhCheckId: Int,
    @SerializedName("VdhCheckVmId")
    val vdhCheckVmId: Int,
    @SerializedName("VdhCheckYear")
    val vdhCheckYear: Int,
    @SerializedName("VehCheckLmId")
    val vehCheckLmId: Int,
    @SerializedName("VehRegNo")
    val vehRegNo: String,
    @SerializedName("VehWkCheckWeek")
    val vehWkCheckWeek: Int
)
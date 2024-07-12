package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class SaveInspectionRequestBody(
    @SerializedName("VdhCheckId")
    val VdhCheckId: Int,

    @SerializedName("ClientUniqueRefId")
    val clientUniqueRefId: String,
    @SerializedName("VdhCheckDaId")
    val vdhCheckDaId: Int,
    @SerializedName("VdhCheckVmId")
    val vdhCheckVmId: Int,
    @SerializedName("VdhCheckWeekNo")
    val vdhCheckWeekNo: Int,
    @SerializedName("VdhCheckYearNo")
    val vdhCheckYearNo: Int,
    @SerializedName("VehCheckLmId")
    val vehCheckLmId: Int
)
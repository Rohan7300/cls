package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class VehicleInfoX(
    @SerializedName("Accident")
    val accident: Boolean,
    @SerializedName("AllocUsrId")
    val allocUsrId: Int,
    @SerializedName("AllocUsrName")
    val allocUsrName: String,
    @SerializedName("Comments")
    val comments: String,
    @SerializedName("ConAccComment")
    val conAccComment: String,
    @SerializedName("Conviction")
    val conviction: Boolean,
    @SerializedName("DOB")
    val dOB: String,
    @SerializedName("DaId")
    val daId: Int,
    @SerializedName("DaName")
    val daName: String,
    @SerializedName("IsMultiAllocAllow")
    val isMultiAllocAllow: Boolean,
    @SerializedName("LisanceEnddate")
    val lisanceEnddate: String,
    @SerializedName("LisanceStartDate")
    val lisanceStartDate: String,
    @SerializedName("LisenceNumber")
    val lisenceNumber: String,
    @SerializedName("SecondAllocUsrId")
    val secondAllocUsrId: Int,
    @SerializedName("SecondAllocUsrName")
    val secondAllocUsrName: String,
    @SerializedName("ThirdAllocUsrId")
    val thirdAllocUsrId: Int,
    @SerializedName("ThirdAllocUsrName")
    val thirdAllocUsrName: String,
    @SerializedName("VanTypeId")
    val vanTypeId: Int
)
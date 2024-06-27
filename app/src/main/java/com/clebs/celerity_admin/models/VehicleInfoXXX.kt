package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class VehicleInfoXXX(
    @SerializedName("MultiuserVeh")
    val multiuserVeh: Boolean,
    @SerializedName("SecondAllocUsrId")
    val secondAllocUsrId: Any,
    @SerializedName("ThirdAllocUsrId")
    val thirdAllocUsrId: Any,
    @SerializedName("VehLastMillage")
    val vehLastMillage: Int,
    @SerializedName("VmAllocUsrId")
    val vmAllocUsrId: Int,
    @SerializedName("VmId")
    val vmId: Int
)
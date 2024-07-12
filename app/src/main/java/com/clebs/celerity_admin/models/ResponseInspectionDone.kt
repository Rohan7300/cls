package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class ResponseInspectionDone(
    @SerializedName("ClientUniqueRefId")
    val clientUniqueRefId: String,
    @SerializedName("IsInspectionDone")
    val isInspectionDone: Boolean
)
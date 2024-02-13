package com.clebs.celerity.models.requests


import com.google.gson.annotations.SerializedName

data class GetDefectSheetBasicInfoRequestModel(
    @SerializedName("LmID")
    val lmID: Int,
    @SerializedName("VmID")
    val vmID: Int
)
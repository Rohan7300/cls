package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class DriverListResponseModelItem(
    @SerializedName("Id")
    val id: Int,
    @SerializedName("Name")
    val name: String
)
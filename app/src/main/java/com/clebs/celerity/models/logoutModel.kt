package com.clebs.celerity.models


import com.google.gson.annotations.SerializedName

data class logoutModel(
    @SerializedName("Message")
    val message: String,
    @SerializedName("ResponseType")
    val responseType: String
)
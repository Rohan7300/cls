package com.clebs.celerity.models.response


import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @SerializedName("Message")
    val message: String,
    @SerializedName("ResponseType")
    val responseType: String
)
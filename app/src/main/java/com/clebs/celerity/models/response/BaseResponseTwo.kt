package com.clebs.celerity.models.response

import com.google.gson.annotations.SerializedName

data class BaseResponseTwo (
        @SerializedName("code")
        val code: Int,
        @SerializedName("message")
        val message: String,
)
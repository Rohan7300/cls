package com.clebs.celerity.models.response

import com.google.gson.annotations.SerializedName

data class BaseResponseTwo (
        @SerializedName("Status")
        val Status: Int,
        @SerializedName("Message")
        val message: String,
)
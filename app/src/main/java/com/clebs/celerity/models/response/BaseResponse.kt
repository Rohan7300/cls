package com.clebs.celerity.models.response


import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    val code: Int,
    val message: String,
    val data: T
)
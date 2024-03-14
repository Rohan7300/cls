package com.clebs.celerity.models.requests


import com.google.gson.annotations.SerializedName

data class UpdateProfileRequestBody(
    @SerializedName("UserId")
    val userId: Int,
    @SerializedName("FirstName")
    val firstName: String,
    @SerializedName("LastName")
    val lastName: String,
    @SerializedName("EmailID")
    val emailID: String,
    @SerializedName("PhoneNumber")
    val phoneNumber: String,

    @SerializedName("Address")
    val address: String,

)
package com.clebs.celerity.models.response


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("Message")
    var message: String ="",
    @SerializedName("RefreshToken")
    var refreshToken: String = "",
    @SerializedName("Token")
    var token: String ="",
    @SerializedName("TokenExpiredOn")
    var tokenExpiredOn: String = "",
    @SerializedName("UserID")
    var userID: Int = 0
)
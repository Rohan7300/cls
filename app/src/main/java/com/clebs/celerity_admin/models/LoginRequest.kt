package com.clebs.celerity_admin.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


data class LoginRequest(


@field:SerializedName("UserName")
var username: String? = "",

@field:SerializedName("Password")
var password: String? = "",

)

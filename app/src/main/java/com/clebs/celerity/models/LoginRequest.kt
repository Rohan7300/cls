package com.clebs.celerity.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize @Keep
data class LoginRequest(


@field:SerializedName("UserName")
var username: String? = "",

@field:SerializedName("Password")
var password: String? = "",

) : Parcelable

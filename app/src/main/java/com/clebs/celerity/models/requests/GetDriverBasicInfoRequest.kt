package com.clebs.celerity.models.requests


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetDriverBasicInfoRequest(
    @SerializedName("UserID")
    val userID: Double
) : Parcelable
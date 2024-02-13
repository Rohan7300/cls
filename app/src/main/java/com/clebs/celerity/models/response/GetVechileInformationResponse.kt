package com.clebs.celerity.models.response


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class GetVechileInformationResponse(


    @SerializedName("VehicleLastMillage")
    val vehicleLastMillage: Int,
    @SerializedName("VmDaVehStatusId")
    val vmDaVehStatusId: Int,
    @SerializedName("VmId")
    val vmId: Int,
    @SerializedName("VmLocId")
    val vmLocId: Int,
    @SerializedName("VmRegNo")
    val vmRegNo: String,
    @SerializedName("VmType")
    val vmType: String
) : Parcelable
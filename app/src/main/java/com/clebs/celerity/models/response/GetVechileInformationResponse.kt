package com.clebs.celerity.models.response


import android.os.Parcelable
import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class GetVechileInformationResponse(

    @SerializedName("Status")
    val status:String?,
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
    val vmType: String,
    @SerializedName("LocationName")
    val locationName: String,
    @SerializedName("VinNumber")
    val VinNumber:String? = null,
    @SerializedName("VehicleMake")
    val VehicleMake:String? = null,
    @SerializedName("VehicleBodyStyle")
    val VehicleBodyStyle:String? = null,
    @SerializedName("VehicleModel")
    val VehicleModel:String? = null,
    @SerializedName("VmCreatedDate")
    val VmCreatedDate:String? = null,
    @SerializedName("VmStatus")
    val VmStatus:String?= null
) : Parcelable
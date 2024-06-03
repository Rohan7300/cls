package com.clebs.celerity.models.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetVehicleImageUploadInfoResponse(
    var Status: String,
    var Message: String,
    @SerializedName("DaVehImgId") var DaVehImgId: Int? = null,
    @SerializedName("DaVehDriverId") var DaVehDriverId: Int? = null,
    @SerializedName("DaVehDefctSheetId") var DaVehDefctSheetId: Int? = null,
    @SerializedName("DaVehImageUploadedDate") var DaVehImageUploadedDate: String? = null,
    @SerializedName("DaVehicleDashBoardImage") var DaVehImgDashBoardFileName: String? = null,
    @SerializedName("DaVehicleFrontImage") var DaVehImgFrontFileName: String? = null,
    @SerializedName("DaVehicleRearImage") var DaVehImgRearFileName: String? = null,
    @SerializedName("DaVehicleNearSideImage") var DaVehImgNearSideFileName: String? = null,
    @SerializedName("DaVehicleOffSideImage") var DaVehImgOffSideFileName: String? = null,
    @SerializedName("DaVehicleFaceMaskImage") var DaVehImgFaceMaskFileName: String? = null,
    @SerializedName("DaVehicleOilLevelImage") var DaVehImgOilLevelFileName: String? = null,
    @SerializedName("DaVehicleAddBlueImage") var DaVehicleAddBlueImage: String? = null,
    @SerializedName("IsVehicleImageUploaded") var IsVehicleImageUploaded: Boolean? = null,
    @SerializedName("IsAdBlueRequired") var IsAdBlueRequired:Boolean?=null
) : Parcelable
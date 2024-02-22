package com.clebs.celerity.models.response

import com.google.gson.annotations.SerializedName

data class GetVehicleImageUploadInfoResponse(
    var Status: String,
    var Message: String,
    @SerializedName("DaVehImgId") var DaVehImgId: Int? = null,
    @SerializedName("DaVehDriverId") var DaVehDriverId: Int? = null,
    @SerializedName("DaVehDefctSheetId") var DaVehDefctSheetId: Int? = null,
    @SerializedName("DaVehImageUploadedDate") var DaVehImageUploadedDate: String? = null,
    @SerializedName("DaVehImgDashBoardFileName") var DaVehImgDashBoardFileName: String? = null,
    @SerializedName("DaVehImgFrontFileName") var DaVehImgFrontFileName: String? = null,
    @SerializedName("DaVehImgRearFileName") var DaVehImgRearFileName: String? = null,
    @SerializedName("DaVehImgNearSideFileName") var DaVehImgNearSideFileName: String? = null,
    @SerializedName("DaVehImgOffSideFileName") var DaVehImgOffSideFileName: String? = null,
    @SerializedName("DaVehImgFaceMaskFileName") var DaVehImgFaceMaskFileName: String? = null,
    @SerializedName("DaVehImgOilLevelFileName") var DaVehImgOilLevelFileName: String? = null,
    @SerializedName("IsVehicleImageUploaded") var IsVehicleImageUploaded: Boolean? = null
)
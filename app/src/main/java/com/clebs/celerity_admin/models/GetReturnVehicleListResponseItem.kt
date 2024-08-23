package com.clebs.celerity_admin.models

data class GetReturnVehicleListResponseItem(
    val LocationId: Int,
    val LocationName: String,
    val VehicleId: Int,
    val VehicleName: String,
    val VehicleRegNo: String,
    val VehicleType: String
)
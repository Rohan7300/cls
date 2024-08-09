package com.clebs.celerity.models.requests

data class GetAllVehicleInspectionListResponseItem(
    val VehicleId: Int,
    val VehicleName: String,
    val VehicleRegNo: String
)
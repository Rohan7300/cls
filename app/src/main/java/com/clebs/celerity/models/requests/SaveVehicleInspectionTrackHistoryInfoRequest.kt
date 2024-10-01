package com.clebs.celerity.models.requests

data class SaveVehicleInspectionTrackHistoryInfoRequest(
    val ClientUniqueId: String,
    val DriverId: Int,
    val InspectionDate: String,
    val InspectionType: String,
    val LmId: Int,
    val VmId: Int
)
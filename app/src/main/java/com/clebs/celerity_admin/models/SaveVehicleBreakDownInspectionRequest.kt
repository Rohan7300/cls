package com.clebs.celerity_admin.models

data class SaveVehicleBreakDownInspectionRequest(
    val Comment: String,
    val DriverId: Int,
    val InspectionId: Int,
    val SuperVisorId: Int,
    val VehInspectionDate: String,
    val VehRequestTypeIds: List<Int>,
    val VmId: Int
)
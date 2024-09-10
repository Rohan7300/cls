package com.clebs.celerity.models

data class CompleteDriverVehicleBreakDownInspectionRequest(
    val AddBlueMileage: String,
    val DaVehImgId: Int,
    val DriverId: Int,
    val FuelLevelId: Int,
    val InspectionId: Int,
    val IsVehBreakDownInspectionDone: Boolean,
    val OilLevelId: Int,
    val SupervisorId: Int,
    val VehCurrentMileage: String,
    val VehInspectionDoneById: Int,
    val VehInspectionDoneOn: String
)
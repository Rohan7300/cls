package com.clebs.celerity_admin.models

data class CollectVehicleFromSupplierRequest(
    val AddBlueMileage: String,
    val ClientRefId: String,
    val DriverId: Int,
    val InspectionDate: String,
    val IsVehAllocDaLeft: Boolean,
    val IsVehAllocInOurGarage: Boolean,
    val IsVehAllocIsDamaged: Boolean,
    val IsVehCollected: Boolean,
    val NewCollectedRegNo: String,
    val NewVmId: Int,
    val OldVmId: Int,
    val ParentCompanyId: Int,
    val Signature1: String,
    val VehAllocComments: String,
    val VehAllocGarageStartDate: String,
    val VehAllocStatusId: Int,
    val VehCurrentFuelLevelId: Int,
    val VehCurrentMileage: String,
    val VehCurrentOILLevelId: Int,
    val VehSelectedLocationId: Int,
    val VehType: String,
    val supervisorId: Int
)
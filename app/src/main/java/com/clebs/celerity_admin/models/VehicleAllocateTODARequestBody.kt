package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class VehicleAllocateTODARequestBody(

    @SerializedName("AccidentsChk")
    val accidentsChk: Boolean,
    @SerializedName("AccidentsChk2")
    val accidentsChk2: Boolean,
    @SerializedName("AccidentsChk3")
    val accidentsChk3: Boolean,
    @SerializedName("AddBlueMileage")
    val addBlueMileage: String,
    @SerializedName("ChangeVehCount")
    val changeVehCount: Int,
    @SerializedName("ChargeDay")
    val chargeDay: Int,
    @SerializedName("ChargeDayComment")
    val chargeDayComment: String,
    @SerializedName("ClientRefId")
    val clientRefId: String,
    @SerializedName("ConOrAcciComment")
    val conOrAcciComment: String,
    @SerializedName("ConOrAcciComment2")
    val conOrAcciComment2: String,
    @SerializedName("ConOrAcciComment3")
    val conOrAcciComment3: String,
    @SerializedName("ConvictionsChk")
    val convictionsChk: Boolean,
    @SerializedName("ConvictionsChk2")
    val convictionsChk2: Boolean,
    @SerializedName("ConvictionsChk3")
    val convictionsChk3: Boolean,
    @SerializedName("DOB")
    val dOB: String,
    @SerializedName("DOB2")
    val dOB2: String,
    @SerializedName("DOB3")
    val dOB3: String,
    @SerializedName("DriverId")
    val driverId: Int,
    @SerializedName("ExistingFirstUsrId")
    val existingFirstUsrId: Int,
    @SerializedName("ExistingSecondUsrId")
    val existingSecondUsrId: Int,
    @SerializedName("ExistingThirdUsrId")
    val existingThirdUsrId: Int,
    @SerializedName("FirstUserAllocPosition")
    val firstUserAllocPosition: Int,
    @SerializedName("FirstUsrId")
    val firstUsrId: Int,
    @SerializedName("InspectionDate")
    val inspectionDate: String,
    @SerializedName("IsDaVehChange")
    val isDaVehChange: Boolean,
    @SerializedName("IsVehAllocDaLeft")
    val isVehAllocDaLeft: Boolean,
    @SerializedName("IsVehAllocInOurGarage")
    val isVehAllocInOurGarage: Boolean,
    @SerializedName("IsVehAllocIsDamaged")
    val isVehAllocIsDamaged: Boolean,
    @SerializedName("IsVehicleMultiAlloc")
    val isVehicleMultiAlloc: Boolean,
    @SerializedName("LicenseEndDate")
    val licenseEndDate: String,
    @SerializedName("LicenseEndDate2")
    val licenseEndDate2: String,
    @SerializedName("LicenseEndDate3")
    val licenseEndDate3: String,
    @SerializedName("LicenseNo")
    val licenseNo: String,
    @SerializedName("LicenseNo2")
    val licenseNo2: String,
    @SerializedName("LicenseNo3")
    val licenseNo3: String,
    @SerializedName("LicenseStartDate")
    val licenseStartDate: String,
    @SerializedName("LicenseStartDate2")
    val licenseStartDate2: String,
    @SerializedName("LicenseStartDate3")
    val licenseStartDate3: String,
    @SerializedName("NewVmId")
    val newVmId: Int,
    @SerializedName("OldVmId")
    val oldVmId: Int,
    @SerializedName("ParentCompanyId")
    val parentCompanyId: Int,
    @SerializedName("SecondUserAllocPosition")
    val secondUserAllocPosition: Int,
    @SerializedName("SecondUsrId")
    val secondUsrId: Int,
    @SerializedName("Signature1")
    val signature1: String,
    @SerializedName("Signature2")
    val signature2: String,
    @SerializedName("Signature3")
    val signature3: String,
    @SerializedName("supervisorId")
    val supervisorId: Int,
    @SerializedName("ThirdUserAllocPosition")
    val thirdUserAllocPosition: Int,
    @SerializedName("ThirdUsrId")
    val thirdUsrId: Int,
    @SerializedName("VehAllocComments")
    val vehAllocComments: String,
    @SerializedName("VehAllocGarageStartDate")
    val vehAllocGarageStartDate: String?=null,
    @SerializedName("VehAllocStatusId")
    val vehAllocStatusId: Int,
    @SerializedName("VehCurrentFuelLevelId")
    val vehCurrentFuelLevelId: Int,
    @SerializedName("VehCurrentMileage")
    val vehCurrentMileage: String,
    @SerializedName("VehCurrentOILLevelId")
    val vehCurrentOILLevelId: Int,
    @SerializedName("VehSelectedLocationId")
    val vehSelectedLocationId: Int,
    @SerializedName("VehType")
    val vehType: String
)
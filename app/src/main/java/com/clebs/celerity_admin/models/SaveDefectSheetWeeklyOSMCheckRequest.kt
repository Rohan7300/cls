package com.clebs.celerity_admin.models

data class SaveDefectSheetWeeklyOSMCheckRequest(
    val Comment: String,
    val PowerSteering: Boolean,
    val PowerSteeringLiquid: Boolean,
    val TyrePressureFrontNS: Boolean,
    val TyrePressureFrontOS: Boolean,
    val TyrePressureRearNS: Boolean,
    val TyrePressureRearOS: Boolean,
    val TyreThreadDepthFrontNSVal: Int,
    val TyreThreadDepthFrontOSVal: Int,
    val TyreThreadDepthRearNSVal: Int,
    val TyreThreadDepthRearOSVal: Int,
    val UserId: Int,
    val VdhAdminComment: String,
    val VdhBrakeFluidLevelId: Int,
    val VdhCheckId: Int,
    val VdhDefChkImgOilLevelId: Int,
    val VdhEngineCoolantLevelId: Int,
    val VdhWindScreenConditionId: Int,
    val VdhWindowScreenWashingLiquidId: Int,
    val WeeklyActionCheck: Boolean,
    val WeeklyApproveCheck: Boolean,
    val WindowScreenState: Boolean,
    val WindscreenWashingLiquid: Boolean
)
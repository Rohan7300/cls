package com.clebs.celerity.models.requests


import com.google.gson.annotations.SerializedName

data class SaveVechileDefectSheetRequest(
    @SerializedName("VdhBrakes")
    val vdhBrakes: String,
    @SerializedName("VdhBrakesComment")
    val vdhBrakesComment: String,
    @SerializedName("VdhCabSecurityInterior")
    val vdhCabSecurityInterior: String,
    @SerializedName("VdhCabSecurityInteriorComment")
    val vdhCabSecurityInteriorComment: String,
    @SerializedName("VdhComments")
    val vdhComments: String,
    @SerializedName("VdhDaId")
    val vdhDaId: Int,
    @SerializedName("VdhDate")
    val vdhDate: String,
    @SerializedName("VdhExcessiveEngineExhaustSmoke")
    val vdhExcessiveEngineExhaustSmoke: String,
    @SerializedName("VdhExcessiveEngineExhaustSmokeComment")
    val vdhExcessiveEngineExhaustSmokeComment: String,
    @SerializedName("VdhFuelAdBlueLevel")
    val vdhFuelAdBlueLevel: String,
    @SerializedName("VdhFuelAdBlueLevelComment")
    val vdhFuelAdBlueLevelComment: String,
    @SerializedName("VdhFuelOilLeaks")
    val vdhFuelOilLeaks: String,
    @SerializedName("VdhFuelOilLeaksComment")
    val vdhFuelOilLeaksComment: String,
    @SerializedName("VdhHornReverseBeeper")
    val vdhHornReverseBeeper: String,
    @SerializedName("VdhHornReverseBeeperComment")
    val vdhHornReverseBeeperComment: String,
    @SerializedName("VdhId")
    val vdhId: Int,
    @SerializedName("VdhIndicatorsSideRepeaters")
    val vdhIndicatorsSideRepeaters: String,
    @SerializedName("VdhIndicatorsSideRepeatersComment")
    val vdhIndicatorsSideRepeatersComment: String,
    @SerializedName("VdhIsDefected")
    val vdhIsDefected: Boolean,
    @SerializedName("VdhLights")
    val vdhLights: String,
    @SerializedName("VdhLightsComment")
    val vdhLightsComment: String,
    @SerializedName("VdhLmId")
    val vdhLmId: Int,
    @SerializedName("VdhMirrors")
    val vdhMirrors: String,
    @SerializedName("VdhMirrorsComment")
    val vdhMirrorsComment: String,
    @SerializedName("VdhOdoMeterReading")
    val vdhOdoMeterReading: Int,
    @SerializedName("VdhOilFuelCoolantLeaks")
    val vdhOilFuelCoolantLeaks: String,
    @SerializedName("VdhOilFuelCoolantLeaksComment")
    val vdhOilFuelCoolantLeaksComment: String,
    @SerializedName("VdhPocIsaction")
    val vdhPocIsaction: Boolean,
    @SerializedName("VdhReflectorsMarkers")
    val vdhReflectorsMarkers: String,
    @SerializedName("VdhReflectorsMarkersComment")
    val vdhReflectorsMarkersComment: String,
    @SerializedName("VdhRegistrationPlates")
    val vdhRegistrationPlates: String,
    @SerializedName("VdhRegistrationPlatesComment")
    val vdhRegistrationPlatesComment: String,
    @SerializedName("VdhSeatBelt")
    val vdhSeatBelt: String,
    @SerializedName("VdhSeatBeltComment")
    val vdhSeatBeltComment: String,
    @SerializedName("VdhSpareWheel")
    val vdhSpareWheel: String,
    @SerializedName("VdhSpareWheelComment")
    val vdhSpareWheelComment: String,
    @SerializedName("VdhSteering")
    val vdhSteering: String,
    @SerializedName("VdhSteeringComment")
    val vdhSteeringComment: String,
    @SerializedName("VdhTyresWheels")
    val vdhTyresWheels: String,
    @SerializedName("VdhTyresWheelsComment")
    val vdhTyresWheelsComment: String,
    @SerializedName("VdhVehFront")
    val vdhVehFront: String,
    @SerializedName("VdhVehFrontComment")
    val vdhVehFrontComment: String,
    @SerializedName("VdhVehNearSide")
    val vdhVehNearSide: String,
    @SerializedName("VdhVehNearSideComment")
    val vdhVehNearSideComment: String,
    @SerializedName("VdhVehOffside")
    val vdhVehOffside: String,
    @SerializedName("VdhVehOffsideComment")
    val vdhVehOffsideComment: String,
    @SerializedName("VdhVehRear")
    val vdhVehRear: String,
    @SerializedName("VdhVehRearComment")
    val vdhVehRearComment: String,
    @SerializedName("VdhVehicleLockingSystem")
    val vdhVehicleLockingSystem: String,
    @SerializedName("VdhVehicleLockingSystemComment")
    val vdhVehicleLockingSystemComment: String,
    @SerializedName("VdhVmId")
    val vdhVmId: Int,
    @SerializedName("VdhWarningServiceLights")
    val vdhWarningServiceLights: String,
    @SerializedName("VdhWarningServiceLightsComment")
    val vdhWarningServiceLightsComment: String,
    @SerializedName("VdhWheelsWheelFixings")
    val vdhWheelsWheelFixings: String,
    @SerializedName("VdhWheelsWheelFixingsComment")
    val vdhWheelsWheelFixingsComment: String,
    @SerializedName("VdhWindowsGlassVisibility")
    val vdhWindowsGlassVisibility: String,
    @SerializedName("VdhWindowsGlassVisibilityComment")
    val vdhWindowsGlassVisibilityComment: String,
    @SerializedName("VdhWindscreen")
    val vdhWindscreen: String,
    @SerializedName("VdhWindscreenComment")
    val vdhWindscreenComment: String,
    @SerializedName("VdhWipersWashers")
    val vdhWipersWashers: String,
    @SerializedName("VdhWipersWashersComment")
    val vdhWipersWashersComment: String,
    @SerializedName("WeekNo")
    val weekNo: Int,
    @SerializedName("YearNo")
    val yearNo: Int
)
package com.clebs.celerity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Images"
)
data class ImageEntity(

    @PrimaryKey(autoGenerate = true)
    var localId: Int = 0,

    @ColumnInfo(defaultValue = "user")
    var userName:String?="user",


    @ColumnInfo(defaultValue = "empty")
    var vehicleDashboard: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var front: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var nearSide: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var rear: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var offside: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var oilLevel: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var addBlue: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inWindScreen: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inWindowGlass: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inWipersWashers: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inMirrors: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inCabSecurityInterior: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inSeatBelt: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inWarningServiceLights: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inFuelAdBlueLevel: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inOilCoolantLevel: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inFogLights: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inIndicatorsSideRepeaters: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inHornReverseBeeper: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inSteeringControl: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var inBrakedEbsAbs: String? = "empty",


    //Internal defect Names
    @ColumnInfo(defaultValue = "f")
    var dfNameWindScreen: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameWindowGlass: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameWipersWashers: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameMirrors: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameCabSecurityInterior: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameSeatBelt: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameWarningServiceLights: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameFuelAdBlueLevel: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameOilCoolantLevel: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameFogLights: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameIndicatorsSideRepeaters: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameHornReverseBeeper: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameSteeringControl: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameBrakedEbsAbs: String? = "f",


    @ColumnInfo(defaultValue = "empty")
    var exVehicleLockingSystem: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exBodyDamageFront: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exBodyDamageNearSide: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exBodyDamageRear: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exBodyDamageOffside: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exRegistrationNumberPlates: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exReflectorsMarkers: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exWheelFixings: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exTyreConditionThreadDepth: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exOilFuelCoolantLeaks: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exExcessiveEngExhaustSmoke: String? = "empty",
    @ColumnInfo(defaultValue = "empty")
    var exSpareWheel: String? = "empty",


    @ColumnInfo(defaultValue = "f")
    var dfNameVehicleLockingSystem: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameBodyDamageFront: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameBodyDamageNearSide: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameBodyDamageRear: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameBodyDamageOffside: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameRegistrationNumberPlates: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameReflectorsMarkers: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameWheelFixings: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameTyreConditionThreadDepth: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameOilFuelCoolantLeaks: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameExcessiveEngExhaustSmoke: String? = "f",
    @ColumnInfo(defaultValue = "f")
    var dfNameSpareWheel: String? = "f"
)

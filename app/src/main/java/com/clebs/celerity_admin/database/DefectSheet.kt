package com.clebs.celerity_admin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "defectsheet")
data class DefectSheet(
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0,
    var tyreDepthFrontNSImage:String?=null,
    var tyrePressureFrontNSRB:Int=-1,
    var tyreDepthRearNSImage:String?=null,
    var tyrePressureRearNSRB:Int=-1,
    var tyreDepthFrontOSImage:String?=null,
    var tyrePressureFrontOSRB:Int=-1,
    var tyreDepthRearOSImage:String?=null,
    var tyrePressureRearOSRB:Int=-1,
    var engineLevelImage:String?=null,
    var oilLevelID:Int =-1,
    var engineCoolantLevelID:Int = -1,
    var brakeFluidLevelID:Int = -1,
    var powerSteeringCheck:Boolean = false,
    var windScreenWashingLevelId:Int = -1,
    var windScreenConditionId:Int = -1,
    var addBlueLevelImage:String?= null,
    var nsWingMirrorImage:String? = null,
    var osWingMirrorImage:String? = null,
    var threeSixtyVideo:String? = null,
    var comment:String? = null

)

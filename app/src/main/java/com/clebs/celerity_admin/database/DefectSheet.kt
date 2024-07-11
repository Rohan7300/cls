package com.clebs.celerity_admin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "defectsheet")
data class DefectSheet(
    @PrimaryKey(autoGenerate = true)
    val id:Int = 0,
    val tyreDepthFrontNSImage:String?=null,
    val tyrePressureFrontNSRB:Int=-1,
    val tyrePressureRearNSImage:String?=null,
    val tyrePressureRearNSRB:Int=-1,
    val tyrePressureFrontOSImage:String?=null,
    val tyrePressureFrontOSRB:Int=-1,
    val engineLevelImage:String?=null,
    val oilLevelID:Int =-1,
    val engineCoolantLevelID:Int = -1,
    val brakeFluidLevelID:Int = -1,
    val powerSteeringCheck:Boolean = false,
    val windScreenWashingLevelId:Int = -1,
    val windScreenConditionId:Int = -1,
    val addBlueLevelImage:String?= null,
    val nsWingMirrorImage:String? = null,
    val osWingMirrorImage:String? = null,
    val threeSixtyVideo:String? = null

)

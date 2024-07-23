package com.clebs.celerity_admin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "defectsheet")
data class DefectSheet(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var tyreDepthFrontNSImage: String? = null,
    var uploadTyreDepthFrontNSImage: Boolean = true,
    var tyrePressureFrontNSRB: Int = -1,
    var tyreDepthRearNSImage: String? = null,
    var uploadTyreDepthRearNSImage: Boolean = true,
    var tyrePressureRearNSRB: Int = -1,
    var tyreDepthFrontOSImage: String? = null,
    var uploadTyreDepthFrontOSImage: Boolean = true,
    var tyrePressureFrontOSRB: Int = -1,
    var tyreDepthRearOSImage: String? = null,
    var uploadTyreDepthRearOSImage: Boolean = true,
    var tyrePressureRearOSRB: Int = -1,
    var engineLevelImage: String? = null,
    var uploadEngineLevelImage: Boolean = true,
    var oilLevelID: Int = -1,
    var engineCoolantLevelID: Int = -1,
    var brakeFluidLevelID: Int = -1,
    var powerSteeringCheck: Boolean = false,
    var windScreenWashingLevelId: Int = -1,
    var windScreenConditionId: Int = -1,
    var addBlueLevelImage: String? = null,
    var uploadAddBlueLevelImage: Boolean = true,
    var nsWingMirrorImage: String? = null,
    var uploadNSWingMirrorImage: Boolean = true,
    var osWingMirrorImage: String? = null,
    var uploadOSWingMirrorImage: Boolean = true,
    var threeSixtyVideo: String? = null,
    var uploadThreeSixtyVideo: Boolean = true,
    var comment: String? = null,
    var otherImages: String? = null,
    var uploadOtherImages: Boolean = true,
    var WeeklyActionCheck: Boolean = false,
    var WeeklyApproveCheck: Boolean = false
)

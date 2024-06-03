package com.clebs.celerity.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "OfflineSync"
)
data class OfflineSyncEntity(

    @PrimaryKey(autoGenerate = true)
    var offId: Int = 0,

    @ColumnInfo("isIni")
    var isIni: Boolean = false,

    @ColumnInfo("clebID")
    var clebID: Int = 0,

    @ColumnInfo("vehicleID")
    var vehicleID: String? = null,

    @ColumnInfo("DaWDate")
    var dawDate: String? = null,



    @ColumnInfo("dashboardImage")
    var dashboardImage: String? = null,

    @ColumnInfo("isDashboardImageRequired")
    var isDashboardImageRequired:Boolean = true,

    @ColumnInfo("isdashboardUploadFailed")
    var isdashboardUploadedFailed: Boolean = false,



    @ColumnInfo("frontImage")
    var frontImage: String? = null,

    @ColumnInfo("isFrontImageRequired")
    var isFrontImageRequired:Boolean = true,

    @ColumnInfo("isfrontImageFailed")
    var isfrontImageFailed: Boolean = false,



    @ColumnInfo("rearSideImage")
    var rearSideImage: String? = null,

    @ColumnInfo("isRearImageRequired")
    var isRearImageRequired:Boolean = true,

    @ColumnInfo("isrearSideFailed")
    var isrearSideFailed: Boolean = false,



    @ColumnInfo("offSideImage")
    var offSideImage: String? = null,

    @ColumnInfo("isOffsideImageRequired")
    var isOffsideImageRequired:Boolean = true,

    @ColumnInfo("isoffSideFailed")
    var isoffSideFailed: Boolean = false,



    @ColumnInfo("nearSideImage")
    var nearSideImage: String? = null,

    @ColumnInfo("isNearImageRequired")
    var isnearImageRequired:Boolean = true,

    @ColumnInfo("isnearSideFailed")
    var isnearSideFailed: Boolean = false,



    @ColumnInfo("addblueImage")
    var addblueImage: String? = null,

    @ColumnInfo("isaddBlueImageRequired")
    var isaddBlueImageRequired:Boolean = true,

    @ColumnInfo("isaddblueImageFailed")
    var isaddblueImageFailed: Boolean = false,



    @ColumnInfo("oillevelImage")
    var oillevelImage: String? = null,

    @ColumnInfo("isoilLevelImageRequired")
    var isoilLevelImageRequired:Boolean = true,

    @ColumnInfo("isoillevelImageFailed")
    var isoillevelImageFailed: Boolean = false,



    @ColumnInfo("facemaskImage")
    var faceMaskImage: String? = null,

    @ColumnInfo("isfaceMaskImageRequired")
    var isfaceMaskImageRequired:Boolean = true,

    @ColumnInfo("isfaceMaskFailed")
    var isfaceMaskImageFailed: Boolean = false,



    @ColumnInfo("isInspectionDoneToday")
    var isInspectionDoneToday:Boolean = false,

    @ColumnInfo("isImageUploadedToday")
    var isImagesUploadedToday: Boolean = false,

    @ColumnInfo("isClockedInToday")
    var isClockedInToday:Boolean = false,

    @ColumnInfo("isClockedOutToday")
    var isClockedOutToday:Boolean = false,

    @ColumnInfo("isDefectSheetFilled")
    var isDefectSheetFilled:Boolean = false
)

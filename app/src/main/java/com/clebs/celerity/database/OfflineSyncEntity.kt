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
    var isIni:Boolean = false,

    @ColumnInfo("clebID")
    var clebID: Int =0,

    @ColumnInfo("vehicleID")
    var vehicleID: String? = null,

    @ColumnInfo("DaWDate")
    var dawDate: String? = null,

    @ColumnInfo("dashboardImage")
    var dashboardImage: String? = null,

    @ColumnInfo("isdashboardUploadFailed")
    var isdashboardUploadedFailed: Boolean = false,

    @ColumnInfo("frontImage")
    var frontImage: String? = null,

    @ColumnInfo("isfrontImageFailed")
    var isfrontImageFailed: Boolean = false,

    @ColumnInfo("rearSideImage")
    var rearSideImage: String? = null,

    @ColumnInfo("isrearSideFailed")
    var isrearSideFailed: Boolean = false,

    @ColumnInfo("offSideImage")
    var offSideImage: String? = null,

    @ColumnInfo("isoffSideFailed")
    var isoffSideFailed: Boolean = false,

    @ColumnInfo("nearSideImage")
    var nearSideImage: String? = null,

    @ColumnInfo("isnearSideFailed")
    var isnearSideFailed: Boolean = false,

    @ColumnInfo("addblueImage")
    var addblueImage: String? = null,

    @ColumnInfo("isaddblueImageFailed")
    var isaddblueImageFailed: Boolean = false,

    @ColumnInfo("oillevelImage")
    var oillevelImage: String? = null,

    @ColumnInfo("isoillevelImageFailed")
    var isoillevelImageFailed: Boolean = false,

    @ColumnInfo("facemaskImage")
    var faceMaskImage: String? = null,

    @ColumnInfo("isfaceMaskFailed")
    var isfaceMaskImageFailed: Boolean = false,

)

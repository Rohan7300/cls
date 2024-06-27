package com.clebs.celerity_admin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "upload")
data class CheckInspection(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val InspectionDone: Boolean,

)

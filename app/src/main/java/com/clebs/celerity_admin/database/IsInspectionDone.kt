package com.clebs.celerity_admin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inspection")
data class IsInspectionDone(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var InspectionDoneRegNo: String,
    var InspectionClientUniqueID: String,
)

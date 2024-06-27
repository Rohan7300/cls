package com.clebs.celerity_admin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "information")
data class VehicleInformation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var vehiclelocation: String,
    var currentVehicleMileage: String,
    var blueleve: String,
    val fuelelevel: String,
    val oillevel: String
)

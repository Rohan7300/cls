package com.clebs.celerity_admin.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")

data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val CompanyName: String,
    val changeDAvechile:Boolean,

    val returnDAvehicle:Boolean,
    val selectDA: String,
    val selectVehicleReturn:String,
    val worthy:Boolean,
    val notworthy:Boolean,
    val reuquestType:String,
    var spinnerposition:Int,

)
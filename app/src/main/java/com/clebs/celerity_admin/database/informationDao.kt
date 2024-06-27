package com.clebs.celerity_admin.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface informationDao {
    @Insert
    suspend fun insert(information: VehicleInformation)

    @Update
    suspend fun update(information: VehicleInformation)

    @Delete
    suspend fun delete(information: VehicleInformation)

    @Query("SELECT * FROM information")
    fun getAllUsersinfo(): List<VehicleInformation>

    @Query("DELETE FROM information")
    suspend fun deleteTableInfo()
}
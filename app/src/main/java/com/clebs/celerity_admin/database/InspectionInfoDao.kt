package com.clebs.celerity_admin.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface InspectionInfoDao {
    @Insert
    suspend fun insert(vehicleUploadInformation: CheckInspection)

    @Update
    suspend fun update(vehicleUploadInformation: CheckInspection)

    @Delete
    suspend fun delete(vehicleUploadInformation: CheckInspection)

    @Query("SELECT * FROM upload")
    fun getAllUsers(): List<CheckInspection>


    @Query("DELETE FROM upload")
    suspend fun deleteTableInfoUpload()
}
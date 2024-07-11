package com.clebs.celerity_admin.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface IsInspectionDoneDao {
    @Insert
    fun insert(isInspectionDone: IsInspectionDone)

    @Update
    suspend fun updates(isInspectionDone: IsInspectionDone)

    @Delete
    suspend fun deletes(isInspectionDone: IsInspectionDone)

    @Query("SELECT * FROM inspection")
    fun getAllinspectioninfo(): List<IsInspectionDone>

    @Query("DELETE FROM inspection")
    suspend fun deleteTableInfos()
}
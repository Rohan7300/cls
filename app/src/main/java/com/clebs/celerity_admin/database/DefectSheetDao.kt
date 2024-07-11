package com.clebs.celerity_admin.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DefectSheetDao {
    @Insert
    suspend fun insert(defectSheet: DefectSheet)

    @Update
    suspend fun update(defectSheet: DefectSheet)

    @Query("SELECT * FROM DefectSheet WHERE id = :id")
    fun getDefectSheetById(id: Int): DefectSheet?
}
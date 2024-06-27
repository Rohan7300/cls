package com.clebs.celerity_admin.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ReturnToSupplierDao {
    @Insert
    suspend fun insert(information: ReturnToSupplierDepoDatabase)

    @Update
    suspend fun update(information: ReturnToSupplierDepoDatabase)

    @Delete
    suspend fun delete(information: ReturnToSupplierDepoDatabase)

    @Query("SELECT * FROM depodatabase")
    fun getAllUsersinfo(): List<ReturnToSupplierDepoDatabase>

    @Query("DELETE FROM depodatabase")
    suspend fun deleteTableInfo()


}
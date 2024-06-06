package com.clebs.celerity.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ImageDao {

    @Query("SELECT * FROM Images WHERE DaWDate = :dawDate ORDER BY localId DESC LIMIT 1")
    fun getImagesByUserName(dawDate:String): ImageEntity


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserName(name: ImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVal(image: ImageEntity)
}
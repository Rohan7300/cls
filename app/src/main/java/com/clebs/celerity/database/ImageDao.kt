package com.clebs.celerity.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.clebs.celerity.utils.dateToday

@Dao
interface ImageDao {

    @Query("SELECT * FROM Images WHERE DaWDate = :dawDate ORDER BY localId DESC LIMIT 1")
    fun getImagesByUserName(dawDate:String): ImageEntity


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserName(name: ImageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVal(image: ImageEntity){
        val dawDate = dateToday()
        deleteByDawDate(dawDate)
        insert(image)
    }

    @Query("DELETE FROM Images WHERE DaWDate = :dawDate")
    suspend fun deleteByDawDate(dawDate: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: ImageEntity)

}
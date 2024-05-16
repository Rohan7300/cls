package com.clebs.celerity.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OfflineSyncDao {

    @Query("SELECT * FROM OfflineSync WHERE clebID = :clebID AND DaWDate = :dawDate AND isIni = 1 ORDER BY offId DESC LIMIT 1")
    suspend fun getOSyncData(clebID: Int, dawDate: String): OfflineSyncEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(data: OfflineSyncEntity)
}
package com.clebs.celerity.database

class OSyncRepo(private val db: OfflineSyncDB) {

   suspend fun getData(clebID:Int,dawDate:String):OfflineSyncEntity{
        return db.osDao().getOSyncData(clebID,dawDate)
    }

    suspend fun insertData(data:OfflineSyncEntity){
        db.osDao().insertData(data)
    }
}
package com.clebs.celerity.database

class OSyncRepo(private val db: OfflineSyncDB) {

    suspend fun getData(clebID: Int, dawDate: String): OfflineSyncEntity {
        return db.osDao().getOSyncData(clebID, dawDate)
    }

    suspend fun insertData(data: OfflineSyncEntity) {
        db.osDao().insertData(data)
    }

    suspend fun insertDataIfNotExists(data: OfflineSyncEntity) {
        val count = db.osDao().countEntries(data.clebID, data.dawDate!!)
        if (count == 0) {
            insertData(data)
        }
    }

    suspend fun insertOrUpdateData(data: OfflineSyncEntity) {
        val existingDataCount = db.osDao().countEntries(data.clebID, data.dawDate!!)
        val existingData = db.osDao().getOSyncData(data.clebID, data.dawDate!!)

        if (existingDataCount <= 0) {
            insertData(data)
        } else {
            db.osDao().updateData(data.copy(offId = existingData.offId))
        }
    }

}
package com.clebs.celerity.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [OfflineSyncEntity::class],
    version = 9
)
abstract class OfflineSyncDB : RoomDatabase() {
    abstract fun osDao(): OfflineSyncDao

    companion object {
        @Volatile
        private var instance: OfflineSyncDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = Companion.instance ?: synchronized(LOCK) {
            instance ?: createDataBase(context).also {
                instance = it
            }
        }

        private fun createDataBase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                OfflineSyncDB::class.java,
                "offlinesync.db"
            ).fallbackToDestructiveMigration()
                .build()
    }


}
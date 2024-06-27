package com.clebs.celerity_admin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, VehicleInformation::class], version = 2)
abstract class OfflineSyncDB : RoomDatabase() {
    abstract fun osDao(): UserDao
    abstract fun infoDao(): informationDao

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
                .allowMainThreadQueries()
                .build()
    }

    suspend fun isUserTableEmpty(): Boolean {
        return osDao().getAllUsers().isEmpty()
    }

    suspend fun insert(user: User) {
        osDao().insert(user)
    }

    suspend fun update(user: User) {
        osDao().update(user)
    }

    suspend fun delete(user: User) {
        osDao().delete(user)
    }

    fun getAllUsers(): List<User> {
        return osDao().getAllUsers()
    }

    suspend fun isUserTableEmptyInformation(): Boolean {
        return infoDao().getAllUsersinfo().isEmpty()
    }

    suspend fun insertinfo(information: VehicleInformation) {
        infoDao().insert(information)
    }

    fun getAllUsersinfo(): List<VehicleInformation> {
        return infoDao().getAllUsersinfo()
    }
}
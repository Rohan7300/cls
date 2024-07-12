package com.clebs.celerity_admin.database

import android.content.Context
import androidx.room.Database
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, VehicleInformation::class, CheckInspection::class ,IsInspectionDone::class,DefectSheet::class],
    version = 2
)
abstract class OfflineSyncDB : RoomDatabase() {
    abstract fun osDao(): UserDao
    abstract fun infoDao(): informationDao

    abstract fun UploadImagesInfoDao(): InspectionInfoDao

    abstract fun defectSheetDao():DefectSheetDao

    abstract fun IsInspectionDone():IsInspectionDoneDao

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

    suspend fun deleteUserTable() {
        osDao().deleteTable()
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

    suspend fun deleteTableInfos() {
        infoDao().deleteTableInfo()
    }

    suspend fun isUploadPicturesIsEmpty(): Boolean {
        return UploadImagesInfoDao().getAllUsers().isEmpty()
    }

    suspend fun insertInfoUpload(vehicleUploadInformation: CheckInspection) {
        return UploadImagesInfoDao().insert(vehicleUploadInformation)
    }

    suspend fun deleteUploadInformation() {
        UploadImagesInfoDao().deleteTableInfoUpload()
    }

    fun getUploadImagesInformation(): List<CheckInspection> {
        return UploadImagesInfoDao().getAllUsers()
    }

    suspend fun isDepoDatabaseIsEmpty(): Boolean {
        return UploadImagesInfoDao().getAllUsers().isEmpty()
    }

    suspend fun insertDepoInfo(vehicleUploadInformation: CheckInspection) {
        return UploadImagesInfoDao().insert(vehicleUploadInformation)
    }

    suspend fun deleteDepoInformation() {
        UploadImagesInfoDao().deleteTableInfoUpload()
    }

    fun GetDepoInformation(): List<CheckInspection> {
        return UploadImagesInfoDao().getAllUsers()
    }

    suspend fun insertOrUpdate(defectSheet: DefectSheet) {
        val existingDefectSheet = defectSheetDao().getDefectSheetById(defectSheet.id)
        if (existingDefectSheet == null) {
            defectSheetDao().insert(defectSheet)
        } else {
            defectSheetDao().update(defectSheet)
        }
    }

    fun getDefectSheet(id:Int):DefectSheet?{
        return defectSheetDao().getDefectSheetById(id)
    }
    fun insertinspectionInfo(isInspectionDone: IsInspectionDone) {
       IsInspectionDone().insert(isInspectionDone)
    }

    fun getInspectionInfo(): List<IsInspectionDone> {
        return IsInspectionDone().getAllinspectioninfo()
    }

     fun isInspectionTableEmpty(): Boolean {
        return IsInspectionDone().getAllinspectioninfo().isEmpty()
    }
}
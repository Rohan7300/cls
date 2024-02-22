package com.clebs.celerity.database

import com.clebs.celerity.utils.DBImages
import com.clebs.celerity.utils.DBNames
import com.clebs.celerity.utils.Prefs


class ImagesRepo(private val db: ImageDatabase, private val pref: Prefs) {

    fun getImagesbyUser(): ImageEntity? {
        return db.imageDao().getImagesByUserName()
    }

    suspend fun insertImage(image: ImageEntity) {
        db.imageDao().insertVal(image)
    }

    suspend fun insertDefectName(imageEntity: ImageEntity) {
        db.imageDao().insertVal(imageEntity)
    }
}
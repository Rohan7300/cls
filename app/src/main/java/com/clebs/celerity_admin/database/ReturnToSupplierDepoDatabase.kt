package com.clebs.celerity_admin.database

import androidx.room.Entity


@Entity(tableName = "depodatabase")
data class ReturnToSupplierDepoDatabase(
    var returndepo:Boolean,
    var returnsupplier:Boolean,
    var worthy:Boolean,
    var notworthy:Boolean

)

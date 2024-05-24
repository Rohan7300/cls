package com.clebs.celerity.models.response

data class VehicleExpiringDocumentsResponseItem(
    val DocTypeName: String,
    val VehDocEndDate: String,
    val VehDocFileName: String,
    val VehDocId: Int,
    val VehDocIsCurrent: Boolean,
    val VehDocStartDate: String,
    val VehDocTypeId: Int,
    val VehDocVmId: Int
)
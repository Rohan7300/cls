package com.clebs.celerity.models.response

data class GetDAVehicleExpiredDocumentsItem(
    val DocTypeName: String,
    val RegNo: String,
    val VehDocEndDate: String,
    val VehDocFileName: String,
    val VehDocId: Int,
    val VehDocIsCurrent: Boolean,
    val VehDocStartDate: String,
    val VehDocTypeId: Int,
    val VehDocVmId: Int
)
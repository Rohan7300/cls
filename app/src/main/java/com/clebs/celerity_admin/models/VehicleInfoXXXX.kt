package com.clebs.celerity_admin.models

data class VehicleInfoXXXX(
    val Accident: Boolean,
    val AllocUsrId: Int,
    val AllocUsrName: String,
    val AllowReturnSupplier: String?=null,
    val Comments: String,
    val ConAccComment: String,
    val Conviction: Boolean,
    val DOB: String,
    val DaId: Int,
    val DaName: String,
    val IsMultiAllocAllow: Boolean,
    val LisanceEnddate: String,
    val LisanceStartDate: String,
    val LisenceNumber: String,
    val SecondAllocUsrId: Int,
    val SecondAllocUsrName: String,
    val ThirdAllocUsrId: Int,
    val ThirdAllocUsrName: String,
    val VanTypeId: Int,
    val VehLmId: Int
)
package com.clebs.celerity_admin.models

data class GetVehicleReturnHistoryResponseItem(
    val CompanyAddress: String,
    val CompanyName: String,
    val MasterRetCommunicationFileName: String,
    val VehRetComment: Any,
    val VehRetDaId: Int,
    val VehRetDaName: String,
    val VehRetId: Int,
    val VehRetIsReturned: Boolean,
    val VehRetMasterId: Int,
    val VehRetMasterIsComplete: Boolean,
    val VehRetReqDate: String,
    val VehRetbyId: Any,
    val VehRetbyName: Any,
    val VehRethireCompanyId: Int,
    val VehReturnedOn: Any,
    val VehReturnedOnString: String
)
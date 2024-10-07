package com.clebs.celerity_admin.models

data class GetVehicleCollectionHistoryResponseItem(
    val CompanyAddress: String,
    val CompanyName: String,
    val LocationName: String,
    val MasterCommunicationFileName: String,
    val MasterHireAggrFileName: String,
    val MasterHireEndDate: String,
    val MasterHireStartDate: String,
    val VehColComment: Any,
    val VehColDaId: Int,
    val VehColDaName: String,
    val VehColHisMasterId: Int,
    val VehColHisMasterIsComplete: Boolean,
    val VehColId: Int,
    val VehColIsCollected: Boolean,
    val VehColReqDate: String,
    val VehColbyId: Any,
    val VehColbyName: Any,
    val VehColhireCompanyId: Int,
    val VehColhireLocationId: Int,
    val VehCollectedOn: Any,
    val VehCollectedOnString: String,
    val VehNotReady: Boolean,
    val VehUnqueNo: String
)
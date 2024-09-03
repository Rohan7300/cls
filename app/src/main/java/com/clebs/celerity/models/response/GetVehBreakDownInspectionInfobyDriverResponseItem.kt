package com.clebs.celerity.models.response

data class GetVehBreakDownInspectionInfobyDriverResponseItem(
    val AddBlueMileage: Any,
    val DaVehImgId: Int,
    val FuleLevelId: Any,
    val InspectionDateOnString: String,
    val InspectionDoneOnString: String,
    val InspectionRequestedOnString: String,
    val IsActualInspectionDone: Boolean,
    val OilLevelId: Any,
    val VehInspComment: String,
    val VehInspDaId: Int,
    val VehInspDaName: String,
    val VehInspDate: String,
    val VehInspDoneById: Any,
    val VehInspDoneOn: Any,
    val VehInspDonebyName: Any,
    val VehInspId: Int,
    val VehInspIsActive: Boolean,
    val VehInspIsInspDone: Boolean,
    val VehInspIsRequested: Boolean,
    val VehInspRequestedBy: Int,
    val VehInspRequestedOn: String,
    val VehInspRequestedbyName: String,
    val VehInspVmId: Int,
    val VehMileage: Any,
    val VmRegNo: String
)
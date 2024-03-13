package com.clebs.celerity.models.response

data class GetRideAlongRouteInfoByIdRes(
    val IsReTraining: Boolean,
    val LeadDriverId: Int,
    val RtAddMode: String,
    val RtComment: String,
    val RtFinishMileage: Int,
    val RtId: Int,
    val RtLocationId: Int,
    val RtName: String,
    val RtNoOfParcelsDelivered: Int,
    val RtNoParcelsbroughtback: Int,
    val RtType: Int,
    val RtUsrId: Int,
    val TrainingDays: Int,
    val VehicleId: Int
)
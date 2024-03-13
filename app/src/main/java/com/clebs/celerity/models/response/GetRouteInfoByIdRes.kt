package com.clebs.celerity.models.response

data class GetRouteInfoByIdRes(
    val RtAddMode: String,
    val RtComment: String,
    val RtDwId: Int,
    val RtFinishMileage: Int,
    val RtId: Int,
    val RtLocationId: Int,
    val RtName: String,
    val RtNoOfParcelsDelivered: Int,
    val RtNoParcelsbroughtback: Int,
    val RtTypeId: Int,
    val RtUsrId: Int,
    val VehicleId: Int
)
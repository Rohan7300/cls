package com.clebs.celerity.models.requests

data class AddOnRouteInfoRequest(
    val RtAddMode: String,
    val RtComment: String,
    val RtDwId: Int,
    val RtFinishMileage: Long,
    val RtTypeId: Int,
    val RtLocationId: Int,
    val RtName: String,
    val RtNoOfParcelsDelivered: Long,
    val RtNoParcelsbroughtback: Int,
    val RtUsrId: Int,
    val VehicleId: Int
)
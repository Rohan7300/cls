package com.clebs.celerity.models.requests

data class AddOnRouteInfoRequest(
    val RtAddMode: String,
    val RtComment: String,
    val RtDwId: Int,
    val RtFinishMileage: Int,
    val RtId: Int,
    val RtLocationId: Int,
    val RtName: String,
    val RtNoOfParcelsDelivered: Int,
    val RtNoParcelsbroughtback: Int,
    val RtType: Int,
    val RtUsrId: Int,
    val VehicleId: Int
)
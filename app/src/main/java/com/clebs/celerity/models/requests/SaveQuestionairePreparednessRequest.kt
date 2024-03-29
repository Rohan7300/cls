package com.clebs.celerity.models.requests

data class SaveQuestionairePreparednessRequest(
    val DaDailyWorkId: Int,
    val LeadDriverId: Int,
    val QuestionId: Int,
    val RaPreparednessDeviceReq: String,
    val RaPreparednessFitForWork: String,
    val RaPreparednessVehicleReadiness: String,
    val RideAlongDriverId: Int,
    val RoutetId: Int
)
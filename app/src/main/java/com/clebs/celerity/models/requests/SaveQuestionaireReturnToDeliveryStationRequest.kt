package com.clebs.celerity.models.requests

data class SaveQuestionaireReturnToDeliveryStationRequest(
    val QuestionId: Int,
    val RaRetToDeliveryStattionComments: String,
    val RaRetToDeliveryStattionHandPackedToAmzl: String,
    val RaRetToDeliveryStattionOnloadBags: String
)
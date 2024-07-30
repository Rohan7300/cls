package com.clebs.celerity.models.requests

data class SaveQuestionareStartupRequestNew(
    val DaDailyWorkId: Int,
    val LeadDriverId: Int,
    val QuestionId: Int,
    val RaStartupClsDaSystem: String,
    val RaStartupComments: String,
    val RaStartupDvic: String,
    val RaStartupEmentor: String,
    val RaStartupLoadingVehicle: String,
    val RaStartupUseOfTrollies: String,
    val RaStartupYardSafty: String,
    val RideAlongDriverId: Int,
    val RoutetId: Int
)
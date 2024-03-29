package com.clebs.celerity.models.requests

data class SaveQuestionaireOnGoingActivitiesRequest(
    val QuestionId: Int,
    val RaOnGoingActivitiesCustomerFeedback: String,
    val RaOnGoingActivitiesDamagedParcel: String,
    val RaOnGoingActivitiesDcrParcelReturned: String,
    val RaOnGoingActivitiesDpmoConcession: String,
    val RaOnGoingActivitiesLocatePackages: String,
    val RaOnGoingActivitiesMissedYouCard: String,
    val RaOnGoingActivitiesNavigatingByZone: String,
    val RaOnGoingActivitiesParkingVehSecurity: String
)
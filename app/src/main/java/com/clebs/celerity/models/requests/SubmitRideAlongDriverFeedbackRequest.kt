package com.clebs.celerity.models.requests

data class SubmitRideAlongDriverFeedbackRequest(
    val DaDailyWorkId: Int,
    val LeadDriverId: Int,
    val QuestionId: Int,
    val RaDriverAdoptCircumstances: String,
    val RaDriverChangeGear: String,
    val RaDriverConfident: String,
    val RaDriverDrivingAnyVan: String,
    val RaDriverFeelSafe: String,
    val RaDriverFillDaWater: String,
    val RaDriverIdentifyDiffVehParts: String,
    val RaDriverObserveLocalTrafficRegulations: String,
    val RaDriverParking: String,
    val RaDriverReactRoadHazard: String,
    val RaDriverReversing: String,
    val RaDriverSpatialAwareness: String,
    val RaDriverToolSpareWheel: String,
    val RaDriverUseOfMirrors: String,
    val RideAlongDriverId: Int,
    val RoutetId: Int,
    val Signature: String
)
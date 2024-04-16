package com.clebs.celerity.models.response

data class SaveDeviceInformationRequest(
    val FcmToken: String,
    val UsrDeviceId: String,
    val UsrDeviceType: String,
    val UsrId: Int
)
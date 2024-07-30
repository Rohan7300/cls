package com.clebs.celerity.models.requests

data class SaveQuestionareDrivingabilityassessment(
    val QuestionId: Int,
    val RaDeliveryProceduresAgeVerificationDelivery: String,
    val RaDeliveryProceduresComments: String,
    val RaDeliveryProceduresContractCompliance: String?="",
    val RaDeliveryProceduresDeliveredToNeighbour: String,
    val RaDeliveryProceduresFrontDeskMailRoom: String?="",
    val RaDeliveryProceduresGeocodes: String,
    val RaDeliveryProceduresHandleWithCare: String,
    val RaDeliveryProceduresLetterboxDelivery: String,
    val RaDeliveryProceduresLockerDeleveries: String?="",
    val RaDeliveryProceduresPersonNamedOnShippingLabel: String,
    val RaDeliveryProceduresPhr: String,
    val RaDeliveryProceduresPod: String,
    val RaDeliveryProceduresVerifyAddress: String
)
package com.clebs.celerity.models.response

data class GetVehicleAdvancePaymentAgreementResponse(
    val AgreementDate: Any,
    val ShowVehicleAdvancePayment: Boolean,
    val VehAdvancePaymentAgreementAmount: Any,
    val VehicleAdvancePaymentContent: Any
)
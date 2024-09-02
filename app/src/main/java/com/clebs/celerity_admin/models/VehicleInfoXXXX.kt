package com.clebs.celerity_admin.models

import com.google.gson.annotations.SerializedName

data class VehicleInfoXXXX(
    @SerializedName("InsuranceExist")
    val insuranceExist: String,
    @SerializedName("MotAndRoad")
    val motAndRoad: String,
    @SerializedName("MotAndRoadExist")
    val motAndRoadExist: Boolean,
    @SerializedName("MoveRequestAlreadyExistId")
    val moveRequestAlreadyExistId: Int,
    @SerializedName("NotAvailableTicketAlreadyGenerated")
    val notAvailableTicketAlreadyGenerated: Boolean,
    @SerializedName("ShowReleaseButton")
    val showReleaseButton: Boolean,
    @SerializedName("VehLmId")
    val vehLmId: Int,
    @SerializedName("VehicleAvailableToAllocate")
    val vehicleAvailableToAllocate: Boolean,
    @SerializedName("VmId")
    val vmId: Int
)
package com.clebs.celerity_admin.models


import com.google.gson.annotations.SerializedName

data class VehicleInfoXX(
    @SerializedName("ShowDepo")
    val showDepo: Boolean,
    @SerializedName("ShowSupplier")
    val showSupplier: Boolean
)
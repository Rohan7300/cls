package com.clebs.celerity_admin.utils

import com.clebs.celerity_admin.models.DriverListResponseModelItem
import com.clebs.celerity_admin.models.VehicleReturnModelListItem

interface OnReturnVehicle {
    fun onItemClick(item: VehicleReturnModelListItem)
}
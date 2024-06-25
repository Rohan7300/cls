package com.clebs.celerity_admin.utils

import com.clebs.celerity_admin.models.GetVehicleLocationItem
import com.clebs.celerity_admin.models.VehicleReturnModelListItem

interface VehicleLocationClick {
    fun onItemClick(item: GetVehicleLocationItem)
}
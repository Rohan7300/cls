package com.clebs.celerity_admin.utils

import com.clebs.celerity_admin.models.CompanyListResponseItem
import com.clebs.celerity_admin.models.GetVehicleRequestTypeItem

interface Onclick {
    fun onItemClick(item: GetVehicleRequestTypeItem)
}
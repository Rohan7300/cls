package com.clebs.celerity_admin.utils

import com.clebs.celerity_admin.models.CompanyListResponseItem
import com.clebs.celerity_admin.models.DriverListResponseModelItem

interface OnclickDriver {
    fun onItemClick(item: DriverListResponseModelItem)
}
package com.clebs.celerity_admin.utils

import com.clebs.celerity_admin.models.CompanyListResponseItem

interface Onclick {
    fun onItemClick(item: CompanyListResponseItem)
}
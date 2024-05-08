package com.clebs.celerity.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.clebs.celerity.database.OSyncRepo

class OSyncVMProvider(
    val oSyncRepo: OSyncRepo,
    val clebID:Int,
    var dawDate:String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OSyncViewModel(oSyncRepo,clebID,dawDate) as T
    }
}
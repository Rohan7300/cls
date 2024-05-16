package com.clebs.celerity.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clebs.celerity.database.OSyncRepo
import com.clebs.celerity.database.OfflineSyncEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OSyncViewModel(val oSyncRepo: OSyncRepo, var clebID: Int, var dawDate: String) : ViewModel() {
    var osData = MutableLiveData(OfflineSyncEntity())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val osEntity = oSyncRepo.getData(clebID, dawDate)
            if (osEntity != null) {
                osData.postValue(osEntity)
            } else {
                Log.d("OSynceViewModel", "Init Fetch Issue")
            }
            /*     osEntity.let {
                     osData.postValue(it)
                 }*/
        }
    }

    fun insertData(data: OfflineSyncEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            oSyncRepo.insertData(data)
        //    val osEntity = oSyncRepo.getData(clebID, dawDate)
            /*            osEntity.let {
                            osData.postValue(it)
                        }*/
        }
    }

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            val osEntity = oSyncRepo.getData(clebID, dawDate)
            if (osEntity != null) {
                if (osEntity.isIni)
                    osData.postValue(osEntity)
            } else {
                Log.d("OSynceViewModel", "Init Fetch Issue")
            }
        }
    }
}
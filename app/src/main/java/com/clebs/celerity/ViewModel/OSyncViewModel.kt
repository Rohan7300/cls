package com.clebs.celerity.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clebs.celerity.database.OSyncRepo
import com.clebs.celerity.database.OfflineSyncEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OSyncViewModel(val oSyncRepo: OSyncRepo,var clebID:Int,var dawDate:String) : ViewModel() {
    var osData = MutableLiveData(OfflineSyncEntity())

    init {
        viewModelScope.launch (Dispatchers.IO){
            val osEntity = oSyncRepo.getData(clebID,dawDate)
            osEntity.let {
                osData.postValue(it)
            }
        }
    }

    fun insertData(data:OfflineSyncEntity){
        viewModelScope.launch (Dispatchers.IO){
            oSyncRepo.insertData(data)
            val osEntity = oSyncRepo.getData(clebID,dawDate)
            osEntity.let {
                osData.postValue(it)
            }
        }
    }
}
package com.clebs.celerity.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.database.OSyncRepo
import com.clebs.celerity.database.OfflineSyncDB
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo

object DependencyProvider {
    private var viewModelInstance: MainViewModel? = null
    private var apiService: ApiService? = null
    private var mainRepo: MainRepo? = null
    private var oSyncRepo:OSyncRepo?=null
     var currentimagebase64: String? = null
    var imagebitmap:Bitmap?=null

    fun getMainVM(owner: ViewModelStoreOwner): MainViewModel {

            viewModelInstance = ViewModelProvider(
                owner,
                MyViewModelFactory(getMainRepo())
            )[MainViewModel::class.java]

        return viewModelInstance!!
    }

    private fun getApiService(): ApiService {
        if (apiService == null)
            apiService = RetrofitService.getInstance().create(ApiService::class.java)
        return apiService!!
    }

    private fun getMainRepo(): MainRepo {
        if (mainRepo == null)
            mainRepo = MainRepo(getApiService())
        return mainRepo!!
    }

    fun offlineSyncRepo(context: Context):OSyncRepo{
        if(oSyncRepo==null)
            oSyncRepo = OSyncRepo(OfflineSyncDB.invoke(context))
        return oSyncRepo!!
    }

    //fun getPrefInstance()
}
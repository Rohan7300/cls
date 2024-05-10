package com.clebs.celerity.utils

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.clebs.celerity.Factory.MyViewModelFactory
import com.clebs.celerity.ViewModel.MainViewModel
import com.clebs.celerity.network.ApiService
import com.clebs.celerity.network.RetrofitService
import com.clebs.celerity.repository.MainRepo

object DependencyProvider {
    private var viewModelInstance: MainViewModel? = null
    private var apiService: ApiService? = null
    private var mainRepo: MainRepo? = null

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

    //fun getPrefInstance()
}
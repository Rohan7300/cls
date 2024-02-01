package com.clebs.celerity.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clebs.celerity.models.GetVechileInformationResponse
import com.clebs.celerity.models.LoginRequest
import com.clebs.celerity.models.LoginResponse
import com.clebs.celerity.repository.MainRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val repo: MainRepo) : ViewModel() {
    fun loginUser(requestModel: LoginRequest): MutableLiveData<LoginResponse?> {
        val responseLiveData = MutableLiveData<LoginResponse?>()

        viewModelScope.launch {
            val response = repo.loginUser(requestModel)
            responseLiveData.postValue(response)
        }

        return responseLiveData
    }
    fun getVichelinformationResponse(userID: Double, LmID: Double,VechileRegistrationno:String): MutableLiveData<GetVechileInformationResponse?> {
        val responseLiveData = MutableLiveData<GetVechileInformationResponse?>()

        viewModelScope.launch {
            val response = repo.getVechileinformation(userID,LmID,VechileRegistrationno)
            responseLiveData.postValue(response)
        }

        return responseLiveData
    }
}
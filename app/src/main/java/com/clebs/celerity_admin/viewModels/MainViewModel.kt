package com.clebs.celerity_admin.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clebs.celerity_admin.models.CompanyListResponse
import com.clebs.celerity_admin.models.DriverListResponseModel
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.models.LoginResponse
import com.clebs.celerity_admin.repo.MainRepo
import kotlinx.coroutines.launch

class MainViewModel( private val repo: MainRepo):ViewModel() {


    fun loginUser(requestModel: LoginRequest): MutableLiveData<LoginResponse?> {
        val responseLiveData = MutableLiveData<LoginResponse?>()

        viewModelScope.launch {
            val response = repo.loginUser(requestModel)
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }

        return responseLiveData
    }
    fun GetCompanyListing(): MutableLiveData<CompanyListResponse?> {
        val responseLiveData = MutableLiveData<CompanyListResponse?>()

        viewModelScope.launch {
            val response = repo.Getcompanylist()
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }

    fun GetDriverListing(): MutableLiveData<DriverListResponseModel?> {
        val responseLiveData = MutableLiveData<DriverListResponseModel?>()

        viewModelScope.launch {
            val response = repo.GetDriverlist()
            if (response.failed) {
                responseLiveData.postValue(null)
            }
            if (!response.isSuccessful) {
                responseLiveData.postValue(null)
            } else {
                responseLiveData.postValue(response.body)
            }
        }
        return responseLiveData
    }
}
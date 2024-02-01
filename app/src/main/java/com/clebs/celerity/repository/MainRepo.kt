package com.clebs.celerity.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.clebs.celerity.models.GetVechileInformationResponse
import com.clebs.celerity.models.LoginRequest
import com.clebs.celerity.models.LoginResponse
import com.clebs.celerity.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainRepo(private val ApiService: ApiService) {

    suspend fun loginUser(requestModel: LoginRequest): LoginResponse? {
        val response = ApiService.login(requestModel)

        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun getVechileinformation(userID: Double, LmID: Double,VechileRegistrationno:String): GetVechileInformationResponse? {
        val response = ApiService.getVehicleInformation(userID, LmID,VechileRegistrationno)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}
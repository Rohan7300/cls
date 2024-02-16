package com.clebs.celerity.repository

import com.clebs.celerity.models.requests.GetDriverBasicInfoRequest
import com.clebs.celerity.models.response.DriversBasicInformationModel
import com.clebs.celerity.models.response.GetVechileInformationResponse
import com.clebs.celerity.models.response.GetsignatureInformation
import com.clebs.celerity.models.requests.LoginRequest
import com.clebs.celerity.models.response.LoginResponse
import com.clebs.celerity.models.requests.logoutModel
import com.clebs.celerity.models.response.BaseResponseTwo
import com.clebs.celerity.models.response.CheckIFTodayCheckIsDone
import com.clebs.celerity.network.ApiService

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

    suspend fun getDriverSignatureInfo(userID: Double): GetsignatureInformation? {
        val response = ApiService.getDriverSignatureInfoforPolicy(userID)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun logout(): logoutModel? {
        val response = ApiService.Logout()
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun GetDriversBasicInfo(userID: Double): DriversBasicInformationModel? {
        val response = ApiService.GetDriversBasicInfo(userID)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun CheckIFTodayCheckIsDone(): CheckIFTodayCheckIsDone? {
        val response = ApiService.CheckifTodayCheckIsDone()
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }

    suspend fun UseEmailAsUsername(userID: Double,emailAdddress:String) : BaseResponseTwo?{
        val response = ApiService.UseEmailAsUsername(userID,emailAdddress)
        if (response.isSuccessful) {
            return response.body()
        }
        return null
    }
}
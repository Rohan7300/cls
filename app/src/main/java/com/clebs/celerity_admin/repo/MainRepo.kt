package com.clebs.celerity_admin.repo

import android.util.Log
import com.clebs.celerity_admin.models.CompanyListResponse
import com.clebs.celerity_admin.models.DriverListResponseModel
import com.clebs.celerity_admin.models.GetVehicleLocation
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.models.LoginResponse
import com.clebs.celerity_admin.models.VehicleReturnModelList
import com.clebs.celerity_admin.models.basemodel.SimpleNetworkResponse
import com.clebs.celerity_admin.network.ApiService
import retrofit2.Response

class MainRepo(private val ApiService: ApiService) {
    private inline fun <T> safeApiCall(apiCall: () -> Response<T>): SimpleNetworkResponse<T> {
        return try {
            SimpleNetworkResponse.success(apiCall.invoke())
        } catch (e: Exception) {
            Log.d("SafeException", "$e")
            SimpleNetworkResponse.failure(e)
        }
    }

    suspend fun loginUser(requestModel: LoginRequest): SimpleNetworkResponse<LoginResponse> {
        return safeApiCall {
            ApiService.login(requestModel)
        }
    }


    suspend fun Getcompanylist(): SimpleNetworkResponse<CompanyListResponse> {
        return safeApiCall {
            ApiService.GetCompanyList()
        }
    }
    suspend fun GetDriverlist(): SimpleNetworkResponse<DriverListResponseModel> {
        return safeApiCall {
            ApiService.GetDriverList()
        }
    }
    suspend fun GetVehiclelist(): SimpleNetworkResponse<VehicleReturnModelList> {
        return safeApiCall {
            ApiService.GetvehicleList()
        }
    }
    suspend fun GetVehiclelocationList(): SimpleNetworkResponse<GetVehicleLocation> {
        return safeApiCall {
            ApiService.GetVehicleLocationList()
        }
    }
}
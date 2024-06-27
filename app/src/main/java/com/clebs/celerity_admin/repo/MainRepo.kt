package com.clebs.celerity_admin.repo

import android.util.Log
import com.clebs.celerity_admin.models.CompanyListResponse
import com.clebs.celerity_admin.models.DDAMandateModel
import com.clebs.celerity_admin.models.DriverListResponseModel
import com.clebs.celerity_admin.models.GetReturnVmID
import com.clebs.celerity_admin.models.GetVehicleFuelLevelList
import com.clebs.celerity_admin.models.GetVehicleLocation
import com.clebs.celerity_admin.models.GetVehicleRequestType
import com.clebs.celerity_admin.models.GetvehicleOilLevelList
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.models.LoginResponse
import com.clebs.celerity_admin.models.RepoInfoModel
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

    suspend fun GetVehicleFuelList(): SimpleNetworkResponse<GetVehicleFuelLevelList> {
        return safeApiCall {
            ApiService.GetVehicleFuelList()
        }
    }

    suspend fun GetVehicleOilList(): SimpleNetworkResponse<GetvehicleOilLevelList> {
        return safeApiCall {
            ApiService.GetVehicleOilList()
        }
    }
    suspend fun GetVehicleDDAMandate(ddaid:String): SimpleNetworkResponse<DDAMandateModel> {
        return safeApiCall {
            ApiService.GetDDAALlocatedVehandLocation(ddaid)
        }
    }
    suspend fun GetVehicleDDAMandateReturn(ddaid:String): SimpleNetworkResponse<GetReturnVmID> {
        return safeApiCall {
            ApiService.GetCurrentAllocatedDAforReturnVehicle(ddaid)
        }
    }
    suspend fun GetRepoInfoModel(ddaid:String): SimpleNetworkResponse<RepoInfoModel> {
        return safeApiCall {
            ApiService.GetvehicleRepoInfo(ddaid)
        }
    }

    suspend fun GetVehicleRequestType(): SimpleNetworkResponse<GetVehicleRequestType> {
        return safeApiCall {
            ApiService.GetVehicleRequestType()
        }
    }
    suspend fun GetDAEmergencyContact(
        userID: Int
    ):SimpleNetworkResponse<String>{
        return safeApiCall {
            ApiService.GetDAEmergencyContact(userID)
        }
    }
}
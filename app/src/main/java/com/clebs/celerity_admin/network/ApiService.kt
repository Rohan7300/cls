package com.clebs.celerity_admin.network

import com.clebs.celerity_admin.models.CompanyListResponse
import com.clebs.celerity_admin.models.DDAMandateModel
import com.clebs.celerity_admin.models.DriverListResponseModel
import com.clebs.celerity_admin.models.GetReturnVmID
import com.clebs.celerity_admin.models.GetVehicleFuelLevelList
import com.clebs.celerity_admin.models.GetVehicleLocation
import com.clebs.celerity_admin.models.GetVehicleRequestType
import com.clebs.celerity_admin.models.GetvehicleOilLevelList
import com.clebs.celerity_admin.models.LastMileageInfo
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.models.LoginResponse
import com.clebs.celerity_admin.models.RepoInfoModel
import com.clebs.celerity_admin.models.VehicleReturnModelList
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/Authentication/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("/api/VehAllocHistories/GetCompanyList")
    suspend fun GetCompanyList(): Response<CompanyListResponse>

    @GET("/api/VehAllocHistories/GetActiveDriversList")
    suspend fun GetDriverList(): Response<DriverListResponseModel>

    @GET("/api/VehAllocHistories/GetVehicleList")
    suspend fun GetvehicleList(): Response<VehicleReturnModelList>

    @GET("/api/VehAllocHistories/GetLocationList")
    suspend fun GetVehicleLocationList(): Response<GetVehicleLocation>

    @GET("/api/VehAllocHistories/GetVehFuelLevelList")
    suspend fun GetVehicleFuelList(): Response<GetVehicleFuelLevelList>


    @GET("/api/VehAllocHistories/GetVehOilLevelList")
    suspend fun GetVehicleOilList(): Response<GetvehicleOilLevelList>

    @GET("/api/VehAllocHistories/GetCurrentVehicleWithDDMandateDone/{driverId}")
    suspend fun GetDDAALlocatedVehandLocation(@Path("driverId") DAId: String): Response<DDAMandateModel>

    @GET("/api/VehAllocHistories/GetCurrentAllocatedDa/{vmId}")
    suspend fun GetCurrentAllocatedDAforReturnVehicle(@Path("vmId") vmId: String): Response<GetReturnVmID>

    @GET("/api/VehAllocHistories/GetVehicleRepoInfo/{vmId}")
    suspend fun GetvehicleRepoInfo(@Path("vmId") vmId: String): Response<RepoInfoModel>

    @GET("/api/VehAllocHistories/GetVehicleDamageWorkingStatus")
    suspend fun GetVehicleRequestType(): Response<GetVehicleRequestType>

    @GET("/api/Drivers/GetDAEmergencyContact/{userId}")
    suspend fun GetDAEmergencyContact(@Path("userId") userId: Int): Response<String>

    @GET("/api/VehAllocHistories/GetVehicleLastMileageInfo/{vmId}")
    suspend fun GetVehicleLastMileage(@Path("userId") vmID: String): Response<LastMileageInfo>
}
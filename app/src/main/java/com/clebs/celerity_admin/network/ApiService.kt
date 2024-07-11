package com.clebs.celerity_admin.network

import androidx.camera.core.processing.SurfaceProcessorNode.In
import com.clebs.celerity_admin.models.CompanyListResponse
import com.clebs.celerity_admin.models.DDAMandateModel
import com.clebs.celerity_admin.models.DriverListResponseModel
import com.clebs.celerity_admin.models.GetReturnVmID
import com.clebs.celerity_admin.models.GetVehOilLevelListResponse
import com.clebs.celerity_admin.models.GetVehWindScreenConditionStatusResponse
import com.clebs.celerity_admin.models.GetVehicleFuelLevelList
import com.clebs.celerity_admin.models.GetVehicleLocation
import com.clebs.celerity_admin.models.GetVehicleRequestType
import com.clebs.celerity_admin.models.GetWeeklyDefectCheckImagesResponse
import com.clebs.celerity_admin.models.GetvehicleOilLevelList
import com.clebs.celerity_admin.models.LastMileageInfo
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.models.LoginResponse
import com.clebs.celerity_admin.models.RepoInfoModel
import com.clebs.celerity_admin.models.SucessStatusMsgResponse
import com.clebs.celerity_admin.models.VehicleReturnModelList
import com.clebs.celerity_admin.models.WeekYearModel
import com.clebs.celerity_admin.models.WeeklyDefectChecksModel
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("/api/VehAllocHistories/GetISO8601WeekandYear")
    suspend fun getCurrentWeekAndYear(): Response<WeekYearModel>

    @GET("/api/WeeklyDefectSheet/GetWeeklyDefectSheetCheckList")
    suspend fun getWeeklyDefectCHeckList(
        @Query("weekNo") weekno: Double,
        @Query("year") year: Double,
        @Query("driverId") driverId: Double,
        @Query("LmId") LmId: Double,
        @Query("showDefectedOnly") showDefectedOnly: Boolean
    ): Response<WeeklyDefectChecksModel>

    @GET("/api/WeeklyDefectSheet/GetWeeklyDefectCheckImages/{vdhCheckId}")
    suspend fun GetWeeklyDefectCheckImages(@Path("vdhCheckId") vdhCheckId: Int): Response<GetWeeklyDefectCheckImagesResponse>

    @GET("/api/WeeklyDefectSheet/GetVehOilLevelList")
    suspend fun GetVehOilLevelList(): Response<GetVehOilLevelListResponse>

    @GET("/api/WeeklyDefectSheet/GetVehWindScreenConditionStatus")
    suspend fun GetVehWindScreenConditionStatus(): Response<GetVehWindScreenConditionStatusResponse>

    @POST("/api/Vehicle/UploadVehOSMDefectChkFile")
    @Multipart
    suspend fun UploadVehOSMDefectChkFile(
        @Query("vdhDefectCheckId") vdhDefectCheckId:Int,
        @Query("fileType") fileType:String,
        @Query("date") date:String,
        @Part image:MultipartBody.Part
    ):Response<SucessStatusMsgResponse>
}
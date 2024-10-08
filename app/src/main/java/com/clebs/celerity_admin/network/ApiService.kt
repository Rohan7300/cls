package com.clebs.celerity_admin.network

import com.clebs.celerity_admin.models.CompanyListResponse
import com.clebs.celerity_admin.models.DDAMandateModel
import com.clebs.celerity_admin.models.DriverListResponseModel
import com.clebs.celerity_admin.models.GetAllDriversInspectionListResponse
import com.clebs.celerity_admin.models.GetAllVehicleInspectionListResponse
import com.clebs.celerity_admin.models.GetCurrentAllocatedDaResponse
import com.clebs.celerity_admin.models.GetCurrentInsuranceInfo
import com.clebs.celerity_admin.models.GetReturnVehicleListResponse
import com.clebs.celerity_admin.models.GetReturnVmID
import com.clebs.celerity_admin.models.GetVehOilLevelListResponse
import com.clebs.celerity_admin.models.GetVehWindScreenConditionStatusResponse
import com.clebs.celerity_admin.models.GetVehicleCollectionHistoryResponse
import com.clebs.celerity_admin.models.GetVehicleDamageWorkingStatusResponse
import com.clebs.celerity_admin.models.GetVehicleFuelLevelList
import com.clebs.celerity_admin.models.GetVehicleLocation
import com.clebs.celerity_admin.models.GetVehicleRequestType
import com.clebs.celerity_admin.models.GetVehicleReturnHistoryResponse
import com.clebs.celerity_admin.models.GetWeeklyDefectCheckImagesResponse
import com.clebs.celerity_admin.models.GetvehicleOilLevelList
import com.clebs.celerity_admin.models.LastMileageInfo
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.models.LoginResponse
import com.clebs.celerity_admin.models.OtherDefectCheckImagesInDropBoxResponse
import com.clebs.celerity_admin.models.RepoInfoModel
import com.clebs.celerity_admin.models.ResponseInspectionDone
import com.clebs.celerity_admin.models.ReturnVehicleToDepoRequest
import com.clebs.celerity_admin.models.SaveInspectionRequestBody
import com.clebs.celerity_admin.models.SaveDefectSheetWeeklyOSMCheckRequest
import com.clebs.celerity_admin.models.SaveVehicleBreakDownInspectionRequest
import com.clebs.celerity_admin.models.SucessStatusMsgResponse
import com.clebs.celerity_admin.models.VehicleReturnModelList
import com.clebs.celerity_admin.models.WeekYearModel
import com.clebs.celerity_admin.models.WeeklyDefectChecksModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
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
        @Query("vdhDefectCheckId") vdhDefectCheckId: Int,
        @Query("fileType") fileType: String,
        @Query("date") date: String,
        @Part image: MultipartBody.Part
    ): Response<SucessStatusMsgResponse>

    @POST("/api/WeeklyDefectSheet/SaveVehWeeklyDefectSheetInspectionInfo")
    suspend fun SaveVehWeeklyDefectSheetInspectionInfo(@Body body: SaveInspectionRequestBody): Response<SucessStatusMsgResponse>


    @GET("/api/WeeklyDefectSheet/GetVehWeeklyDefectSheetInspectionInfo/{vdhCheckId}")
    suspend fun GetVehWeeklyDefectSheetInspectionInfo(@Path("vdhCheckId") vdhCheckId: Int): Response<ResponseInspectionDone>

    @POST("/api/WeeklyDefectSheet/SaveDefectSheetWeeklyOSMCheck")
    suspend fun SaveDefectSheetWeeklyOSMCheck(
        @Body request: SaveDefectSheetWeeklyOSMCheckRequest
    ): Response<SucessStatusMsgResponse>


    @Multipart
    @POST("/api/Vehicle/UploadVan360VideoFile")
    suspend fun UploadVideo360(
        @Query("vdhDefectCheckId") vdhDefectCheckId: Int,
        @Query("date") date: String, @Part video: MultipartBody.Part
    ): Response<SucessStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadOtherPictureOfPartsFile")
    suspend fun UploadOtherPictureOfPartsFile(
        @Query("vdhDefectCheckId") vdhDefectCheckId: Int,
        @Query("fileType") fileType: String,
        @Query("date") date: String,
        @Part image: MultipartBody.Part
    ): Response<SucessStatusMsgResponse>

    @GET("/api/WeeklyDefectSheet/GetOtherDefectCheckImagesInDropBox")
    suspend fun GetOtherDefectCheckImagesInDropBox(
        @Query("vdhDefectCheckId") vdhDefectCheckId: Int,
        @Query("fileType") fileType: String
    ): Response<OtherDefectCheckImagesInDropBoxResponse>

    @GET("/api/WeeklyDefectSheet/GetLocationListbyUserId/{userId}")
    suspend fun GetLocationListbyUserId(@Path("userId") userId: Double): Response<GetVehicleLocation>

    @GET("/api/VehAllocHistories/GetAllVehicleInspectionList")
    suspend fun GetAllVehicleInspectionList(): Response<GetAllVehicleInspectionListResponse>

    @GET("/api/VehAllocHistories/GetAllDriversInspectionList")
    suspend fun GetAllDriversInspectionList(): Response<GetAllDriversInspectionListResponse>

    @GET("/api/VehAllocHistories/GetVehicleDamageWorkingStatus")
    suspend fun GetVehicleDamageWorkingStatus(): Response<GetVehicleDamageWorkingStatusResponse>

    @POST("/api/VehAllocHistories/SaveVehicleBreakDownInspectionInfo")
    suspend fun SaveVehicleBreakDownInspectionInfo(@Body requestBody: SaveVehicleBreakDownInspectionRequest): Response<SucessStatusMsgResponse>

    @GET("/api/VehAllocHistories/GetCurrentAllocatedDa/{vmId}")
    suspend fun GetCurrentAllocatedDa(
        @Path("vmId") vmId: String,
        @Query("isVehReturned") isVehReturned: Boolean
    ): Response<GetCurrentAllocatedDaResponse>

    @GET("/api/VehAllocHistories/GetReturnVehicleList")
    suspend fun GetReturnVehicleList():Response<GetReturnVehicleListResponse>

    @GET("/api/HtmlToPDF/DownloadVehicleHireAgreementPDF")
    suspend fun DownloadVehicleHireAgreementPDF():Response<ResponseBody>
    @GET("/api/HtmlToPDF/DownloadVehicleSignOutHireAgreementPDF")
    suspend fun DownloadVehicleSignOutHireAgreementPDF():Response<ResponseBody>

    @POST("/api/VehAllocHistories/ReturnVehicleToDepo")
    suspend fun ReturnVehicleToDepo(
        @Body request:ReturnVehicleToDepoRequest
    ):Response<SucessStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehAccidentPictureFile")
    suspend fun UploadVehAccidentPictureFile(
        @Query("userId") userId: Int,
        @Query("date") date: String,
        @Part image:MultipartBody.Part
    ):Response<SucessStatusMsgResponse>

    @GET("/api/VehAllocHistories/GetVehicleCurrentInsuranceInfo/{vmId}")
    suspend fun GetVehicleCurrentInsuranceInfo(@Path("vmId") vmId: Int): Response<GetCurrentInsuranceInfo>

    @GET("/api/VehAllocHistories/CreateNewVehicleReleaseTicket")
    suspend fun CreateVehicleReleaseReq(
        @Query("vmId") vmId: Double,
        @Query("supervisorId") supervisorId: Double):Response<SucessStatusMsgResponse>

    @POST("/api/Vehicle/UploadVehSpearWheelPictureFile")
    suspend fun UploadVehSpearWheelPictureFile(
        @Query("supervisorId") supervisorId:Int,
        @Query("driverId") driverId:Int,
        @Query("date") date:String
    ):Response<SucessStatusMsgResponse>
    @POST("/api/Vehicle/UploadVehInterierPictureFile")
    suspend fun UploadVehInterierPictureFile(
        @Query("supervisorId") supervisorId:Int,
        @Query("driverId") driverId:Int,
        @Query("date") date:String
    ):Response<SucessStatusMsgResponse>
    @POST("/api/Vehicle/UploadVehLoadingInteriorPictureFile")
    suspend fun UploadVehLoadingInteriorPictureFile(
        @Query("supervisorId") supervisorId:Int,
        @Query("driverId") driverId:Int,
        @Query("date") date:String
    ):Response<SucessStatusMsgResponse>
    @POST("/api/Vehicle/UploadVehToolsPictureFile")
    suspend fun UploadVehToolsPictureFile(
        @Query("supervisorId") supervisorId:Int,
        @Query("driverId") driverId:Int,
        @Query("date") date:String
    ):Response<SucessStatusMsgResponse>

    @GET("/api/VehAllocHistories/GetVehicleReturnHistoryById")
    suspend fun GetVehicleReturnHistory(
        @Query("supervisorId") supervisorId:Int,
        @Query("includeReturned") includeReturned:Boolean
    ):Response<GetVehicleReturnHistoryResponse>
    @GET("/api/VehAllocHistories/GetVehicleCollectionHistoryById")
    suspend fun GetVehicleCollectionHistory(
        @Query("supervisorId") supervisorId:Int,
        @Query("includeReturned") includeReturned:Boolean
    ):Response<GetVehicleCollectionHistoryResponse>
}
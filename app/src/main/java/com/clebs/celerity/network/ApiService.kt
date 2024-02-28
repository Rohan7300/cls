package com.clebs.celerity.network

import com.clebs.celerity.models.requests.AddOnRouteInfoRequest
import com.clebs.celerity.models.requests.CreateDaikyworkRequestBody
import com.clebs.celerity.models.requests.GetDefectSheetBasicInfoRequestModel
import com.clebs.celerity.models.requests.GetDriverBasicInfoRequest
import com.clebs.celerity.models.response.DriversBasicInformationModel
import com.clebs.celerity.models.response.GetVechileInformationResponse
import com.clebs.celerity.models.response.GetsignatureInformation

import com.clebs.celerity.models.requests.LoginRequest
import com.clebs.celerity.models.response.LoginResponse
import com.clebs.celerity.models.requests.SaveBreakStartEndTImeRequestModel
import com.clebs.celerity.models.requests.SaveDriverDocumentSignatureRequest
import com.clebs.celerity.models.requests.SaveVechileDefectSheetRequest
import com.clebs.celerity.models.requests.UpdateDriverAgreementSignatureRequest
import com.clebs.celerity.models.requests.logoutModel
import com.clebs.celerity.models.response.BaseResponse
import com.clebs.celerity.models.response.BaseResponseTwo

import com.clebs.celerity.models.response.CheckIFTodayCheckIsDone
import com.clebs.celerity.models.response.DailyWorkInfoByIdResponse
import com.clebs.celerity.models.response.GetDailyWorkDetailsResponse
import com.clebs.celerity.models.response.GetDefectSheetBasicInfoResponse
import com.clebs.celerity.models.response.GetDriverSignatureInformationResponse
import com.clebs.celerity.models.response.GetRideAlongRouteTypeInfoResponse
import com.clebs.celerity.models.response.GetRouteLocationInfoResponse
import com.clebs.celerity.models.response.GetVehicleDefectSheetInfoResponse
import com.clebs.celerity.models.response.GetVehicleImageUploadInfoResponse
import com.clebs.celerity.models.response.SaveVehDefectSheetResponse
import com.clebs.celerity.models.response.SimpleStatusMsgResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/Authentication/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("/api/Vehicle/GetVehicleInformation")
    suspend fun getVehicleInformation(
        @Query("userId") userId: Double,
        @Query("lmId") lmId: Double,
        @Query("vehRegNo") vehRegNo: String
    ): Response<GetVechileInformationResponse>


    @GET("/api/Drivers/GetDriverSignatureInformation/{userId}")
    suspend fun getDriverSignatureInfoforPolicy(@Path("userId") userId: Double): Response<GetsignatureInformation>

    @GET("/api/Home/Logout")
    suspend fun Logout(): Response<logoutModel>

    @GET("/api/Drivers/GetDriverBasicInformation/{userId}")
    suspend fun GetDriversBasicInfo(@Path("userId") userId: Double): Response<DriversBasicInformationModel>

    @POST("/api/DaDailyWorks/SaveBreakStartAndEndTime")
    suspend fun SaveBreakStartEndTime(@Body body: SaveBreakStartEndTImeRequestModel): Response<BaseResponseTwo>

    @POST("/api/DaDailyWorks/DeleteBreakTime/{dawDriverBreakId}")
    suspend fun deleteBreakTime(@Path("dawDriverBreakId") dawDriverBreakId: Int): Response<BaseResponseTwo>

    @POST("/api/DailyWorks/SaveVehDefectSheet")
    suspend fun SaveVichileDeffectSheet(@Body body: SaveVechileDefectSheetRequest): Response<BaseResponseTwo>

    @POST("/api/DailyWorks/GetDefectSheetBasicInfo")
    suspend fun GetDefectSheetBasicInfo(@Body body: GetDefectSheetBasicInfoRequestModel): Response<GetDefectSheetBasicInfoResponse>


    @PUT("/api/Drivers/UpdateUsernameFromEmail")
    suspend fun UseEmailAsUsername(
        @Query("userId") userId: Double,
        @Query("emailAddress") emailAddress: String
    ): Response<BaseResponseTwo>

    @PUT("/api/Drivers/UpdateDAProfileIn90Days")
    suspend fun updateDAProfile90days(
        @Query("userId") userId: Double,
        @Query("emailAddress") emailAddress: String,
        @Query("phonenumber") phonenumber: String
    ): Response<BaseResponseTwo>


    @POST("/api/DailyWorks/CheckIfTodayDefecChecktIsDone")
    suspend fun CheckifTodayCheckIsDone(): Response<CheckIFTodayCheckIsDone>

    @GET("/api/DailyWorks/GetDetailsDailyWork/{dwId}")
    suspend fun GetDailyworkDetails(@Path("dwid") dwid: Double): Response<GetDailyWorkDetailsResponse>

    @POST("/api/DailyWorks/CreateDailyWork")
    suspend fun createDailyWork(@Body body: CreateDaikyworkRequestBody): Response<BaseResponseTwo>

    @POST("/api/Drivers/SaveDriverDocumentSingature")
    suspend fun saveDriversDocumentSignature(@Body body: SaveDriverDocumentSignatureRequest): Response<BaseResponseTwo>

    @GET("/api/DailyWorks/GetVehicleDefectSheetInfo/{userId}")
    suspend fun GetVehicleDefectSheetInfo(@Path("userId") userId: Int): Response<GetVehicleDefectSheetInfoResponse>

    @POST("/api/Vehicle/SaveVehDefectSheet")
    suspend fun SaveVehDefectSheet(@Body body: SaveVechileDefectSheetRequest): Response<SaveVehDefectSheetResponse>

    @GET("/api/Vehicle/GetVehicleInformation")
    suspend fun GetVehicleInformation(
        @Query("userId") userId: Int,
        @Query("vehRegNo") vehRegNo: String = ""
    ): Response<GetVechileInformationResponse>

    @GET("/api/DailyWorks/GetVehicleImageUploadedInfo/{userId}")
    suspend fun GetVehicleImageUploadInfo(
        @Path("userId") userId: Int
    ): Response<GetVehicleImageUploadInfoResponse>

    @Multipart
    @POST("/api/Vehicle/UploadFaceMaskFile")
    suspend fun UploadFaceMaskFile(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleDashBoardPictureFile")
    suspend fun uploadVehicleDashboardImage(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleFrontPictureFile")
    suspend fun uploadVehFrontImage(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleNearSidePictureFile")
    suspend fun uploadVehNearSideImage(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleRearPictureFile")
    suspend fun uploadVehRearImage(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleOilLevelFile")
    suspend fun UploadVehicleOilLevelFile(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleOffSidePictureFile")
    suspend fun uploadVehOffSideImage(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @Multipart
    @POST("/api/Vehicle/UploadVehicleAddBlueFile")
    suspend fun UploadVehicleAddBlueFile(
        @Query("userId") userId: Int,
        @Part image: MultipartBody.Part
    ): Response<SimpleStatusMsgResponse>

    @GET("/api/DailyWorks/GetDailyWorkInfobyId/{userId}")
    suspend fun GetDailyWorkInfobyId(
        @Path("userId") userId: Int
    ): Response<DailyWorkInfoByIdResponse>

    @GET("/api/RouteUpdate/GetRouteLocationInfo/{locationId}")
    suspend fun GetRouteLocationInfo(
        @Path("locationId") locationId: Int
    ): Response<GetRouteLocationInfoResponse>

    @GET("/api/RouteUpdate/GetRideAlongRouteTypeInfo/{driverId}")
    suspend fun GetRideAlongRouteTypeInfo(
        @Path("driverId") userId: Int
    ): Response<GetRideAlongRouteTypeInfoResponse>

    @GET("/api/Drivers/GetDriverSignatureInformation/{userId}")
    suspend fun GetDriverSignatureInformation(@Path("userId") userId: Int): Response<GetDriverSignatureInformationResponse>

    @POST("/api/Drivers/UpdateDriverAgreementSignature")
    suspend fun UpdateDriverAgreementSignature(@Body updateDriverAgreementSignatureRequest: UpdateDriverAgreementSignatureRequest): Response<SimpleStatusMsgResponse>

    @POST("/api/RouteUpdate/AddOnRouteInfo")
    suspend fun AddOnRouteInfo(@Body addOnRouteInfoRequest: AddOnRouteInfoRequest): Response<SimpleStatusMsgResponse>
}
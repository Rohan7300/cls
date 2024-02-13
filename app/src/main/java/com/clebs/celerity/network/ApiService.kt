package com.clebs.celerity.network

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
import com.clebs.celerity.models.requests.logoutModel
import com.clebs.celerity.models.response.BaseResponse

import com.clebs.celerity.models.response.CheckIFTodayCheckIsDone
import com.clebs.celerity.models.response.GetDailyWorkDetailsResponse
import com.clebs.celerity.models.response.GetDefectSheetBasicInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/Authentication/login")
    suspend fun  login(@Body body: LoginRequest):Response<LoginResponse>

    @GET("/api/Vehicle/GetVehicleInformation")
    suspend fun getVehicleInformation(@Query("userId") userId:Double, @Query("lmId") lmId:Double, @Query("vehRegNo") vehRegNo:String):Response<GetVechileInformationResponse>


    @GET("/api/Drivers/GetDriverSignatureInformation/{userId}")
    suspend fun getDriverSignatureInfoforPolicy(@Path("userId") userId: Double):Response<GetsignatureInformation>

    @GET("/api/Home/Logout")
    suspend fun Logout():Response<logoutModel>

    @POST("/api/Drivers/GetDriverBasicInformation")
    suspend fun  GetDriversBasicInfo(@Body body: GetDriverBasicInfoRequest):Response<DriversBasicInformationModel>


    @POST("/api/DaDailyWorks/SaveBreakStartAndEndTime")
    suspend fun  SaveBreakStartEndTime(@Body body: SaveBreakStartEndTImeRequestModel):Response<BaseResponse>

    @POST("/api/DailyWorks/SaveVehDefectSheet")
    suspend fun  SaveVichileDeffectSheet(@Body body: SaveVechileDefectSheetRequest):Response<BaseResponse>

    @POST("/api/DailyWorks/GetDefectSheetBasicInfo")
    suspend fun GetDefectSheetBasicInfo(@Body body: GetDefectSheetBasicInfoRequestModel) : Response<GetDefectSheetBasicInfoResponse>

    @POST("/api/DailyWorks/CheckIfTodayDefecChecktIsDone")
    suspend fun CheckifTodayCheckIsDone() :Response<CheckIFTodayCheckIsDone>

    @GET("/api/DailyWorks/GetDetailsDailyWork/{dwId}")
    suspend fun GetDailyworkDetails(@Path("dwid") dwid: Double):Response<GetDailyWorkDetailsResponse>

    @POST("/api/DailyWorks/CreateDailyWork")
    suspend fun createDailyWork(@Body body: CreateDaikyworkRequestBody) :Response<BaseResponse>

    @POST("/api/Drivers/SaveDriverDocumentSingature")
    suspend fun  saveDriversDocumentSignature(@Body body: SaveDriverDocumentSignatureRequest) : Response<BaseResponse>

}
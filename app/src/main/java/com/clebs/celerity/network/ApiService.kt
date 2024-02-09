package com.clebs.celerity.network

import com.clebs.celerity.models.GetVechileInformationResponse
import com.clebs.celerity.models.GetsignatureInformation

import com.clebs.celerity.models.LoginRequest
import com.clebs.celerity.models.LoginResponse
import com.clebs.celerity.models.logoutModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("api/Authentication/login")
    suspend fun  login(@Body body: LoginRequest ):Response<LoginResponse>

    @GET("/api/Vehicle/GetVehicleInformation")
    suspend fun getVehicleInformation(@Query("userId") userId:Double, @Query("lmId") lmId:Double, @Query("vehRegNo") vehRegNo:String):Response<GetVechileInformationResponse>


    @GET("/api/Drivers/GetDriverSignatureInformation/{userId}")
    suspend fun getDriverSignatureInfoforPolicy(@Path("userId") userId: Double):Response<GetsignatureInformation>

    @GET("/api/Home/Logout")
    suspend fun Logout():Response<logoutModel>

}
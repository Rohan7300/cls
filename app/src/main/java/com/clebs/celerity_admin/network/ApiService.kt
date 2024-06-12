package com.clebs.celerity_admin.network

import com.clebs.celerity_admin.models.CompanyListResponse
import com.clebs.celerity_admin.models.DriverListResponseModel
import com.clebs.celerity_admin.models.LoginRequest
import com.clebs.celerity_admin.models.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/Authentication/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @GET("/api/VehAllocHistories/GetCompanyList")
    suspend fun GetCompanyList(): Response<CompanyListResponse>

    @GET("/api/VehAllocHistories/GetActiveDriversList")
    suspend fun GetDriverList(): Response<DriverListResponseModel>
}
package com.clebs.celerity.network

import com.ais.plate_req_api.model.NumberPlateResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiPlateRecognizer {
    @Multipart
    @POST("plate-reader")
    suspend fun getNumberPlateDetails(
        @Header("Authorization") token: String,
        @Part imagePart : MultipartBody.Part
    ) : Response<NumberPlateResponse>
}
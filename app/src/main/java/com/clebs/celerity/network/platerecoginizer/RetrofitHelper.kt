package com.ais.plate_req_api.webService

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {

    val baseUrl = "https://api.platerecognizer.com/v1/"

    fun getInstance(): Retrofit {
        val builder = OkHttpClient.Builder()

        builder.readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)

        val clientSetup = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES) // write timeout
            .readTimeout(5, TimeUnit.MINUTES) // read timeout
            .build()
        return Retrofit.Builder().baseUrl(baseUrl).client(clientSetup)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
}
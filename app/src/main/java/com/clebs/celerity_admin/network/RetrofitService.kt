package com.clebs.celerity_admin.network


import android.annotation.SuppressLint
import android.util.Log
import com.clebs.celerity_admin.LoginActivityTwo
import com.clebs.celerity_admin.SplashActivityTwo
import com.clebs.celerity_admin.ui.App

import com.clebs.celerity_admin.utils.Prefs
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object RetrofitService {
    //private const val BASE_URL = "http://182.64.1.105:8119/"
  private const val BASE_URL = "http://122.176.42.96:8119/"
// private const val BASE_URL = "http://122.186.85.26:8119/"
//private const val BASE_URL = "http://192.168.0.150:8119/"
//   private const val BASE_URL = "https://api.clsdasystem.com/"
    //private const val BASE_URL = "https://api.clsdasystem.com/"

    fun getInstance(): Retrofit {
        val builder = OkHttpClient.Builder()

        builder.readTimeout(5, TimeUnit.MINUTES)

            .writeTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(logging)

        return Retrofit.Builder().baseUrl(BASE_URL)
            .client(
                getUnSecureOkHttpClient(
                    provideHeaderInterceptor(),
                    provideHttpLoggingInterceptor()
                )
            )
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

    fun getUnSecureOkHttpClient(
        headerInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val trustAllCerts = arrayOf<TrustManager>(@SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                return arrayOf()
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(arg0: Array<X509Certificate?>?, arg1: String?) {
                // Not implemented
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(arg0: Array<X509Certificate?>?, arg1: String?) {
                // Not implemented
            }
        })

        try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            val sslSocketFactory = sslContext.socketFactory
            val builder = OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .connectTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(headerInterceptor)


            return builder.build()
        } catch (e: Exception) {
            e.printStackTrace()
            return getSecureOkHttpClient(headerInterceptor, loggingInterceptor)
        }
    }

    fun getSecureOkHttpClient(
        headerInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(headerInterceptor)
            .build()
    }

    private fun provideHeaderInterceptor(): Interceptor {
        val applicationContext = App.instance
        try {
            return Interceptor { chain ->
                val accessToken: String = Prefs.getInstance(applicationContext).accessToken
                Log.d("Net Interceptor", "Internet Available")
                if (Prefs.getInstance(applicationContext).accessToken.isNotEmpty()) {
                    val request: Request = chain.request().newBuilder()
                        .addHeader("accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer $accessToken").build()
                    chain.proceed(request)
                } else {
                    val request: Request = chain.request().newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Accept", "application/json").build()
                    chain.proceed(request)
                }
            }
        } catch (_: Exception) {
            throw Exception()
        }
    }


    private fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor =
            HttpLoggingInterceptor { message ->
//                Log.d("API Logging", "response => $message")
                val maxLogSize = 4000
                for (i in 0..message.length / maxLogSize) {
                    val start = i * maxLogSize
                    var end = (i + 1) * maxLogSize
                    end = if (end > message.length) message.length else end
                    Log.d("API Logging", "response => ${message.substring(start, end)}")
                }
            }
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return httpLoggingInterceptor
    }


}

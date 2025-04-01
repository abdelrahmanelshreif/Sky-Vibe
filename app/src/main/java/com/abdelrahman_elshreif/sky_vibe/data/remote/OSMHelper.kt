package com.abdelrahman_elshreif.sky_vibe.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object OSMHelper {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .addHeader(
                    "User-Agent",
                    "SkyVibe/1.0 (Sky-Vibe; link93955@gmail.com)"
                )
                .build()
            chain.proceed(newRequest)
        }
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofitInstance =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val apiService = retrofitInstance.create(OSMApiServices::class.java)
}
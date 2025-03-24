package com.abdelrahman_elshreif.sky_vibe.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface SkyVibeApiServices {


    object RetrofitHelper {
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        private val retrofitInstance =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val apiservice = retrofitInstance.create(SkyVibeApiServices::class.java)
    }
}
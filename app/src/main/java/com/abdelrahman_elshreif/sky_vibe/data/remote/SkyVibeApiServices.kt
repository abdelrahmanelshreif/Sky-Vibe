package com.abdelrahman_elshreif.sky_vibe.data.remote


import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SkyVibeApiServices {

    companion object {
        const val APIKEY: String = "e315959fbe479848d4ca3ee9d1301721"
    }

    @GET("onecall")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("exclude") exclude: String = "minutely",
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "ar",
        @Query("appid") apiKey: String = APIKEY
    ): Response<WeatherResponse>


}
package com.abdelrahman_elshreif.sky_vibe.data.remote

import com.abdelrahman_elshreif.sky_vibe.model.WeatherForecastResponse
import com.abdelrahman_elshreif.sky_vibe.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SkyVibeApiServices {

    companion object {
        const val APIKEY: String = "e315959fbe479848d4ca3ee9d1301721"
    }

    @GET("weather")
    suspend fun getWeatherByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en",
        @Query("appid") apiKey: String = APIKEY
    ): Response<WeatherResponse>


    @GET("forecast")
    suspend fun getForecastByCoordinates(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "en",
        @Query("appid") apiKey: String = APIKEY
    ): Response<WeatherForecastResponse>
}
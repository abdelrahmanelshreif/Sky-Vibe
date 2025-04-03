package com.abdelrahman_elshreif.sky_vibe.data.remote

import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import retrofit2.http.GET
import retrofit2.http.Query

interface OSMApiServices {

    @GET("search")
    suspend fun searchLocations(
        @Query("q") query:String,
        @Query("format") format:String = "json",
        @Query("limit") limit:Int = 5
    ):List<NominatimLocation>


    object Nom
}
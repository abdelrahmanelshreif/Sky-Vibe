package com.abdelrahman_elshreif.sky_vibe.data.model


sealed class Response {
    data object Loading : Response()
    data class Success(val data: WeatherResponse) : Response()
    data class Failure(val error: Throwable) : Response()

}

package com.abdelrahman_elshreif.sky_vibe.data.remote

import com.abdelrahman_elshreif.sky_vibe.data.model.CurrentWeather
import com.abdelrahman_elshreif.sky_vibe.data.model.DailyWeather
import com.abdelrahman_elshreif.sky_vibe.data.model.FeelsLike
import com.abdelrahman_elshreif.sky_vibe.data.model.HourlyWeather
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.Temperature
import com.abdelrahman_elshreif.sky_vibe.data.model.Weather
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse

object FakeWeatherResponseFactory {
    fun createFakeWeatherResponse() = WeatherResponse(
        lat = 30.0,
        lon = 31.0,
        timezone = "Africa/Cairo",
        timezoneOffset = 7200,
        current = createFakeCurrentWeather(),
        hourly = createFakeHourlyWeatherList(),
        daily = createFakeDailyWeatherList()
    )

    private fun createFakeCurrentWeather() = CurrentWeather(
        dt = System.currentTimeMillis() / 1000,
        sunrise = System.currentTimeMillis() / 1000 - 21600,
        sunset = System.currentTimeMillis() / 1000 + 21600,
        temp = 25.0,
        feelsLike = 26.0,
        pressure = 1013,
        humidity = 65,
        dewPoint = 20.0,
        uvi = 5.0,
        clouds = 40,
        visibility = 10000,
        windSpeed = 5.0,
        windDeg = 180,
        weather = listOf(
            Weather(
                id = 800,
                main = "Clear",
                description = "clear sky",
                icon = "01d"
            )
        )
    )

    private fun createFakeHourlyWeatherList(): List<HourlyWeather> {
        val hourlyList = mutableListOf<HourlyWeather>()
        val currentTime = System.currentTimeMillis() / 1000

        for (i in 0..23) {
            hourlyList.add(
                HourlyWeather(
                    dt = currentTime + (i * 3600),
                    temp = 20.0 + (Math.random() * 10),
                    feelsLike = 21.0 + (Math.random() * 10),
                    pressure = 1013,
                    humidity = 65,
                    dewPoint = 15.0,
                    uvi = 5.0,
                    clouds = 40,
                    visibility = 10000,
                    windSpeed = 5.0,
                    windDeg = 180,
                    weather = listOf(
                        Weather(
                            id = 800,
                            main = "Clear",
                            description = "clear sky",
                            icon = "01d"
                        )
                    ),
                    pop = 0.0,
                    windGust = 0.0
                )
            )
        }
        return hourlyList
    }

    private fun createFakeDailyWeatherList(): List<DailyWeather> {
        val dailyList = mutableListOf<DailyWeather>()
        val currentTime = System.currentTimeMillis() / 1000

        for (i in 0..6) {
            dailyList.add(
                DailyWeather(
                    dt = currentTime + (i * 86400),
                    sunrise = currentTime + (i * 86400) - 21600,
                    sunset = currentTime + (i * 86400) + 21600,
                    temp = Temperature(
                        day = 25.0 + (Math.random() * 5),
                        min = 20.0,
                        max = 30.0,
                        night = 18.0,
                        eve = 23.0,
                        morn = 21.0
                    ),
                    feelsLike = FeelsLike(
                        day = 26.0,
                        night = 19.0,
                        eve = 24.0,
                        morn = 22.0
                    ),
                    pressure = 1013,
                    humidity = 65,
                    dewPoint = 15.0,
                    windSpeed = 5.0,
                    windDeg = 180,
                    weather = listOf(
                        Weather(
                            id = 800,
                            main = "Clear",
                            description = "clear sky",
                            icon = "01d"
                        )
                    ),
                    clouds = 40,
                    pop = 0.0,
                    uvi = 5.0,
                    moonrise = 170000,
                    summary = "GOOD",
                    moonset = 170000,
                    moonPhase = 0.0,
                    windGust = null
                )
            )
        }
        return dailyList
    }

    fun createFakeLocationsList() = listOf(
        NominatimLocation(
            displayName = "Cairo, Egypt",
            lat = 30.0444,
            lon = 31.2357
        ),
        NominatimLocation(
            displayName = "Alexandria, Egypt",
            lat = 31.2001,
            lon = 29.9187
        ),
        NominatimLocation(
            displayName = "London, United Kingdom",
            lat = 51.5074,
            lon = -0.1278
        ),
        NominatimLocation(
            displayName = "New York City, New York, USA",
            lat = 40.7128,
            lon = -74.0060
        ),
        NominatimLocation(
            displayName = "Tokyo, Japan",
            lat = 35.6762,
            lon = 139.6503
        )
    )
}
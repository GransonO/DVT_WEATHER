package com.granson.dvtweather.data.api

import com.granson.dvtweather.data.models.weather.WeatherRequest
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("/data/2.5/onecall?exclude=hourly&units=metric")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("appid") apiKey: String
    ): WeatherRequest
}


package com.granson.dvtweather.data.repository.repos

import com.granson.dvtweather.data.models.weather.WeatherRequest
import com.granson.dvtweather.data.repository.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {

    suspend fun getCurrentWeather(
        lat: Float,
        lon: Float,
        apiKey: String
    ): Flow<Resource<WeatherRequest>>
}
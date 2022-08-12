package com.granson.dvtweather.data.repository.repos.repoImpl

import com.granson.dvtweather.data.api.WeatherService
import com.granson.dvtweather.data.models.weather.WeatherRequest
import com.granson.dvtweather.data.repository.BaseRepository
import com.granson.dvtweather.data.repository.Resource
import com.granson.dvtweather.data.repository.repos.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherService: WeatherService
): WeatherRepository, BaseRepository() {

    override suspend fun getCurrentWeather(lat: Float, lon: Float, apiKey: String): Flow<Resource<WeatherRequest>> = dvtAPICall {
        weatherService.getCurrentWeather(
            lat, lon,  apiKey
        )
    }

}
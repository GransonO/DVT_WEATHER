package com.granson.dvtweather.fake

import com.granson.dvtweather.data.models.weather.WeatherRequest
import com.granson.dvtweather.data.repository.Resource
import com.granson.dvtweather.data.repository.repos.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeWeatherRepositoryImpl: WeatherRepository {

    override suspend fun getCurrentWeather(
        lat: Float,
        lon: Float,
        apiKey: String
    ): Flow<Resource<WeatherRequest>> = flow {
        emit(Resource.Success(
            WeatherRequest()
        ))
    }

}
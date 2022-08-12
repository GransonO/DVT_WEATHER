package com.granson.dvtweather.presentation.composables.screens.viewModels.models

import com.granson.dvtweather.data.models.weather.WeatherRequest

data class SavedPlace(
    val location: PlaceLocation = PlaceLocation(),
    val name: String = "",
    val locality: String = "",
    val lastWeatherID: Int = 800,
    val date: String = "Last Update 24 June, 2022",
    val placeId: String = "",
    val placeWeather: WeatherRequest? = null
)
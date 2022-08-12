package com.granson.dvtweather.presentation.composables.screens.viewModels.models

import com.granson.dvtweather.data.models.weather.WeatherRequest

data class RequestState(
    var isLoading: Boolean = false,
    var data: WeatherRequest? = null,
    var error: String? = null
)

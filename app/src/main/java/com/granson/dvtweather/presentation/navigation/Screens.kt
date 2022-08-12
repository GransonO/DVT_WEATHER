package com.granson.dvtweather.presentation.navigation

import androidx.annotation.StringRes
import com.granson.dvtweather.R


sealed class Screen(val route: String, @StringRes val stringId: Int, val Icon: Int) {
    object WeatherScreen : Screen("weather_screen", R.string.weather_screen, R.drawable.ic_baseline_grain_24)
    object MapsScreen : Screen("maps_screen", R.string.maps_screen, R.drawable.ic_baseline_location_on_24)
}

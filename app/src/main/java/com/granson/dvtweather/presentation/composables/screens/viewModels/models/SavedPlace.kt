package com.granson.dvtweather.presentation.composables.screens.viewModels.models

import com.google.android.gms.maps.model.LatLng

data class SavedPlace(
    val location: LatLng = LatLng(0.0,0.0),
    val name: String = "",
    val locality: String = "",
    val lastWeatherID: Int = 800,
    val date: String = "Last Update 24 June, 2022",
    val placeId: String = ""
)
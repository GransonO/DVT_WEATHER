package com.granson.dvtweather.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

import androidx.room.PrimaryKey
import com.granson.dvtweather.data.models.weather.WeatherRequest
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.PlaceLocation

@Entity
class Places {
    @PrimaryKey
    var placeId = ""

    @ColumnInfo(name = "name")
    var name: String = ""

    @ColumnInfo(name = "locality")
    var locality: String = ""

    @ColumnInfo(name = "location")
    var location: PlaceLocation? = null

    @ColumnInfo(name = "lastWeatherID")
    var lastWeatherID: Int = 800

    @ColumnInfo(name = "date") // Update date
    var date: String = ""

    @ColumnInfo(name = "placeWeather")
    var placeWeather: WeatherRequest? = null
}

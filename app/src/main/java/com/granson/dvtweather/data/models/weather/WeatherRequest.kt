package com.granson.dvtweather.data.models.weather


import com.google.gson.annotations.SerializedName

data class WeatherRequest(
    @SerializedName("current")
    val current: Current = Current(),
    @SerializedName("daily")
    val daily: List<Daily> = listOf(),
    @SerializedName("lat")
    val lat: Double = 0.0,
    @SerializedName("lon")
    val lon: Double = 0.0,
    @SerializedName("timezone")
    val timezone: String = "",
    @SerializedName("timezone_offset")
    val timezoneOffset: Int = 0
)
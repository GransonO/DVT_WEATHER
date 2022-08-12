package com.granson.dvtweather.data.models.weather


import com.google.gson.annotations.SerializedName

data class Current(
    @SerializedName("clouds")
    val clouds: Double = 0.0,
    @SerializedName("temp")
    val temp: Double = 0.0,
    @SerializedName("weather")
    val weather: List<Weather> = listOf(),
)
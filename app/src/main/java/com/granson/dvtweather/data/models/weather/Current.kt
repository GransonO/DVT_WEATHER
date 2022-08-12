package com.granson.dvtweather.data.models.weather


import com.google.gson.annotations.SerializedName

data class Current(
    @SerializedName("clouds")
    val clouds: Double = 0.0,
    @SerializedName("dew_point")
    val dewPoint: Double = 0.0,
    @SerializedName("dt")
    val dt: Double = 0.0,
    @SerializedName("feels_like")
    val feelsLike: Double = 0.0,
    @SerializedName("humidity")
    val humidity: Double = 0.0,
    @SerializedName("pressure")
    val pressure: Double = 0.0,
    @SerializedName("sunrise")
    val sunrise: Double = 0.0,
    @SerializedName("sunset")
    val sunset: Double = 0.0,
    @SerializedName("temp")
    val temp: Double = 0.0,
    @SerializedName("uvi")
    val uvi: Double = 0.0,
    @SerializedName("visibility")
    val visibility: Double = 0.0,
    @SerializedName("weather")
    val weather: List<Weather> = listOf(),
    @SerializedName("wind_deg")
    val windDeg: Double = 0.0,
    @SerializedName("wind_speed")
    val windSpeed: Double = 0.0
)
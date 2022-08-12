package com.granson.dvtweather.data.models.weather


import com.google.gson.annotations.SerializedName

data class Daily(
    @SerializedName("temp")
    val temp: Temp,
    @SerializedName("weather")
    val weather: List<Weather>
)
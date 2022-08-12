package com.granson.dvtweather.data.models.weather


import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("description")
    val description: String = "",
    @SerializedName("icon")
    val icon: String = "",
    @SerializedName("id")
    val id: Int = 800,
    @SerializedName("main")
    val main: String = ""
)
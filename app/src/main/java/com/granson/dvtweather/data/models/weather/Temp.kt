package com.granson.dvtweather.data.models.weather


import com.google.gson.annotations.SerializedName

data class Temp(
    @SerializedName("max")
    val max: Double,
    @SerializedName("min")
    val min: Double
)
package com.granson.dvtweather.utils

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.LatLng
import com.granson.dvtweather.data.models.LocationDetails
import com.granson.dvtweather.data.models.weather.Daily
import com.granson.dvtweather.enums.WeatherEnums
import java.text.SimpleDateFormat
import java.util.*


object Common {
    // Global Scope
    var selectedWeatherEnums = mutableStateOf(WeatherEnums.SUNNY)
    var mainWeatherEnums = mutableStateOf(WeatherEnums.SUNNY)
    var userCurrentLocation: LatLng = LatLng(-1.23456, 36.2345)
    var userLocationDetails = mutableStateOf(LocationDetails())
    val currentTemp = mutableStateOf("00")
    var dailyWeather = mutableStateOf(listOf<Daily>())
    var daysOrder = mutableStateOf(listOf<String>())

    //Maps Scope
    var selectedPlaceWeatherEnums = mutableStateOf(selectedWeatherEnums.value)

    val STORM_IDS = listOf( 800 )
    val CLOUDY_IDS = listOf( 801, 802, 803, 804 )
    val DRIZZLE_IDS = listOf( 300, 301, 302, 310, 311, 312, 313, 314, 321 )
    val RAIN_IDS = listOf( 500, 501, 502, 503, 504, 511, 520, 521, 522, 531 )
    val CLEAR_IDS = listOf( 200, 201, 202, 210, 211, 212, 221, 230, 231, 232 )
    val SNOW_IDS = listOf( 600, 601, 602, 611, 612, 613, 615, 616, 620, 621, 622 )

    const val UPDATE_INTERVAL = 5000L
    const val FASTEST_UPDATE_INTERVAL = 5000L
    const val DB_VERSION = 1

    fun baseLogger(title: String, value: Any?) {
        Log.e(title, " :> $value")
    }


    fun getWeatherEnum(value: Int): WeatherEnums {
        return when{
            STORM_IDS.contains(value) -> WeatherEnums.RAINY
            CLOUDY_IDS.contains(value) -> WeatherEnums.CLOUDY
            DRIZZLE_IDS.contains(value) -> WeatherEnums.RAINY
            RAIN_IDS.contains(value) -> WeatherEnums.RAINY
            CLEAR_IDS.contains(value) -> WeatherEnums.SUNNY
            SNOW_IDS.contains(value) -> WeatherEnums.RAINY
            else -> WeatherEnums.SUNNY
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun daysFlow(): List<String> {
        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val sdf = SimpleDateFormat("EEEE")
        val d = Date()
        val dayOfTheWeek: String = sdf.format(d)
        val passedDay = days.filter { it.equals(dayOfTheWeek, ignoreCase = true) }
        var currentIndex = days.indexOf(passedDay[0])

        // Get the next day
        if(currentIndex == 6)
            currentIndex = 1
        else
            currentIndex += 1

        val daysList = mutableListOf<String>()
        for(x in currentIndex..6){
             daysList.add(days[x])
        }

        if(daysList.size < 8){
            for(x in 0..currentIndex){
                daysList.add(days[x])
            }
        }

        return daysList
    }

    @SuppressLint("SimpleDateFormat")
    val getCurrentDate =  {
        val sdf = SimpleDateFormat("d MMM, yyyy")
        val d = Date()
        sdf.format(d)
    }
}

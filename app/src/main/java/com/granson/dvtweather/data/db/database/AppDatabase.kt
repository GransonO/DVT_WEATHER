package com.granson.dvtweather.data.db.database

import androidx.room.*
import com.google.gson.Gson
import com.granson.dvtweather.data.db.dao.WeatherDao
import com.granson.dvtweather.data.db.entities.Places
import com.granson.dvtweather.data.models.weather.WeatherRequest
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.PlaceLocation
import com.granson.dvtweather.utils.Common.DB_VERSION

@Database(entities = [
        Places::class
    ],
    version = DB_VERSION,
    exportSchema = false
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val weatherDao: WeatherDao
}

class Converter {
    @TypeConverter
    fun weatherObjToString(value: WeatherRequest) = Gson().toJson(value)

    @TypeConverter
    fun stringToWeatherObj(value: String) =
        Gson().fromJson(value, WeatherRequest::class.java)

    @TypeConverter
    fun placeToString(value: PlaceLocation) = Gson().toJson(value)

    @TypeConverter
    fun stringToPlace(value: String) =
        Gson().fromJson(value, PlaceLocation::class.java)

}
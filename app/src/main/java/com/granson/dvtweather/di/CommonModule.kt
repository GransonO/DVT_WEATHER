package com.granson.dvtweather.di

import android.content.Context
import android.content.SharedPreferences
import com.granson.dvtweather.R
import com.granson.dvtweather.enums.WeatherEnums
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommonModule {

    @Provides
    @Singleton
    fun provideInitialWeatherState(
        sharedPrefs: SharedPreferences
    ): WeatherEnums {
        return when (sharedPrefs.getString("last_weather_update", "")) {
            WeatherEnums.SUNNY.name -> WeatherEnums.SUNNY
            WeatherEnums.CLOUDY.name -> WeatherEnums.CLOUDY
            WeatherEnums.RAINY.name -> WeatherEnums.RAINY
            else -> WeatherEnums.SUNNY
        }
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(
        @ApplicationContext context: Context
    ): SharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
}
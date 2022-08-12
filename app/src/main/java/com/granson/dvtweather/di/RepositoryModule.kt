package com.granson.dvtweather.di

import com.granson.dvtweather.data.api.PlaceService
import com.granson.dvtweather.data.api.WeatherService
import com.granson.dvtweather.data.db.database.AppDatabase
import com.granson.dvtweather.data.repository.repos.DataRepository
import com.granson.dvtweather.data.repository.repos.PlaceRepository
import com.granson.dvtweather.data.repository.repos.WeatherRepository
import com.granson.dvtweather.data.repository.repos.repoImpl.DataRepositoryImpl
import com.granson.dvtweather.data.repository.repos.repoImpl.PlaceRepositoryImpl
import com.granson.dvtweather.data.repository.repos.repoImpl.WeatherRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePlaceRepository(apiService: PlaceService): PlaceRepository = PlaceRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideWeatherRepository(apiService: WeatherService): WeatherRepository = WeatherRepositoryImpl(apiService)

    @Provides
    @Singleton
    fun provideDatabaseRepository(dataService: AppDatabase): DataRepository = DataRepositoryImpl(dataService)
}
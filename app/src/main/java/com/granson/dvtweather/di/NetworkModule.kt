package com.granson.dvtweather.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.granson.dvtweather.data.api.PlaceService
import com.granson.dvtweather.data.api.WeatherService
import com.granson.dvtweather.data.repository.repos.PlaceRepository
import com.granson.dvtweather.data.repository.repos.WeatherRepository
import com.granson.dvtweather.data.repository.repos.repoImpl.PlaceRepositoryImpl
import com.granson.dvtweather.data.repository.repos.repoImpl.WeatherRepositoryImpl
import com.granson.dvtweather.utils.APIUtils.PLACE_URL
import com.granson.dvtweather.utils.APIUtils.WEATHER_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun retroLogger(): HttpLoggingInterceptor {
        val logger = HttpLoggingInterceptor()
        logger.setLevel(HttpLoggingInterceptor.Level.BODY)
        return logger
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val authRequest = chain.request().newBuilder()
                    .header("Content-Type", "application/json")
                    .build()

                return@addInterceptor chain.proceed(authRequest)
            }
            .addNetworkInterceptor(loggingInterceptor)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideWeatherService(okHttpClient: OkHttpClient): WeatherService {
        val retrofit = Retrofit.Builder()
            .baseUrl(WEATHER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(WeatherService::class.java)
    }

    @Provides
    @Singleton
    fun providePlaceService(okHttpClient: OkHttpClient): PlaceService {
        val retrofit = Retrofit.Builder()
            .baseUrl(PLACE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(PlaceService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppContext(@ApplicationContext context: Context): Context = context

}
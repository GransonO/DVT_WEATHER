package com.granson.dvtweather.presentation.composables.screens.viewModels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.granson.dvtweather.R
import com.granson.dvtweather.data.models.places.autocomplete.AutoComplete
import com.granson.dvtweather.data.models.weather.Weather
import com.granson.dvtweather.data.repository.Resource
import com.granson.dvtweather.data.repository.repos.PlaceRepository
import com.granson.dvtweather.data.repository.repos.WeatherRepository
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.RequestState
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.SavedPlace
import com.granson.dvtweather.utils.Common.baseLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScreensViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val placeRepository: PlaceRepository
): ViewModel() {

    private var _currentWeather = MutableStateFlow(RequestState())
    var  currentWeather= _currentWeather.asSharedFlow()

    val queryPlaces = mutableStateOf(AutoComplete())
    val listSavedPlaces = mutableStateOf(listOf(SavedPlace()))
    val selectedPlace = mutableStateOf(SavedPlace())

    val isRequesting =  mutableStateOf(false)
    private val _placeDetails = MutableStateFlow(SavedPlace())
    val placeDetails = _placeDetails.asSharedFlow()

    fun getWeatherInfo(
        lat: Float,
        lon: Float,
        context: Context
    ){
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = context.resources.getString(R.string.weather_api_key)
            ).collect{
                baseLogger("It Value", it is Resource.Success)
                when(it){
                    is Resource.Success -> {
                        _currentWeather.emit(
                            RequestState(
                                 isLoading = false,
                                 data = it.data ,
                                 error = null
                            )
                        )
                    }
                    is Resource.Error -> {

                        _currentWeather.emit(
                            RequestState(
                                isLoading = false,
                                data = null ,
                                error = it.message.toString()
                            )
                        )
                    }
                    is Resource.Loading -> {

//                        _currentWeather.emit(
//                            RequestState(
//                                isLoading = it.isLoading,
//                                data = null,
//                                error = null
//                            )
//                        )
                    }
                }
            }
        }
    }

    fun placeSearch(
        char: String,
        context: Context
    ){
        viewModelScope.launch {
            placeRepository.placeSearch(
                name = char,
                apiKey = context.resources.getString(R.string.maps_api_key)
            ).collect{
                when(it){
                    is Resource.Success -> {
                        isRequesting.value = false
                        if(it.data != null){
                            queryPlaces.value = it.data
                        }
                    }
                    is Resource.Error -> {
                        isRequesting.value = false
                    }
                    is Resource.Loading -> {

                    }
                }
            }
        }
    }

    fun getPlaceDetails(
        placeId: String,
        context: Context
    ){
        viewModelScope.launch {
            placeRepository.placeDetails(
                placeId = placeId,
                apiKey = context.resources.getString(R.string.maps_api_key)
            ).collect{
                when(it){
                    is Resource.Success -> {
                        val place = it.data
                        if(place != null){
                            _placeDetails.emit(
                                SavedPlace(
                                    location = LatLng(place.result.geometry.location.lat, place.result.geometry.location.lng),
                                    name = place.result.name,
                                    placeId = placeId
                                )
                            )
                        }
                    }
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {

                    }
                }
            }
        }
    }

    fun getSavedPlaces(){
        viewModelScope.launch {
            delay(3000)
            listSavedPlaces.value = listOf(
                SavedPlace(
                    location = LatLng(-1.2255328,36.2962995),
                    name = "Westlands",
                    lastWeatherID = 200,
                    date = "Last Update 21 June, 2022"
                ),
                SavedPlace(
                    location = LatLng(-1.3458328,36.4970995),
                    name = "Kilimani",
                    lastWeatherID = 500,
                    date = "Last Update 23 June, 2022"
                ),
                SavedPlace(
                    location = LatLng(-1.2895328,36.8972995),
                    name = "Kilieleshwa",
                    lastWeatherID = 800,
                    date = "Last Update 26 June, 2022"
                ),
            )
        }
    }
}
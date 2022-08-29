package com.granson.dvtweather.presentation.composables.screens.viewModels

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.granson.dvtweather.data.models.places.autocomplete.AutoComplete
import com.granson.dvtweather.data.repository.Resource
import com.granson.dvtweather.data.repository.repos.DataRepository
import com.granson.dvtweather.data.repository.repos.PlaceRepository
import com.granson.dvtweather.data.repository.repos.WeatherRepository
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.PlaceLocation
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.RequestState
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.SavedPlace
import com.granson.dvtweather.utils.Common.baseLogger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("DEPRECATION") // Will be omitted later
@HiltViewModel
class ScreensViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val placeRepository: PlaceRepository,
    private val dataRepository: DataRepository
): ViewModel() {

    private var _currentWeather = MutableSharedFlow<RequestState>()
    var  currentWeather= _currentWeather.asSharedFlow()

    val queryPlaces = mutableStateOf(AutoComplete())
    val listSavedPlaces = mutableStateOf(listOf<SavedPlace>())
    val selectedPlace = mutableStateOf(SavedPlace())

    val isPlaceSelected = mutableStateOf(false)

    val isRequesting =  mutableStateOf(false)
    val isLoading =  mutableStateOf(true)

    private val _placeDetails = MutableStateFlow(SavedPlace())
    val placeDetails = _placeDetails.asSharedFlow()

    val isAddedSuccess = mutableStateOf(false)
    val isUpdated = mutableStateOf(false)
    val isDeleted = mutableStateOf(false)
    val dbRequestError = mutableStateOf(false)
    val requestError = mutableStateOf(false)

    fun getInternetStatus(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }

    fun getWeatherInfo(
        lat: Float,
        lon: Float,
        key: String
    ){
        viewModelScope.launch {
            weatherRepository.getCurrentWeather(
                lat = lat,
                lon = lon,
                apiKey = key
            ).collect{
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
                        _currentWeather.emit(
                            RequestState(
                                isLoading = it.isLoading,
                                data = null,
                                error = null
                            )
                        )
                    }
                }
            }
        }
    }

    fun placeSearch(
        char: String,
        key: String
    ){
        viewModelScope.launch {
            println("Request got here --> Request made")
            placeRepository.placeSearch(
                name = char,
                apiKey = key
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
        key: String
    ){
        viewModelScope.launch {
            placeRepository.placeDetails(
                placeId = placeId,
                apiKey = key
            ).collect{
                when(it){
                    is Resource.Success -> {
                        val place = it.data
                        if(place != null){
                            _placeDetails.emit(
                                SavedPlace(
                                    location = PlaceLocation(place.result.geometry.location.lat, place.result.geometry.location.lng),
                                    name = place.result.name,
                                    placeId = placeId
                                )
                            )
                        }
                    }
                    is Resource.Error -> {
                        dbRequestError.value = true
                    }
                    is Resource.Loading -> {

                    }
                }
            }
        }
    }

    fun getSavedPlaces(){
        CoroutineScope(Dispatchers.IO).launch {
            dataRepository.getAllFavouritePlaces().collect{
                when (it) {
                    is Resource.Success -> {
                        // baseLogger("The Saved Places are", it.data)
                        val value = it.data
                        isLoading.value = false
                        if(value != null){
                            listSavedPlaces.value = value
                        }
                    }
                    else -> {
                        // baseLogger("The Saved Places error", it.message)
                    }
                }
            }
        }
    }

    fun savePlace(place: SavedPlace){
        CoroutineScope(Dispatchers.IO).launch {
            dataRepository.addFavouritePlace(place).collect{
                when (it) {
                    is Resource.Success -> {
                        //baseLogger("The Saved Places", it.data)
                        isAddedSuccess.value = true
                        getSavedPlaces()
                    }
                    else -> {
                        //baseLogger("The Saved Places error", it.message)
                        dbRequestError.value = true
                    }
                }
            }
        }
    }

    fun updatePlace(place: SavedPlace){
        CoroutineScope(Dispatchers.IO).launch {
            dataRepository.updateFavouritePlaces(place).collect{
                when (it) {
                    is Resource.Success -> {
                        getSavedPlaces()
                        isUpdated.value = true
                    }
                    else -> {
                        dbRequestError.value = true
                    }
                }
            }
        }
    }

    fun deletePlace(place: SavedPlace){
        CoroutineScope(Dispatchers.IO).launch {
            dataRepository.deleteFavouritePlaces(place).collect{
                when (it) {
                    is Resource.Success -> {

                        getSavedPlaces()
                        isDeleted.value = true
                    }
                    else -> {
                        dbRequestError.value = true
                    }
                }
            }
        }
    }
}
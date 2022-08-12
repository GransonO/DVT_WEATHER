package com.granson.dvtweather.data.repository.repos

import com.granson.dvtweather.data.repository.Resource
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.SavedPlace
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    suspend fun addFavouritePlace(place: SavedPlace): Flow<Resource<Long>>

    suspend fun getAllFavouritePlaces(): Flow<Resource<List<SavedPlace>>>

    suspend fun updateFavouritePlaces(place: SavedPlace): Flow<Resource<Int>>

    suspend fun deleteFavouritePlaces(place: SavedPlace): Flow<Resource<Int>>

}
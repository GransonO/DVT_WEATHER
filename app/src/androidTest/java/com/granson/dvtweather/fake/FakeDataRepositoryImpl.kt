package com.granson.dvtweather.fake

import com.granson.dvtweather.data.repository.Resource
import com.granson.dvtweather.data.repository.repos.DataRepository
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.SavedPlace
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeDataRepositoryImpl: DataRepository {

    private val places = mutableListOf<SavedPlace>()

    override suspend fun addFavouritePlace(place: SavedPlace): Flow<Resource<Long>> = flow {
        places.add(place)
        emit(Resource.Success(1L))
    }

    override suspend fun getAllFavouritePlaces(): Flow<Resource<List<SavedPlace>>> = flow {
        emit(Resource.Success(places))
    }

    override suspend fun updateFavouritePlaces(place: SavedPlace): Flow<Resource<Int>> = flow {
        val thePlaceList = places.filter { it.placeId == place.placeId }
        if(thePlaceList.isEmpty()){
            emit(Resource.Error("No place found"))
        }else{
            places.remove(thePlaceList[0])
            places.add(place)
            emit(Resource.Success(1))
        }
    }

    override suspend fun deleteFavouritePlaces(place: SavedPlace): Flow<Resource<Int>> = flow {
        val thePlaceList = places.filter { it.placeId == place.placeId }
        if(thePlaceList.isEmpty()){
            emit(Resource.Error("No place found"))
        }else{
            places.remove(thePlaceList[0])
            emit(Resource.Success(1))
        }
    }
}
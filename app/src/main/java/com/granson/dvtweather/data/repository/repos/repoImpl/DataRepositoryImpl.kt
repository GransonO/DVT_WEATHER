package com.granson.dvtweather.data.repository.repos.repoImpl

import com.granson.dvtweather.data.db.database.AppDatabase
import com.granson.dvtweather.data.repository.BaseRepository
import com.granson.dvtweather.data.repository.Resource
import com.granson.dvtweather.data.repository.repos.DataRepository
import com.granson.dvtweather.presentation.composables.screens.viewModels.models.SavedPlace
import com.granson.dvtweather.utils.Common.baseLogger
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataRepositoryImpl @Inject constructor(
    private val dao: AppDatabase
): DataRepository, BaseRepository() {

    override suspend fun addFavouritePlace(place: SavedPlace): Flow<Resource<Long>> {
        return databaseCall { dao.weatherDao.insertPlace(place) }
    }

    override suspend fun getAllFavouritePlaces(): Flow<Resource<List<SavedPlace>>>{
        return databaseCall { dao.weatherDao.getAllPlaces() }
    }

    override suspend fun updateFavouritePlaces(place: SavedPlace): Flow<Resource<Int>> {
        baseLogger("The updated place is", place)
        return databaseCall { dao.weatherDao.updatePlace(place) }
    }

    override suspend fun deleteFavouritePlaces(place: SavedPlace): Flow<Resource<Int>> {
        return databaseCall { dao.weatherDao.deletePlace(place) }
    }

}
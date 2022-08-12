package com.granson.dvtweather.data.repository.repos.repoImpl

import com.granson.dvtweather.data.api.PlaceService
import com.granson.dvtweather.data.models.places.autocomplete.AutoComplete
import com.granson.dvtweather.data.models.places.details.PlaceDetails
import com.granson.dvtweather.data.repository.BaseRepository
import com.granson.dvtweather.data.repository.repos.PlaceRepository
import com.granson.dvtweather.data.repository.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlaceRepositoryImpl @Inject constructor(
    private val placeService: PlaceService
): PlaceRepository, BaseRepository() {

    override suspend fun placeSearch(name: String, apiKey: String): Flow<Resource<AutoComplete>> = dvtAPICall {
        placeService.placeSearch(name, apiKey)
    }

    override suspend fun placeDetails(
        placeId: String,
        apiKey: String
    ): Flow<Resource<PlaceDetails>> = dvtAPICall{
        placeService.placeDetails(placeId, apiKey)
    }

}
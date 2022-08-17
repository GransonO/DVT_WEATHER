package com.granson.dvtweather.fake

import com.granson.dvtweather.data.models.places.autocomplete.AutoComplete
import com.granson.dvtweather.data.models.places.details.PlaceDetails
import com.granson.dvtweather.data.repository.Resource
import com.granson.dvtweather.data.repository.repos.PlaceRepository
import kotlinx.coroutines.flow.Flow

class PlaceRepositoryImpl: PlaceRepository {

    override suspend fun placeSearch(name: String, apiKey: String): Flow<Resource<AutoComplete>> {
        TODO("Not yet implemented")
    }

    override suspend fun placeDetails(
        placeId: String,
        apiKey: String
    ): Flow<Resource<PlaceDetails>> {
        TODO("Not yet implemented")
    }
}
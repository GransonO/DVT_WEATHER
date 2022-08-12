package com.granson.dvtweather.data.repository.repos

import com.granson.dvtweather.data.models.places.autocomplete.AutoComplete
import com.granson.dvtweather.data.models.places.details.PlaceDetails
import com.granson.dvtweather.data.repository.Resource
import kotlinx.coroutines.flow.Flow

interface PlaceRepository {

    suspend fun placeSearch(
        name: String,
        apiKey: String,
    ): Flow<Resource<AutoComplete>>

    suspend fun placeDetails(
        placeId: String,
        apiKey: String,
    ): Flow<Resource<PlaceDetails>>
}
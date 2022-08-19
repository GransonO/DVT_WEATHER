package com.granson.dvtweather.fake

import com.granson.dvtweather.data.models.places.autocomplete.AutoComplete
import com.granson.dvtweather.data.models.places.autocomplete.MatchedSubstring
import com.granson.dvtweather.data.models.places.autocomplete.Prediction
import com.granson.dvtweather.data.models.places.autocomplete.StructuredFormatting
import com.granson.dvtweather.data.models.places.details.Geometry
import com.granson.dvtweather.data.models.places.details.Location
import com.granson.dvtweather.data.models.places.details.PlaceDetails
import com.granson.dvtweather.data.models.places.details.Result
import com.granson.dvtweather.data.repository.Resource
import com.granson.dvtweather.data.repository.repos.PlaceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakePlaceRepositoryImpl: PlaceRepository {

    var placeRequest = AutoComplete(
            predictions = listOf(
                Prediction(
                    description = "Test Place 1",
                    matchedSubstrings = listOf(
                        MatchedSubstring(
                            5,2
                        ),
                        MatchedSubstring(
                            5,3
                        ),
                        MatchedSubstring(
                            5,4
                        ),
                    ),
                    placeId = "testPlaceId1",
                    reference = "1234",
                    structuredFormatting = StructuredFormatting(),
                ),
                Prediction(
                    description = "Test Place 2",
                    matchedSubstrings = listOf(
                        MatchedSubstring(
                            5,2
                        ),
                        MatchedSubstring(
                            5,3
                        ),
                        MatchedSubstring(
                            5,4
                        ),
                    ),
                    placeId = "testPlaceId2",
                    reference = "1234",
                    structuredFormatting = StructuredFormatting(),
                ),
                Prediction(
                    description = "Test Place 3",
                    matchedSubstrings = listOf(
                        MatchedSubstring(
                            5,2
                        ),
                        MatchedSubstring(
                            5,3
                        ),
                        MatchedSubstring(
                            5,4
                        ),
                    ),
                    placeId = "testPlaceId3",
                    reference = "1234",
                    structuredFormatting = StructuredFormatting(),
                ),
            )
        )

    override suspend fun placeSearch(name: String, apiKey: String): Flow<Resource<AutoComplete>> = flow {
        val placeFilter = placeRequest.predictions.filter { it.description.contains(name, ignoreCase = true) }
        println("Request got here --> $placeFilter")
        emit(Resource.Success(
            AutoComplete(
                predictions = placeFilter
            )
        ))
    }

    override suspend fun placeDetails(
        placeId: String,
        apiKey: String
    ): Flow<Resource<PlaceDetails>> = flow {

        try {
            val placeItem = placeRequest.predictions.filter { it.placeId == placeId }[0]
            emit(Resource.Success(
                PlaceDetails(
                    result = Result(
                        placeId = placeId,
                        adrAddress = placeItem.description,
                        geometry = Geometry(
                            location = Location(0.0, 0.0)
                        ),
                        formattedAddress = placeItem.description,
                        name = placeItem.description
                    )
                )
            ))
        }catch (e: Exception){
            emit(Resource.Error("No place"))
        }

    }
}
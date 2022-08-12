package com.granson.dvtweather.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

abstract class BaseRepository {

    suspend fun <T> dvtAPICall(apiCall: suspend () -> T ): Flow<Resource<T>> {
        return flow {
            try {
                emit(Resource.Loading(true))
                val resp = apiCall()
                emit(Resource.Success(resp))
                emit(Resource.Loading(false))
            } catch (e: Exception) {
                e.printStackTrace()

                emit(Resource.Loading(true))
                when (e) {
                    is IOException -> emit(
                        Resource.Error(
                            message = "Unable to connect, check your connection"
                        )
                    )

                    is HttpException -> {
                        emit(
                            Resource.Error(
                                message =  "Error occurred while processing the request"
                            )
                        )
                    }
                    is UnknownHostException -> emit(
                        Resource.Error(
                            message = "Unable to connect, check your connection"
                        )
                    )
                    is SocketTimeoutException -> emit(
                        Resource.Error(
                            message = "Network error, please try again later"
                        )
                    )
                    else ->  emit(
                        Resource.Error(
                            message = "Unable to connect, check your connection"
                        )
                    )
                }
            }
        }
    }
}
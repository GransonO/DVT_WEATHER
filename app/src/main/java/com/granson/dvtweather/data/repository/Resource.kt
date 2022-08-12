package com.granson.dvtweather.data.repository

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null,
    val error: T? = null,
    val isLoading: Boolean = false
){
    class Success<T>(data: T?): Resource<T>(data)
    class Error<T>(message: String?): Resource<T>(data = null, message = message)
    class Loading<T>(isLoading: Boolean = false): Resource<T>(data = null, isLoading = isLoading)
}

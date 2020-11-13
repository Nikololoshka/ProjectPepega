package com.vereshchagin.nikolay.stankinschedule.utils

sealed class State<T> {
    class Loading<T> : State<T>()
    data class Success<T>(val data: T) : State<T>()
    data class Failed<T>(val error: Exception) : State<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success(data)
        fun <T> failed(error: Exception) = Failed<T>(error)
    }
}

package com.ruliam.organizedfw.core.data.util

sealed class UiStateFlow<T>(val data: T? = null) {
    class Success<T>(data: T): UiStateFlow<T>(data)
    class Error<T>(val message: String) : UiStateFlow<T>()
    class Loading<T> : UiStateFlow<T>()
    class Empty<T> : UiStateFlow<T>()
}
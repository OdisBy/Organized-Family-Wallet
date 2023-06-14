package com.ruliam.organizedfw.core.data.util

sealed class ResourceFirebase<out T> {
    data class Success<out T>(val data: T) : ResourceFirebase<T>()
    data class Error(val message: String) : ResourceFirebase<Nothing>()
}
package com.odisby.organizedfw.core.data.util

sealed class StorageResult {
    object Success: StorageResult()
    data class Error(val message: String) : StorageResult()
    object Loading : StorageResult()
    object Empty : StorageResult()
}
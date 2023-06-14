package com.ruliam.organizedfw.core.data.util

sealed class AuthResult {
    object Success: AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
    object Empty : AuthResult()
}
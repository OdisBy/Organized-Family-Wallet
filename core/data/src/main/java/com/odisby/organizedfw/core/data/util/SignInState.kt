package com.odisby.organizedfw.core.data.util

data class SignInState (
    var isLoading: Boolean = false,
    var isSuccess: Boolean = false,
    var isError: String? = ""
)
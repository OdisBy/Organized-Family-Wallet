package com.odisby.organizedfw.core.data.util

sealed class SignUpResult {
    object Success: SignUpResult()
    data class Error(val message: String): SignUpResult()
    object AlreadyHasAccount: SignUpResult() {
        val message: String = "Esse email já esta vinculado a uma conta."

    }
    object Empty: SignUpResult()
    object Loading: SignUpResult()



}
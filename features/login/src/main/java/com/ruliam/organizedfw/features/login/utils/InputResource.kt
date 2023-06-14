package com.ruliam.organizedfw.features.login.utils

sealed class InputResource {
    object Success: InputResource()
    data class Error(val message: String): InputResource() {
        companion object {
            val IsEmpty = Error("O campo não pode ser vazio")
            val IsInvalid = Error("Campo inválido")
            fun lessCharacters(value: Int) = Error("O campo deve ter pelo menos $value caracteres")
            fun customError(message: String) = Error(message)
        }
    }
}
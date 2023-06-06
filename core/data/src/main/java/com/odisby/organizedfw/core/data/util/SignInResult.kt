package com.odisby.organizedfw.core.data.util

sealed class SignInResult {
    data class Success(val userId: String): SignInResult()
    object AccountNotCompleted : SignInResult() {
        val message: String = "A conta não está completa.\nPor favor, preencha todas as informações."
    }
    data class Error(val message: String): SignInResult() {
        companion object {
            fun customError(message: String) = Error(message)
        }
    }

    data class EmailError(val message: String): SignInResult() {
        companion object {
            val NotFoundAccount = EmailError("Conta não encontrada")
        }
    }

    data class PasswordError(val message: String): SignInResult() {
        companion object {
            val WrongPassword = PasswordError("Senha incorreta")
        }
    }

    object Loading: SignInResult()
    object Empty: SignInResult()
}
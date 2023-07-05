package com.ruliam.organizedfw.core.data.util

sealed class RequestPendingResult {
    object Success: RequestPendingResult()
    object alreadyPendingUserInThisGroup: RequestPendingResult()
    object alreadyRequestForAnotherGroup: RequestPendingResult()

    data class Error(val message: String): RequestPendingResult() {
        companion object {
            fun customError(message: String) = Error(message)
        }
    }

    object userAlreadyInThisGroup: RequestPendingResult()
    object groupDoesNotExist: RequestPendingResult()
    object Empty: RequestPendingResult()
}
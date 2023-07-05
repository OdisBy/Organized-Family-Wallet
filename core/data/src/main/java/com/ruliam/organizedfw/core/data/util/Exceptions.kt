package com.ruliam.organizedfw.core.data.util

class FinanceRepositoryException(message: String?, cause: Throwable?) : Exception(message, cause)

object DoesNotExist: Exception("Does not exist")

class CanNotAddFirebase(message: String?) : Exception(message)
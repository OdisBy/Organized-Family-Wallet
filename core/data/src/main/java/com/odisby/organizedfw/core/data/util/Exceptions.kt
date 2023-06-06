package com.odisby.organizedfw.core.data.util

class FinanceRepositoryException(message: String?, cause: Throwable?) : Exception(message, cause)

class DoesNotExist(message: String?): Exception(message)

class CanNotAddFirebase(message: String?) : Exception(message)
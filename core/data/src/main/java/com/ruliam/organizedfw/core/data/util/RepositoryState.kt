package com.ruliam.organizedfw.core.data.util

data class RepositoryState (
    var isLoading: Boolean = false,
    var isSuccess: Boolean = false,
    var isError: String? = ""
)
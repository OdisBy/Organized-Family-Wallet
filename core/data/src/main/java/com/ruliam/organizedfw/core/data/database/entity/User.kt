package com.ruliam.organizedfw.core.data.database.entity

internal data class User(
    val uuid: String,
    val username: String,
    val balance: Double,
    val isMainUser: Boolean
)
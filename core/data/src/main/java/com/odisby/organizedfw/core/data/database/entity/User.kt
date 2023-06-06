package com.odisby.organizedfw.core.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

internal data class User(
    val uuid: String,
    val username: String,
    val balance: Double,
    val isMainUser: Boolean
)
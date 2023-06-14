package com.ruliam.organizedfw.core.data.database.entity

import java.time.LocalDate

internal data class Finance(
    val uuid: String,
    val userId: String,
    val title: String,
    val date: LocalDate,
    val description: String,
    val amount: Double,
    val recurrent: Boolean
)

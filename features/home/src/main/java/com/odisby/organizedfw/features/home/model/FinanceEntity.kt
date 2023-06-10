package com.odisby.organizedfw.features.home.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date

data class FinanceEntity(
    val id: String? = "",
    val userId: String? = "",
    val name: String? = "",
    val username: String? = "",
    val date: Date? = null,
    val description: String? = "",
    val amount: Double = 0.0,
    val recurrent: Boolean = false
)
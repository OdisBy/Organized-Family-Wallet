package com.odisby.organizedfw.features.home.model

import java.time.LocalDate

data class FinanceEntity(
    val id: String? = "",
    val userId: String? = "",
    val name: String? = "",
    val username: String? = "",
    val date: LocalDate? = LocalDate.now(),
    val description: String? = "",
    val amount: Double = 0.0,
    val recurrent: Boolean = false
)
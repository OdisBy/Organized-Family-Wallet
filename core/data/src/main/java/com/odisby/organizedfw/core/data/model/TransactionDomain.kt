package com.odisby.organizedfw.core.data.model

import java.util.Date


data class TransactionDomain(
    val id: String = "",
    val userId: String = "",
    var userName: String = "",
    val title: String = "",
    val date: Date = Date(),
    val recurrent: Boolean = false,
//    val isCoupleFinance: Boolean,
    val description: String = "",
    val amount: Double = 0.0
)
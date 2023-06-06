package com.odisby.organizedfw.core.data.model


data class TransactionDomain(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val date: Long = 0,
    val recurrent: Boolean = false,
//    val isCoupleFinance: Boolean,
    val description: String = "",
    val amount: Double = 0.0
)
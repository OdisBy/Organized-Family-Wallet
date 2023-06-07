package com.odisby.organizedfw.core.data.repository

import com.odisby.organizedfw.core.data.model.TransactionDomain
import java.time.LocalDate
import java.util.UUID

interface FinanceRepository {

    /**
     * Get all finances of user
     */
    suspend fun fetchAllByUser(userId: UUID): List<TransactionDomain>

    /**
     * Get all finances of month by
     * @param userId
     */
    suspend fun fetchMonthByUser(month: Long, userId: String): List<TransactionDomain>

    suspend fun getLastByUser(userId: String): TransactionDomain?

    /**
     * Add a new finance
     */
    suspend fun add(name: String,
                    date: Long,
                    description: String,
                    amount: Double,
                    recurrent: Boolean)

}
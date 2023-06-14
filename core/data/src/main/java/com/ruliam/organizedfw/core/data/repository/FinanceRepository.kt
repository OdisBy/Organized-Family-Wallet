package com.ruliam.organizedfw.core.data.repository

import com.ruliam.organizedfw.core.data.model.TransactionDomain
import java.util.Date
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

    suspend fun fetchGroupFinancesByMonth(month: Int): List<TransactionDomain>


    suspend fun getLastByUser(userId: String): TransactionDomain?

    /**
     * Add a new finance
     */
    suspend fun add(name: String,
                    date: Date,
                    description: String,
                    amount: Double,
                    recurrent: Boolean)

}
package com.ruliam.organizedfw.core.data.repository

import com.ruliam.organizedfw.core.data.model.TransactionDomain
import java.util.Date
import java.util.UUID

interface FinanceRepository {
    /**
     * Get all finances of user using it userID
     */
    suspend fun fetchAllByUser(userId: UUID): List<TransactionDomain>
    /**
     * Get all finances of month of user with userId
     */
    suspend fun fetchMonthByUser(month: Long, userId: String): List<TransactionDomain>
    /**
     * Get all finances of month of the group
     */
    suspend fun fetchGroupFinancesByMonth(month: Int): List<TransactionDomain>
    /**
     * Get the last finance of the user
     */
    suspend fun getLastByUser(userId: String): TransactionDomain?
    /**
     * Add a new finance
     * @param name the name of the finance
     * @param date the current LocalDateTime
     * @param description the type of the finance
     * @param amount the amount, can be negative and positive
     * @param recurrent is this finance recurrent
     */
    suspend fun add(name: String,
                    date: Date,
                    description: String,
                    amount: Double,
                    recurrent: Boolean)

}
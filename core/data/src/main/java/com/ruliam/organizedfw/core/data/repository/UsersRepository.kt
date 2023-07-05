package com.ruliam.organizedfw.core.data.repository

import com.ruliam.organizedfw.core.data.model.UserDomain

interface UsersRepository {
    suspend fun getMainUser(): UserDomain?
    suspend fun getUserBalance(userId: String): Double
    suspend fun getUserById(userId: String): UserDomain?
    suspend fun getUserId(): String
    suspend fun getGroupId(): String
    suspend fun updateUsername(name: String)
    suspend fun updateBalance(userId: String, amount: Double)
}
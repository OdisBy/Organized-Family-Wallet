package com.odisby.organizedfw.core.data.repository

import com.odisby.organizedfw.core.data.model.GroupUsersDomain
import com.odisby.organizedfw.core.data.model.UserDomain
import com.odisby.organizedfw.core.data.util.ResourceFirebase
import kotlinx.coroutines.flow.Flow

interface UsersRepository {

//    suspend fun checkIfUserExists()

    suspend fun getMainUser(): UserDomain?

    suspend fun getUserBalance(userId: String): Double

//    suspend fun setAvatar()
    suspend fun getUserById(userId: String): UserDomain?

    suspend fun getUserId(): String

    suspend fun updateNameUser(name: String)

    suspend fun updateBalance(userId: String, amount: Double)
}
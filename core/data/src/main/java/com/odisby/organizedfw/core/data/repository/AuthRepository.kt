package com.odisby.organizedfw.core.data.repository

import android.graphics.Bitmap
import com.odisby.organizedfw.core.data.model.UserDomain
import com.odisby.organizedfw.core.data.util.SignInResult
import com.odisby.organizedfw.core.data.util.SignUpResult

interface AuthRepository {
    suspend fun loginUserWithEmailAndPassword(email: String, password: String): SignInResult
    suspend fun registerUserWithEmailAndPassword(email: String, password: String): SignUpResult
    suspend fun checkLogin(): Boolean
    suspend fun createUserAndAddToGroup(
        username: String,
        groupInvite: String?,
        profileAvatar: Bitmap
    ): UserDomain

    fun createLoginSession(userId: String, groupId: String)
}
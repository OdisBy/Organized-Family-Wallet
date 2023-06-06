package com.odisby.organizedfw.features.login.repositories

import android.graphics.Bitmap
import com.odisby.organizedfw.core.data.model.UserDomain
import com.odisby.organizedfw.core.data.repository.AuthRepository
import com.odisby.organizedfw.core.data.util.SignInResult
import com.odisby.organizedfw.core.data.util.SignUpResult
import java.util.UUID

class FakeAuthRepository: AuthRepository {

    private var shouldReturnNetworkError = false
    private var shouldReturnNotCompletedAccount = false

    fun shouldReturnNotCompletedAccount(value: Boolean){
        shouldReturnNotCompletedAccount = value
    }

    fun shouldReturnNetworkError(value: Boolean){
        shouldReturnNetworkError = value
    }

    override suspend fun loginUserWithEmailAndPassword(
        email: String,
        password: String
    ): SignInResult {
        TODO("Not yet implemented")
    }

    override suspend fun registerUserWithEmailAndPassword(
        email: String,
        password: String
    ): SignUpResult {
        TODO("Not yet implemented")
    }

    override suspend fun checkLogin(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun createUserAndAddToGroup(
        username: String,
        groupInvite: String?,
        profileAvatar: Bitmap
    ): UserDomain {
        TODO("Not yet implemented")
    }

    override fun createLoginSession(userId: String) {
        TODO("Not yet implemented")
    }

}
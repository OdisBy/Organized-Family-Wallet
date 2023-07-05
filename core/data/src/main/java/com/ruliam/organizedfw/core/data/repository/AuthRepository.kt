package com.ruliam.organizedfw.core.data.repository

import android.graphics.Bitmap
import com.ruliam.organizedfw.core.data.model.UserDomain
import com.ruliam.organizedfw.core.data.util.SignInResult
import com.ruliam.organizedfw.core.data.util.SignUpResult

interface AuthRepository {
    /**
     * Login the user with email and password
     */
    suspend fun loginUserWithEmailAndPassword(email: String, password: String): SignInResult
    /**
     * Register the user with email and password
     */
    suspend fun registerUserWithEmailAndPassword(email: String, password: String): SignUpResult

    /**
     * Check if the user is logged using the device data
     */
    suspend fun checkLogin(): Boolean

    /**
     * Create an user and add it automatically in a group
     * @param groupInvite if has groupInvite the user will be marked as pendent in the group
     */
    suspend fun createUserAndAddToGroup(
        username: String,
        groupInvite: String?,
        profileAvatar: Bitmap
    ): UserDomain
    /**
     * Create a login session in the device data
     */
    fun createLoginSession(userId: String, groupId: String)
}
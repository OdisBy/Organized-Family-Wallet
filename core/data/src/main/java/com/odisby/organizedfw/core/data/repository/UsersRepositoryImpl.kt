package com.odisby.organizedfw.core.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.odisby.organizedfw.core.data.model.UserDomain
import com.odisby.organizedfw.core.data.session.SessionManager
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

internal class UsersRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
    private val sessionManager: SessionManager
    ) : UsersRepository {

    private suspend fun getFirebaseUser(userId: String) : DocumentSnapshot?{
        return try {
            firebaseFirestore.collection("users")
                .document(userId)
                .get()
                .await()
        } catch (e: Exception) {
            Log.w(TAG, "Error on try to get user with id $userId, error: ${e.message}")
            return null
        }
    }
    private suspend fun firebaseToUserDomain(userId: String) : UserDomain? {
        return getFirebaseUser(userId)!!.toObject(UserDomain::class.java)
    }

    override suspend fun getMainUser(): UserDomain? {
        val userId = sessionManager.getUserId()
        return firebaseToUserDomain(userId)
    }

    override suspend fun getUserById(userId: String): UserDomain? {
        return firebaseToUserDomain(userId)
    }

    override suspend fun getUserBalance(userId: String): Double {
        return try {
            val snapshot = firebaseToUserDomain(userId)
            snapshot!!.balance
        } catch (e: Exception) {
            Log.w(TAG, "Error on try to get user with id $userId, error: ${e.message}")
            0.0
        }
    }

    override suspend fun getUserId(): String {
        return sessionManager.getUserId()
    }

    override suspend fun getGroupId(): String {
        return sessionManager.getGroupId() ?: "Unknown error"
    }

    override suspend fun updateNameUser(name: String) {
        val userId = sessionManager.getUserId()
        val snapshot = firebaseFirestore.collection("users")
            .document(userId)
        snapshot
            .update("username", name)
            .await()
    }
    override suspend fun updateBalance(userId: String, amount: Double) {
        try {
            val userBalance = firebaseToUserDomain(userId)!!.balance
            val newBalance = userBalance.plus(amount)
            firebaseFirestore.collection("users")
                .document(userId)
                .update("balance", newBalance)
                .await()

        } catch (e: Exception){
            Log.w(TAG, "Error on try to update user balance with id $userId, error: ${e.message}")
        }
    }

companion object {
    const val TAG = "UsersRepository"
}
}
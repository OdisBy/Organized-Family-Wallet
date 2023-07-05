package com.ruliam.organizedfw.core.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ruliam.organizedfw.core.data.model.GroupDomain
import com.ruliam.organizedfw.core.data.model.GroupUserDomain
import com.ruliam.organizedfw.core.data.model.UserDomain
import com.ruliam.organizedfw.core.data.session.SessionManager
import com.ruliam.organizedfw.core.data.util.SignInResult
import com.ruliam.organizedfw.core.data.util.SignUpResult
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.coroutines.resume

internal class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val sessionManager: SessionManager,
    private val groupRepository: GroupRepository,
    private val avatarRepository: AvatarRepository
) : AuthRepository {
    override suspend fun loginUserWithEmailAndPassword(email: String, password: String): SignInResult = suspendCancellableCoroutine { continuation ->
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid
                    if (userId != null) {
                        handleUserExists(userId, continuation)
                    } else {
                        continuation.resume(SignInResult.Error("Unknown user ID"))
                    }
                } else {
                    val error = task.exception
                    handleLoginError(error, continuation)
                }
            }
            .addOnCanceledListener {
                continuation.cancel()
            }
    }

    override suspend fun registerUserWithEmailAndPassword(email: String, password: String): SignUpResult = suspendCancellableCoroutine { continuation ->
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    continuation.resume(SignUpResult.Success)
                } else {
                    val error = task.exception
                    handleRegisterError(error, continuation)
                }
            }
            .addOnCanceledListener {
                continuation.cancel()
            }
    }

    override suspend fun checkLogin(): Boolean {
        return sessionManager.checkLogin()
    }

    override suspend fun createUserAndAddToGroup(
        username: String,
        groupInvite: String?,
        profileAvatar: Bitmap
    ): UserDomain {
        try{
            val groupDomain: GroupDomain
            val userId = firebaseAuth.currentUser!!.uid
            val userRef = firebaseFirestore.collection("users")
                .document(userId)

            val avatarUri = addAvatar(userRef.id, profileAvatar).toString()

            val userDomain = UserDomain(
                id = userId,
                username = username,
                balance = 0.0,
                email = null,
                phoneNumber = null,
                groupId = "",
                profileUri = avatarUri,
                pendingGroupId = null
            )

            val groupUserDomain = GroupUserDomain(
                id = userId,
                username = userDomain.username!!,
                balance = 0.0,
                profilePhoto = avatarUri,
                lastTransaction = null
            )
            groupDomain = groupRepository.createGroupDomain(groupUserDomain)

            val groupRef: DocumentReference = firebaseFirestore.collection("groups")
                .document()

            groupDomain.id = groupRef.id
            userDomain.groupId = groupRef.id
            createLoginSession(userDomain.id, groupRef.id)

            firebaseFirestore.runBatch { batch ->
                groupRef.set(groupDomain)
                userRef.set(userDomain)
            }

            // After create user ask to enter in the group
            if(!groupInvite.isNullOrEmpty()){
                groupRepository.askEnterGroup(groupInvite)
            }
            return userDomain
        } catch (e: Exception){
            Log.w(TAG, "Error on create user and group: ${e.message.toString()}")
            throw e
        }
    }

    override fun createLoginSession(userId: String, groupId: String) {
        sessionManager.createLoginSession(userId, groupId)
    }

    private suspend fun addAvatar(userId: String, avatar: Bitmap): Uri {
        try {
            var quality = 30
            val outputStream = ByteArrayOutputStream()
            avatar.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            var data = outputStream.toByteArray()
            while (data.size > 15000) {
                quality -= 5
                outputStream.reset()
                avatar.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                data = outputStream.toByteArray()
            }
            return avatarRepository.addAvatarToFirestore(userId, data)
        } catch (e: Exception) {
            Log.w(TAG, "Error on update avatar ${e.message.toString()}")
            throw e
        }
    }
    private fun handleUserExists(userId: String, continuation: CancellableContinuation<SignInResult>) {
        firebaseFirestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val exists = documentSnapshot.exists()
                val result = if (exists) {
                    val user = sessionManager.documentToUserDomain(documentSnapshot)
                    SignInResult.Success(user.id, user.groupId)
                } else {
                    SignInResult.AccountNotCompleted
                }
                continuation.resume(result)
            }
            .addOnFailureListener { exception ->
                val exceptionMessage = SignInResult.Error(exception.localizedMessage ?: "Unknown error")
                continuation.resume(exceptionMessage)
            }
    }

    private fun handleLoginError(error: Exception?, continuation: CancellableContinuation<SignInResult>) {
        Log.w(TAG, error.toString())
        val result = when (error) {
            is FirebaseAuthInvalidUserException -> SignInResult.EmailError.NotFoundAccount
            is FirebaseAuthInvalidCredentialsException -> SignInResult.PasswordError.WrongPassword
            else -> SignInResult.Error(error.toString())
        }
        continuation.resume(result)
    }

    private fun handleRegisterError(error: Exception?, continuation: CancellableContinuation<SignUpResult>) {
        Log.w(TAG, error.toString())
        val result = when (error) {
            is FirebaseAuthUserCollisionException -> SignUpResult.AlreadyHasAccount
            else -> SignUpResult.Error(error.toString())
        }
        continuation.resume(result)
    }

    companion object{
        const val TAG = "AuthRepository"
    }
}
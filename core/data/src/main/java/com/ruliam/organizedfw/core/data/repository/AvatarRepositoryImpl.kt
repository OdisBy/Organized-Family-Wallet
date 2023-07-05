package com.ruliam.organizedfw.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.ruliam.organizedfw.core.data.avatar.UserAvatar
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


internal class AvatarRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val userAvatar: UserAvatar,
    @ApplicationContext private val context: Context
) : AvatarRepository {
    private val storage = firebaseStorage.reference
    private val ONE_MEGABYTE: Long = 1024 * 1024
    private val avatarCache: MutableMap<String, Bitmap> = HashMap()

    override suspend fun addAvatarToFirestore(userId: String, bitmap: ByteArray): Uri {
        val avatarRef = storage.child("avatars/$userId")
        try{
            avatarRef.putBytes(bitmap).await()
            return avatarRef.downloadUrl.await()
        } catch (e: Exception){
            Log.w(TAG, "Error on try to update avatar ${e.message.toString()}")
            throw Exception(e)
        }
    }
    override suspend fun getAvatarById(userId: String): Bitmap? {
        val cachedAvatar = avatarCache[userId]
        if (cachedAvatar != null) {
            Log.d(TAG, "Getting avatar from cache")
            return cachedAvatar
        }

        var attemptCount = 0
        val maxAttempts = 5
        val retryDelayMillis = 1000

        while (attemptCount < maxAttempts) {
            try {
                val avatarRef = storage.child("avatars/$userId")
                val data = avatarRef.getBytes(ONE_MEGABYTE).await()

                val avatarAsBitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
                avatarCache[userId] = avatarAsBitmap
                return avatarAsBitmap
            } catch (e: Exception) {
                if ((e is StorageException) && (e.errorCode == -13010)) {
                    Log.w(TAG, "Avatar not found. Retrying...")
                    attemptCount++
                    delay(retryDelayMillis.toLong())
                } else {
                    Log.w(TAG, "Failed to get avatar: ${e.message.toString()}")
                    break
                }
            }
        }
        return null
    }
    override suspend fun generateAvatar(username: String): Bitmap {
        return userAvatar.generateUserAvatar(username)
    }
    override suspend fun getAvatarByUrl(userId: String, url: String): Bitmap? {
        val cachedAvatar = avatarCache[userId]
        if (cachedAvatar != null) {
            return cachedAvatar
        }
        try {
            val avatarBitmap = Glide.with(context)
                .asBitmap()
                .load(url)
                .submit()
                .get()

            avatarCache[userId] = avatarBitmap
            return avatarBitmap
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Can't get avatar image")
        }
    }
    companion object{
        private const val TAG = "AvatarRepository"
    }
}
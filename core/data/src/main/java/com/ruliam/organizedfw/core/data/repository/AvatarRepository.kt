package com.ruliam.organizedfw.core.data.repository

import android.graphics.Bitmap
import android.net.Uri

interface AvatarRepository {
    /**
     * Add the image passed as bitmap in firestore storage using userId as document reference
     */
    suspend fun addAvatarToFirestore(userId: String, bitmap: ByteArray): Uri
    /**
     * Get the avatar from firestore storage using userId as reference
     */
    suspend fun getAvatarById(userId: String): Bitmap?
    /**
     * Generate a preview of avatar with the initials of username
     */
    suspend fun generateAvatar(username: String): Bitmap
    /**
     * Get the user avatar using it url of firestore storage or cache if its already requested
     */
    suspend fun getAvatarByUrl(userId: String, url: String): Bitmap?
}
package com.ruliam.organizedfw.core.data.repository

import android.graphics.Bitmap
import android.net.Uri

interface AvatarRepository {
    suspend fun addAvatarToFirestore(userId: String, bitmap: ByteArray): Uri

    suspend fun getAvatarById(userId: String): Bitmap?

    suspend fun generateAvatar(username: String): Bitmap

    suspend fun getAvatarByUrl(url: String): Bitmap?
}
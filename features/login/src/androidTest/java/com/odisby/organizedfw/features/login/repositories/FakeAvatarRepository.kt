package com.odisby.organizedfw.features.login.repositories

import android.graphics.Bitmap
import android.net.Uri
import com.odisby.organizedfw.core.data.repository.AvatarRepository

class FakeAvatarRepository: AvatarRepository {
    override suspend fun addAvatarToFirestore(userId: String, bitmap: ByteArray): Uri {
        TODO("Not yet implemented")
    }

    override suspend fun getAvatarById(userId: String): Bitmap? {
        TODO("Not yet implemented")
    }

    override suspend fun generateAvatar(username: String): Bitmap {
        TODO("Not yet implemented")
    }
}
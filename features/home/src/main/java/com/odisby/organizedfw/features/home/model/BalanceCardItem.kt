package com.odisby.organizedfw.features.home.model

import android.graphics.Bitmap
import android.net.Uri

data class BalanceCardItem(
    val userId: String,
    val balance: String,
    val lastTransaction: String,
    val avatar: Bitmap
)
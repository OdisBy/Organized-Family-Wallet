package com.ruliam.organizedfw.features.home.model

import android.graphics.Bitmap

data class BalanceCardItem(
    val userId: String,
    val balance: String,
    val lastTransaction: String,
    val avatar: Bitmap
)
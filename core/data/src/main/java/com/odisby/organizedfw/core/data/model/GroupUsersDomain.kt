package com.odisby.organizedfw.core.data.model

import android.net.Uri
import androidx.core.net.toUri

data class GroupUsersDomain (
    val id: String,
    val username: String,
    var balance: Double,
    val profilePhoto: String? = null,
    var lastTransaction: TransactionDomain?
) {
    constructor(): this("", "", 0.0, null, null)
}
package com.ruliam.organizedfw.core.data.model

data class GroupUserDomain (
    val id: String,
    val username: String,
    var balance: Double,
    val profilePhoto: String? = null,
    var lastTransaction: TransactionDomain?
) {
    constructor(): this("", "", 0.0, null, null)
}
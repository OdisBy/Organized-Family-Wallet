package com.ruliam.organizedfw.core.data.model

data class UserDomain(
    var id: String = "",
    var username: String? = "",
    var balance: Double = 0.0,
    var email: String? = "",
    var phoneNumber: String? = "",
    var groupId: String = "",
    var profileUri: String? = null,
    var pendingGroupId: String? = null
) {
    constructor() : this("", "", 0.0, "", "", "", null, null)
}
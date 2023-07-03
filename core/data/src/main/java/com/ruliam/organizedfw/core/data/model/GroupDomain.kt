package com.ruliam.organizedfw.core.data.model

data class GroupDomain(
    var groupInvite: String?,
    var id: String?,
    var pendingUsers: List<GroupUserDomain?>,
    var users: List<GroupUserDomain>,
) {
    constructor(): this(null, null, listOf(), listOf())
}

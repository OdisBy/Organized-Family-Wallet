package com.ruliam.organizedfw.core.data.model

data class GroupDomain(
    var groupInvite: String?,
    var id: String?,
    var users: List<GroupUserDomain>,
    var pendingUsers: List<GroupUserDomain?>
) {
    constructor(): this(null, null, listOf(), listOf())
}

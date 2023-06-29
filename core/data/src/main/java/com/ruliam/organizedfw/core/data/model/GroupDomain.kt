package com.ruliam.organizedfw.core.data.model

data class GroupDomain(
    var groupInvite: String?,
    var id: String?,
    var users: List<GroupUsersDomain>,
    var pendingUsers: List<GroupUsersDomain?>
) {
    constructor(): this(null, null, listOf(), listOf())
}

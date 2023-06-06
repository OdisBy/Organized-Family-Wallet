package com.odisby.organizedfw.core.data.model

data class GroupDomain(
    var id: String?,
    val groupInvite: String,
    var users: List<GroupUsersDomain>
) {
    constructor(): this(null, "", listOf())
}

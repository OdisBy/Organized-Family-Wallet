package com.ruliam.organizedfw.core.data.repository

import com.ruliam.organizedfw.core.data.model.GroupDomain
import com.ruliam.organizedfw.core.data.model.GroupUsersDomain

interface GroupRepository {

    /**
     * Return the users of the group that is logged...
     * The index 0 will be the user logged
     */
    suspend fun getUsers(): List<GroupUsersDomain?>

    suspend fun getUserGroupId(): String
    suspend fun getGroupInviteCode(): String
    suspend fun createGroupDomain(groupUsersDomain: GroupUsersDomain): GroupDomain

    suspend fun getGroupBalance(): Double

    suspend fun getGroupDomain(groupInvite: String): GroupDomain
    suspend fun getGroupDomainById(groupId: String): GroupDomain

    suspend fun removeUserOfGroup(userId: String)

    suspend fun hasGroup(): Boolean
    suspend fun askEnterGroup(inviteCode: String)
}
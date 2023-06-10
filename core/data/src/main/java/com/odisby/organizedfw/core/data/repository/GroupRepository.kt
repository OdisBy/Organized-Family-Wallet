package com.odisby.organizedfw.core.data.repository

import com.odisby.organizedfw.core.data.model.GroupDomain
import com.odisby.organizedfw.core.data.model.GroupUsersDomain

interface GroupRepository {

    /**
     * Return the users of the group that is logged...
     * The index 0 will be the user logged
     */
    suspend fun getUsers(): List<GroupUsersDomain?>

    suspend fun getUserGroupId(): String

    suspend fun createGroupDomain(groupUsersDomain: GroupUsersDomain): GroupDomain

    suspend fun getGroupBalance(): Double

    suspend fun getGroupDomain(groupInvite: String): GroupDomain

    suspend fun removeUserOfGroup(userId: String)

    suspend fun hasGroup(): Boolean
}
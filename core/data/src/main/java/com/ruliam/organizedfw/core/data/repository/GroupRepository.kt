package com.ruliam.organizedfw.core.data.repository

import com.ruliam.organizedfw.core.data.model.GroupDomain
import com.ruliam.organizedfw.core.data.model.GroupUserDomain
import com.ruliam.organizedfw.core.data.util.RequestPendingResult

interface GroupRepository {

    /**
     * Return the users of the group of user
     *
     * @return List of users. The index 0 will be the user logged
     */
    suspend fun getUsers(): List<GroupUserDomain>

    /**
     * Return if there is some pending user waiting for to be accepted
     *
     * @return List of pending users
     */
    suspend fun checkIfPendingUsers(): List<GroupUserDomain?>

    /**
     * Get user logged groupId
     *
     * @return GroupId as String
     */
    suspend fun getUserGroupId(): String

    /**
     * Get the invite code of group that user is logged
     */
    suspend fun getGroupInviteCode(): String

    /**
     * @param groupUserDomain main user
     * @return GroupDomain with null id and pendingUsers
     */
    suspend fun createGroupDomain(groupUserDomain: GroupUserDomain): GroupDomain

    /**
     * @return the sum of all users balance
     */
    suspend fun getGroupBalance(): Double

    /**
     * Get GroupDomain by Group Invite Code
     *
     * Used when searching for a group
     * @return GroupDomain found with Invite Code
     */
    suspend fun getGroupDomain(groupInvite: String): GroupDomain

    /**
     * Get GroupDomain by Group ID
     *
     * Used when searching for a group
     * @return GroupDomain found with Group ID
     */
    suspend fun getGroupDomainById(groupId: String): GroupDomain

    /**
     * Add a pending user to a new group
     */
    suspend fun addPendingUserToGroup(user: GroupUserDomain)

    /**
     * Denied an request to enter in a group
     */
    suspend fun deniedPendingUser(user: GroupUserDomain)
    /**
     * Check if user with user has group
     */
    suspend fun hasGroup(): Boolean

    /**
     * Called by user that wants to enter in the group
     * Create a pending user in the group with invite code of param
     * @param ignoreCurrentPending is true when what to change the pending group
     */
    suspend fun askEnterGroup(inviteCode: String, ignoreCurrentPending: Boolean = false) : RequestPendingResult
}
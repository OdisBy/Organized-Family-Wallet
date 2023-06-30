package com.ruliam.organizedfw.core.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.ruliam.organizedfw.core.data.model.GroupDomain
import com.ruliam.organizedfw.core.data.model.GroupUserDomain
import com.ruliam.organizedfw.core.data.model.UserDomain
import com.ruliam.organizedfw.core.data.session.SessionManager
import com.ruliam.organizedfw.core.data.util.CanNotAddFirebase
import com.ruliam.organizedfw.core.data.util.DoesNotExist
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import javax.inject.Inject

internal class GroupRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val firebaseFirestore: FirebaseFirestore
) : GroupRepository {

    private var mainGroup: GroupDomain? = null

    private suspend fun getMainGroup() {
        val groupId = getUserGroupId()
        try {
            val groupSnapshot = firebaseFirestore.collection("groups")
                .document(groupId)
                .get()
                .await()

            mainGroup = groupSnapshot.toObject(GroupDomain::class.java)!!
        } catch (e: Exception) {
            Log.w(TAG, "Error retrieving users from group $groupId. Error: ${e.message}")
            throw Exception("error isn't in a group")
        }
    }


    override suspend fun getUsers(): List<GroupUserDomain> {
        val groupId = getUserGroupId()
        val userId = getUserId()

        return try {
            if(mainGroup == null){
                getMainGroup()
            }
            val mainUser = mainGroup!!.users.first { it.id == userId }
            val otherUsers = mainGroup!!.users.filterNot { it.id == userId }

            mutableListOf<GroupUserDomain>().apply {
                add(mainUser)
                addAll(otherUsers)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error retrieving users from group $groupId. Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun checkIfPendingUsers(): List<GroupUserDomain?> {
        if(mainGroup == null){
            getMainGroup()
        }
        return mainGroup?.pendingUsers ?: listOf()
    }

    override suspend fun getUserGroupId(): String {
        return sessionManager.getGroupId()!!
    }

    override suspend fun getGroupInviteCode(): String {
        if(mainGroup == null){
            getMainGroup()
        }
        return mainGroup!!.groupInvite!!
    }

    override suspend fun createGroupDomain(groupUserDomain: GroupUserDomain): GroupDomain {
        val groupInvite = generateInviteCode()
        return GroupDomain(
            id = null,
            groupInvite = groupInvite,
            users = mutableListOf(groupUserDomain),
            pendingUsers = mutableListOf()
        )
    }

    override suspend fun getGroupBalance(): Double {
        val totalBalance = 0.0
        val users = getUsers()
        for (user in users){
            val balance = user.balance
            totalBalance.plus(balance)
        }
        return totalBalance
    }

    override suspend fun getGroupDomain(groupInvite: String): GroupDomain {
        try {
            val groupRef = firebaseFirestore.collection("groups")

            val groupSnapshot = groupRef.whereEqualTo("groupInvite", groupInvite)
                .limit(1)
                .get()
                .await()

            if(!groupSnapshot.isEmpty){
                return groupSnapshot.documents
                    .firstOrNull()!!
                    .toObject(GroupDomain::class.java)!!
            } else {
                throw DoesNotExist("Group does not exist")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error on add user to group, error: ${e.message}")
            throw CanNotAddFirebase("Group does not exist")
        }
    }

    override suspend fun getGroupDomainById(groupId: String): GroupDomain {
        try {
            val groupRef = firebaseFirestore.collection("groups")

            val groupSnapshot = groupRef.whereEqualTo("id", groupId)
                .limit(1)
                .get()
                .await()

            if(!groupSnapshot.isEmpty){
                return groupSnapshot.documents
                    .firstOrNull()!!
                    .toObject(GroupDomain::class.java)!!
            } else {
                throw DoesNotExist("Group does not exist")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error on add user to group, error: ${e.message}")
            throw CanNotAddFirebase("Group does not exist")
        }
    }

    override suspend fun removeUserOfGroup(userId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun hasGroup(): Boolean {
        val userId = sessionManager.getUserId()
        val mainUser = firebaseToUserDomain(userId)
        return !mainUser?.groupId.isNullOrEmpty()
    }

    override suspend fun askEnterGroup(inviteCode: String) {
        val newGroup = getGroupDomain(inviteCode)
        val userId = sessionManager.getUserId()
        val userGroupDomain = mainGroup!!.users.firstOrNull { it.id == userId}

        val groupMutableUsers = newGroup.pendingUsers.toMutableList()
        val userWithId = groupMutableUsers.firstOrNull { it?.id == userId }
        if (userWithId != null) {
            return
        }
        groupMutableUsers.add(userGroupDomain)

        val groupRef = firebaseFirestore.collection("groups").document(newGroup.id!!)
        firebaseFirestore.runBatch {
            groupRef.update("pendingUsers", groupMutableUsers)
        }
    }


    private fun generateInviteCode(): String {
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val codeLength = 6

        val random = SecureRandom()
        val code = StringBuilder()

        for (i in 0 until codeLength) {
            val randomIndex = random.nextInt(characters.length)
            val randomChar = characters[randomIndex]
            code.append(randomChar)
        }

        return code.toString()
    }

    private suspend fun getFirebaseUser(userId: String) : DocumentSnapshot?{
        return try {
            firebaseFirestore.collection("users")
                .document(userId)
                .get()
                .await()
        } catch (e: Exception) {
            Log.w(TAG, "Error on try to get user with id $userId, error: ${e.message}")
            return null
        }
    }
    private suspend fun firebaseToUserDomain(userId: String) : UserDomain? {
        return getFirebaseUser(userId)!!.toObject(UserDomain::class.java)
    }
    private fun getUserId(): String {
        return sessionManager.getUserId()
    }
    companion object{
        const val TAG = "GroupRepository"
    }
}
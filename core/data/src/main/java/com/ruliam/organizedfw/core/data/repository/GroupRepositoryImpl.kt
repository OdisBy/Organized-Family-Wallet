package com.ruliam.organizedfw.core.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.ruliam.organizedfw.core.data.model.GroupDomain
import com.ruliam.organizedfw.core.data.model.GroupUserDomain
import com.ruliam.organizedfw.core.data.model.NotificationData
import com.ruliam.organizedfw.core.data.model.PushNotification
import com.ruliam.organizedfw.core.data.model.UserDomain
import com.ruliam.organizedfw.core.data.network.ApiManager
import com.ruliam.organizedfw.core.data.session.SessionManager
import com.ruliam.organizedfw.core.data.util.CanNotAddFirebase
import com.ruliam.organizedfw.core.data.util.DoesNotExist
import com.ruliam.organizedfw.core.data.util.RequestPendingResult
import kotlinx.coroutines.tasks.await
import java.security.SecureRandom
import javax.inject.Inject

internal class GroupRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val firebaseFirestore: FirebaseFirestore,
    private val firebaseMessaging: FirebaseMessaging,
    private val apiManager: ApiManager
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
                throw DoesNotExist
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error on getGroupDomain, error: ${e.message}")
            throw e
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
                throw DoesNotExist
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error on add user to group, error: ${e.message}")
            throw CanNotAddFirebase("Group does not exist")
        }
    }

    override suspend fun addPendingUserToGroup(user: GroupUserDomain) {
        try{
            val groupId = sessionManager.getGroupId()!!
            val userDomain = getUserDomain(user.id)
            val oldGroup = getGroupDomainById(userDomain.groupId)
            val newGroup = getGroupDomainById(groupId)

            val usersOldGroup = oldGroup.users.toMutableList()
            val usersNewGroup = newGroup.users.toMutableList()

            usersOldGroup.remove(user)
            usersNewGroup.add(user)
            userDomain.groupId = newGroup.id!!

            val newPendingMutableList = newGroup.pendingUsers.toMutableList()

            newPendingMutableList.remove(user)

            val userDomainMap : Map<String, Any?> = mapOf("groupId" to groupId, "pendingGroupId" to null)

            val newGroupMap: Map<String, Any> = mapOf("users" to usersNewGroup, "pendingUsers" to newPendingMutableList)

            val oldGroupRef = firebaseFirestore.collection("groups").document(oldGroup.id!!)
            val newGroupRef = firebaseFirestore.collection("groups").document(newGroup.id!!)
            val userRef = firebaseFirestore.collection("users").document(user.id)


            if(usersOldGroup.isEmpty()) {
                firebaseFirestore.runBatch {
                    userRef.update(userDomainMap)
                    oldGroupRef.delete()
                    newGroupRef.update(newGroupMap)
                }
            } else {
                val oldGroupMap: Map<String, List<GroupUserDomain>> = mapOf("users" to usersOldGroup)

                firebaseFirestore.runBatch {
                    userRef.update(userDomainMap)
                    oldGroupRef.update(oldGroupMap)
                    newGroupRef.update(newGroupMap)
                }
            }

            firebaseMessaging.unsubscribeFromTopic(oldGroup.id!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Unsubscribe from old group success")
                    } else {
                        Log.e(TAG, "Unsubscribe from old group success failed", task.exception)
                    }
                }
            firebaseMessaging.subscribeToTopic(newGroup.id!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Subscribe on group topic success")
                    } else {
                        Log.e(TAG, "Subscribe on group topic failed", task.exception)
                    }
                }.await()
            sendMessageAboutNewUser(user, newGroup)

            getMainGroup()
        } catch (e: Exception){
            e.stackTrace
            Log.w(TAG, "Error on add pending user to group, e = ${e.message}")
            throw e
        }
    }

    override suspend fun deniedPendingUser(user: GroupUserDomain) {
        try{
            val actualGroup = mainGroup!!
            val newPendingMutableList = actualGroup.pendingUsers.toMutableList()
            newPendingMutableList.remove(user)

            val userDomainMap : Map<String, Any?> = mapOf("pendingGroupId" to null)
            val newGroupMap: Map<String, Any> = mapOf("pendingUsers" to newPendingMutableList)

            val groupRef = firebaseFirestore.collection("groups").document(actualGroup.id!!)
            val userRef = firebaseFirestore.collection("users").document(user.id)
            firebaseFirestore.runBatch {
                userRef.update(userDomainMap)
                groupRef.update(newGroupMap)
            }

            getMainGroup()
        } catch (e: Exception){
            e.stackTrace
            Log.w(TAG, "Error on add pending user to group, e = ${e.message}")
            throw e
        }
    }

    override suspend fun hasGroup(): Boolean {
        val userId = sessionManager.getUserId()
        val mainUser = firebaseToUserDomain(userId)
        return !mainUser?.groupId.isNullOrEmpty()
    }

    override suspend fun askEnterGroup(inviteCode: String, ignoreCurrentPending: Boolean): RequestPendingResult {
        if(mainGroup == null){
            getMainGroup()
        }
        lateinit var newGroup: GroupDomain
        var currentGroupRef: DocumentReference? = null
        var currentGroupUpdate: Map<String, MutableList<GroupUserDomain?>>? = null
        val userId = sessionManager.getUserId()
        val userDomain = getUserDomain(userId)
        val userGroupDomain = mainGroup!!.users.firstOrNull { it.id == userId}

        // Check if group exists
        try{
            newGroup = getGroupDomain(inviteCode)
        } catch (e: Exception){
            return if(e == DoesNotExist){
                RequestPendingResult.groupDoesNotExist
            } else{
                RequestPendingResult.Error(e.message.toString())
            }
        }

        if(ignoreCurrentPending){
            val currentPendingGroupId = userDomain.pendingGroupId!!
            val currentGroup = getGroupDomainById(currentPendingGroupId)
            val currentGroupPending = currentGroup.pendingUsers.toMutableList()
            currentGroupPending.remove(userGroupDomain)

            currentGroupRef = firebaseFirestore.collection("groups").document(currentGroup.id!!)
            currentGroupUpdate = mapOf("pendingUsers" to currentGroupPending)
        } else{
            if(userDomain.pendingGroupId != null){
                return if(userDomain.pendingGroupId == newGroup.id){
                    RequestPendingResult.alreadyPendingUserInThisGroup
                } else{
                    RequestPendingResult.alreadyRequestForAnotherGroup
                }
            }
        }

        val newUserPendingId = newGroup.id!!
        val userGroupIdUpdate = mapOf("pendingGroupId" to newUserPendingId)

        val groupPendingMutableUsers = newGroup.pendingUsers.toMutableList()
        groupPendingMutableUsers.add(userGroupDomain)
        val newGroupUpdatedList = mapOf("pendingUsers" to groupPendingMutableUsers)

        val userRef = firebaseFirestore.collection("users").document(userId)
        val newGroupRef = firebaseFirestore.collection("groups").document(newGroup.id!!)

        if(currentGroupRef != null){
            firebaseFirestore.runBatch {
                userRef.update(userGroupIdUpdate)
                newGroupRef.update(newGroupUpdatedList)
                currentGroupRef.update(currentGroupUpdate!!)
            }
            return RequestPendingResult.Success
        }

        firebaseFirestore.runBatch {
            userRef.update(userGroupIdUpdate)
            newGroupRef.update(newGroupUpdatedList)
        }
        return RequestPendingResult.Success
    }

    private suspend fun sendMessageToNewUser(user: GroupUserDomain) {
        val title = "Você está em um novo grupo"
        val message = "Parabéns, agora você faz parte de um novo grupo!"
        val notification = PushNotification(
            NotificationData(title, message),
            to = user.id
        )
        sendNotification(notification)
    }

    private suspend fun sendMessageAboutNewUser(user: GroupUserDomain, groupDomain: GroupDomain) {
        val title = "Novo usuário em seu grupo"
        val message = "Agora ${user.username} faz parte do grupo!"
        val notification = PushNotification(
            NotificationData(title, message),
            to = "/topics/${groupDomain.id!!}"
        )
        sendNotification(notification)
    }

    private suspend fun sendNotification(notification: PushNotification) {
        apiManager.postNotification(notification)
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
    private suspend fun getUserDomain(userId: String) : UserDomain {
        try {
            val userRef = firebaseFirestore.collection("users")

            val userSnapshot = userRef.whereEqualTo("id", userId)
                .limit(1)
                .get()
                .await()

            if(!userSnapshot.isEmpty){
                return userSnapshot.documents
                    .firstOrNull()!!
                    .toObject(UserDomain::class.java)!!
            } else {
                throw DoesNotExist
            }

        } catch (e: Exception) {
            Log.w(TAG, "Error on getUserDomain, error: ${e.message}")
            throw e
        }
    }
    companion object{
        const val TAG = "GroupRepository"
    }
}
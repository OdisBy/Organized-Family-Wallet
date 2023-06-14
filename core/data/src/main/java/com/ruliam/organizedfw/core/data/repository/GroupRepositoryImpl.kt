package com.ruliam.organizedfw.core.data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.ruliam.organizedfw.core.data.model.GroupDomain
import com.ruliam.organizedfw.core.data.model.GroupUsersDomain
import com.ruliam.organizedfw.core.data.model.UserDomain
import com.ruliam.organizedfw.core.data.session.SessionManager
import com.ruliam.organizedfw.core.data.util.CanNotAddFirebase
import com.ruliam.organizedfw.core.data.util.DoesNotExist
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.random.Random

internal class GroupRepositoryImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val firebaseFirestore: FirebaseFirestore
) : GroupRepository {
    override suspend fun getUsers(): List<GroupUsersDomain?> {
        val groupId = getUserGroupId()
        val userId = sessionManager.getUserId()

        return try {
            val groupSnapshot = firebaseFirestore.collection("groups")
                .document(groupId)
                .get()
                .await()

            val group = groupSnapshot.toObject(GroupDomain::class.java)
            val mainUser = group?.users?.firstOrNull { it.id == userId }
            val otherUsers = group?.users?.filterNot { it.id == userId }

            mutableListOf<GroupUsersDomain?>().apply {
                add(mainUser)
                if (otherUsers != null) {
                    addAll(otherUsers)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error retrieving users from group $groupId. Error: ${e.message}")
            emptyList()
        }
    }


    override suspend fun getUserGroupId(): String {
        return try {
            val userId = sessionManager.getUserId()
            val mainUser = firebaseToUserDomain(userId)
            mainUser!!.groupId!!
        } catch (e: Exception){
            Log.w(TAG, "Error on get user groupId, error: ${e.message}")
            throw Exception("Can't get main user")
        }
    }

    override suspend fun createGroupDomain(groupUsersDomain: GroupUsersDomain): GroupDomain {
        try {
            val groupInvite = generateInviteCode()
            return GroupDomain(
                id = null,
                groupInvite = groupInvite,
                users = mutableListOf(groupUsersDomain)
            )

        } catch (e: Exception) {
            Log.w(TAG, "Error on create group, error: ${e.message}")
            throw Exception("Cant create group")
        }
    }

    override suspend fun getGroupBalance(): Double {
        val totalBalance = 0.0

        val users = getUsers()
        for (user in users){
            val balance = user!!.balance
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



//                val groupId = documentSnapshot!!.id
//
//                val usersGroup = documentSnapshot.get("users") as? MutableList<GroupUsersDomain>
//
//                usersGroup!!.add(groupUserDomain)
//
//                val updatedData = hashMapOf<String, Any>("users" to usersGroup)
//
//                groupRef.document(groupId)
//                    .update(updatedData)
//                    .await()
//
//                Log.d(TAG, "User added to group successfully")
//                return groupId

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


    private fun generateInviteCode(): String {
        val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val codeLength = 6

        val random = Random(System.currentTimeMillis())
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

    private suspend fun getFirebaseGroup(groupId: String) : DocumentSnapshot?{
        return try {
            firebaseFirestore.collection("group")
                .document(groupId)
                .get()
                .await()
        } catch (e: Exception) {
            Log.w(TAG, "Error on try to get group with id $groupId, error: ${e.message}")
            return null
        }
    }
    private suspend fun firebaseToGroupDomain(groupId: String) : GroupDomain? {
        return getFirebaseGroup(groupId)!!.toObject(GroupDomain::class.java)
    }


    companion object{
        const val TAG = "GroupRepository"
    }
}
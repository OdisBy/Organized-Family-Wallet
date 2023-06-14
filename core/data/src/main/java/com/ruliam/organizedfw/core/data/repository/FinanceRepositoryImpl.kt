package com.ruliam.organizedfw.core.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ruliam.organizedfw.core.data.model.GroupDomain
import com.ruliam.organizedfw.core.data.model.TransactionDomain
import com.ruliam.organizedfw.core.data.model.UserDomain
import com.ruliam.organizedfw.core.data.session.SessionManager
import com.ruliam.organizedfw.core.data.util.FinanceRepositoryException
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

internal class FinanceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sessionManager: SessionManager
) :
    FinanceRepository {

    override suspend fun fetchAllByUser(userId: UUID): List<TransactionDomain> {
        val finances = mutableListOf<TransactionDomain>()
        val query = firestore.collection("transactions")
            .whereEqualTo("userId", userId)

        try {
            val querySnapshot = query.get().await()
            for (doc in querySnapshot) {
                val finance = doc.toObject(TransactionDomain::class.java)
                finances.add(finance)
            }
        } catch (e: FinanceRepositoryException) {
            Log.e(TAG, "Error getting finances by user id $userId: ", e)
            throw FinanceRepositoryException("Error getting finances by user id $userId", e)
        }

        return finances
    }


    override suspend fun fetchMonthByUser(month: Long, userId: String): List<TransactionDomain> {
        TODO("Not yet implemented")
    }

    override suspend fun fetchGroupFinancesByMonth(month: Int): List<TransactionDomain> {

        val rangeCalendar = Calendar.getInstance()
        rangeCalendar.add(Calendar.MONTH, -1)

        val rangeDate = rangeCalendar.time

        val groupId = sessionManager.getGroupId()!!
        Log.w(TAG, "EndOfMonth = $rangeDate, groupId = $groupId")
        val transactionsRef =
            firestore
                .collection("groups")
                .document(groupId)
                .collection("transactions")

        val querySnapshot =
            transactionsRef
            .whereGreaterThan("date", rangeDate)
                .get()
                .await()

        return try{
            querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(TransactionDomain::class.java)
            }
        } catch (e: Exception){
            Log.e(TAG, "Error toObject TransactionDomain, error: ${e.message.toString()}")
            emptyList()
        }
    }

    override suspend fun getLastByUser(userId: String): TransactionDomain? {

        val groupId = sessionManager.getGroupId()!!

        val transaction =
            firestore
                .collection("groups")
                .document(groupId)
                .collection("transactions")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.ASCENDING)
                .limitToLast(1)
                .get()
                .await()

        return transaction.documents.firstOrNull()?.toObject(TransactionDomain::class.java)
    }


    override suspend fun add(
        name: String,
        date: Date,
        description: String,
        amount: Double,
        recurrent: Boolean
    ) {
        val userId = sessionManager.getUserId()
        val groupId = sessionManager.getGroupId()!!

        val finance = TransactionDomain(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = name,
            date = date,
            description = description,
            amount = amount,
            recurrent = recurrent
        )


        val transactionRef = firestore
            .collection("groups").document(groupId)
            .collection("transactions").document(finance.id)

        val groupRef = firestore
            .collection("groups")
            .document(groupId)


        val groupObject = groupRef.get()
            .await()
            .toObject(GroupDomain::class.java)


        val userRef = firestore.collection("users").document(userId)

        val user = userRef.get()
            .await()
            .toObject(UserDomain::class.java)!!

        val newBalance = user.balance.plus(amount)

        groupObject!!.users.firstOrNull() { it.id == userId }?.balance = newBalance
        val newBalanceHashMap: HashMap<String, Any> = hashMapOf ("balance" to newBalance)

        finance.userName = user.username!!
        try {
            firestore.runBatch { batch ->
                batch.set(transactionRef, finance)
                batch.update(userRef, newBalanceHashMap)
                batch.set(groupRef, groupObject)
            }.await()
        } catch (e: Exception) {
            throw FinanceRepositoryException("Failed to add finance or update balance, error ${e.message.toString()}", e)
        }
    }

    companion object {
        private const val TAG = "FinanceRepository"
    }
}
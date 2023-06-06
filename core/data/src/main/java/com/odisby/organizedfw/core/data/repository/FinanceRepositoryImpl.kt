package com.odisby.organizedfw.core.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.odisby.organizedfw.core.data.model.TransactionDomain
import com.odisby.organizedfw.core.data.model.UserDomain
import com.odisby.organizedfw.core.data.util.FinanceRepositoryException
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

internal class FinanceRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
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
        val endOfMonth = month - 2592000000L // One month ago in Long
        Log.w(TAG, "EndOfMonth = $endOfMonth, UserId = $userId")
        val querySnapshot = firestore.collection("transactions")
            .whereEqualTo("userId", userId)
            .whereGreaterThan("date", endOfMonth)
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
        val querySnapshot = firestore.collection("transactions")
            .whereEqualTo("userId", userId)
            .orderBy("date", Query.Direction.ASCENDING)
            .limitToLast(1)
            .get()
            .await()

        return querySnapshot.documents.firstOrNull()?.toObject(TransactionDomain::class.java)
    }


    override suspend fun add(
        userId: String,
        name: String,
        date: Long,
        description: String,
        amount: Double,
        recurrent: Boolean
    ) {
//        val localDateToLong = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val finance = TransactionDomain(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = name,
            date = date,
            description = description,
            amount = amount,
            recurrent = recurrent
        )


        val transactionRef = firestore.collection("transactions").document(finance.id)
        val userRef = firestore.collection("users").document(userId)

        val userBalance = userRef.get()
            .await()
            .toObject(UserDomain::class.java)!!
            .balance

        val newBalance = userBalance.plus(amount)
        val newBalanceHashMap: HashMap<String, Any> = hashMapOf ("balance" to newBalance)
        try {
            firestore.runBatch { batch ->
                batch.set(transactionRef, finance)
                batch.update(userRef, newBalanceHashMap)
            }.await()
        } catch (e: Exception) {
            throw FinanceRepositoryException("Failed to add finance or update balance, error ${e.message.toString()}", e)
        }
    }

    companion object {
        private const val TAG = "FinanceRepository"
    }
}
package com.ruliam.organizedfw.features.home.domain

import android.util.Log
import com.ruliam.organizedfw.core.data.repository.AvatarRepository
import com.ruliam.organizedfw.core.data.repository.FinanceRepository
import com.ruliam.organizedfw.core.data.repository.GroupRepository
import com.ruliam.organizedfw.core.data.util.AmountUtils
import com.ruliam.organizedfw.features.home.model.BalanceCardItem
import javax.inject.Inject

class GetBalanceUseCaseImpl @Inject constructor(
    private val groupRepository: GroupRepository,
    private val financeRepository: FinanceRepository,
    private val avatarRepository: AvatarRepository
) : GetBalanceUseCase {
    override suspend fun invoke(): List<BalanceCardItem> {
        try{
            val amountUtils = AmountUtils()
            val balanceCards = mutableListOf<BalanceCardItem>()
            val groupUsers = groupRepository.getUsers()

            groupUsers.forEach { user ->
                val balanceString = amountUtils.formatTotalBalance(user!!.balance)
                val lastTransactionAmount = financeRepository.getLastByUser(user.id)?.amount ?: 0.0
                val lastTransactionString = amountUtils.formatBalance(lastTransactionAmount)
                val avatar = avatarRepository.getAvatarById(user.id)!!
                val card = BalanceCardItem(
                    userId = user.id,
                    balance = balanceString,
                    lastTransaction = lastTransactionString,
                    avatar = avatar
                )
                Log.d(TAG, "Adding card: ${card.balance}")
                balanceCards.add(card)
            }

            Log.d(TAG, "Adding total cards: ${balanceCards.size}")
            return balanceCards
        } catch (e: Exception) {
            Log.d(TAG, "Getting error $e on try to get cards")
            return mutableListOf()
        }
    }

    companion object {
        private const val TAG = "GetBalance"
    }
}
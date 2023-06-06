package com.odisby.organizedfw.features.home.domain

import android.util.Log
import com.odisby.organizedfw.features.home.model.FinanceEntity
import com.odisby.organizedfw.features.home.model.FinanceItemType
import com.odisby.organizedfw.features.home.model.HeaderItemType
import com.odisby.organizedfw.features.home.model.ListItemType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class GetFinancesOfMonthUseCaseImpl @Inject constructor(
    private val financeRepository: com.odisby.organizedfw.core.data.repository.FinanceRepository,
    private val usersRepository: com.odisby.organizedfw.core.data.repository.UsersRepository,
//    private val sessionManager: SessionManager
) : GetFinancesOfMonthUseCase {
    override suspend fun invoke(userId: String): List<ListItemType> {
        val currentDate = LocalDate.now()
        val localDateToLong = currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

        val financesByDate = try {
            financeRepository.fetchMonthByUser(localDateToLong, userId)
                .map { finance ->
                    val user = usersRepository.getUserById(userId)!!
                    val zoneId = ZoneId.of("America/Sao_Paulo")
                    val localDate = Instant.ofEpochMilli(finance.date).atZone(zoneId).toLocalDate()
                    FinanceEntity(
                        id = finance.id,
                        userId = userId,
                        username = user.username!!,
                        name = finance.title,
                        date = localDate,
                        description = finance.description,
                        amount = finance.amount,
                        recurrent = finance.recurrent
                    )
                }
                .sortedBy { it.date }
                .groupBy { it.date }
        } catch (e: Exception) {
            Log.d(TAG, "Error on try to object transaction repo to FinanceEntity, error: ${e.message.toString()}")
            return emptyList()
        }

        val responseList = mutableListOf<ListItemType>()

        financesByDate.forEach { (date, finances) ->
            responseList.add(
                0,
                HeaderItemType(
                    date!!.format(DateTimeFormatter.ofPattern("dd MMMM", Locale("pt", "BR")))
                )
            )
            finances.forEach { finance ->
                responseList.add(0, FinanceItemType(finance))
            }
        }

        return responseList
    }

    companion object {
        private const val TAG = "GetFinancesOfMonth"
    }
}
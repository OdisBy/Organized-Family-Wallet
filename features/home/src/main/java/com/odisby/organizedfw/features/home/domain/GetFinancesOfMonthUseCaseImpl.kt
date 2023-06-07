package com.odisby.organizedfw.features.home.domain

import android.util.Log
import com.odisby.organizedfw.features.home.model.FinanceEntity
import com.odisby.organizedfw.features.home.model.FinanceItemType
import com.odisby.organizedfw.features.home.model.HeaderItemType
import com.odisby.organizedfw.features.home.model.ListItemType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
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
        return try {
            val currentDate = LocalDate.now()
            val localDateToLong = currentDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

            val finances = financeRepository.fetchMonthByUser(localDateToLong, userId)
                .map { finance ->
                    val user = usersRepository.getUserById(userId)!!
                    val zoneId = ZoneId.of("America/Sao_Paulo")
                    val localDateTime: LocalDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(finance.date), zoneId)
                    FinanceEntity(
                        id = finance.id,
                        userId = userId,
                        username = user.username!!,
                        name = finance.title,
                        date = localDateTime,
                        description = finance.description,
                        amount = finance.amount,
                        recurrent = finance.recurrent
                    )
                }

            val financesByDate = finances
                .sortedBy { it.date }
                .groupBy { it.date!!.dayOfMonth }

            val responseList = mutableListOf<ListItemType>()

            financesByDate.toSortedMap(compareByDescending { it }).values.forEach { financesMap ->
                val date = finances.first().date
                responseList.add(
                    HeaderItemType(
                        date!!.format(DateTimeFormatter
                            .ofPattern("dd MMMM",
                            Locale("pt", "BR")))
                    )
                )
                financesMap.sortedByDescending { it.date }.forEach { finance ->
                    responseList.add(FinanceItemType(finance))
                }
            }

            responseList
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving finances: ${e.message}", e)
            emptyList()
        }
    }



    companion object {
        private const val TAG = "GetFinancesOfMonth"
    }
}
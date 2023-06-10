package com.odisby.organizedfw.features.home.domain

import android.util.Log
import com.odisby.organizedfw.features.home.model.FinanceEntity
import com.odisby.organizedfw.features.home.model.FinanceItemType
import com.odisby.organizedfw.features.home.model.HeaderItemType
import com.odisby.organizedfw.features.home.model.ListItemType
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class GetFinancesOfMonthUseCaseImpl @Inject constructor(
    private val financeRepository: com.odisby.organizedfw.core.data.repository.FinanceRepository,
) : GetFinancesOfMonthUseCase {

    private var zoneId = ZoneId.of("America/Sao_Paulo")

    override suspend fun invoke(): List<ListItemType> {
        return try {
            val currentDate = LocalDate.now()

            val finances = financeRepository.fetchGroupFinancesByMonth(currentDate.monthValue)
                .map { finance ->
                    FinanceEntity(
                        id = finance.id,
                        userId = finance.userId,
                        username = finance.userName,
                        name = finance.title,
                        date = finance.date,
                        description = finance.description,
                        amount = finance.amount,
                        recurrent = finance.recurrent
                    )
                }

            val financesByDate: Map<LocalDate, List<FinanceEntity>> = finances
                .sortedByDescending {
                    val zonedDateTime = ZonedDateTime.ofInstant(it.date!!.toInstant(), zoneId)
                    zonedDateTime
                }
                .groupBy {
                    val zonedDateTime = ZonedDateTime.ofInstant(it.date!!.toInstant(), zoneId)
                    zonedDateTime.toLocalDate()
                }

            val responseList = mutableListOf<ListItemType>()

            financesByDate.forEach { (date, finances) ->
                val formattedDate = date.format(DateTimeFormatter.ofPattern("dd MMMM", Locale("pt", "BR")))
                responseList.add(HeaderItemType(formattedDate))
                finances.forEach { finance ->
                    responseList.add(FinanceItemType(finance))
                }
            }



            return responseList
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving finances: ${e.message}", e)
            emptyList()
        }
    }

    companion object {
        private const val TAG = "GetFinancesOfMonth"
    }
}
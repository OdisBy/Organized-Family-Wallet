package com.odisby.organizedfw.features.home.domain

import com.odisby.organizedfw.features.home.model.FinanceEntity
import com.odisby.organizedfw.features.home.model.ListItemType

interface GetFinancesOfMonthUseCase {
    suspend operator fun invoke(): List<ListItemType>
}
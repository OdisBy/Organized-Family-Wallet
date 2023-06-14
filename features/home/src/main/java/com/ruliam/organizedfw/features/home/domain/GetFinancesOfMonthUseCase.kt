package com.ruliam.organizedfw.features.home.domain

import com.ruliam.organizedfw.features.home.model.ListItemType

interface GetFinancesOfMonthUseCase {
    suspend operator fun invoke(): List<ListItemType>
}
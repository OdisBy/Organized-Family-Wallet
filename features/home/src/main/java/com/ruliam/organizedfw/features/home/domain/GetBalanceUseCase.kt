package com.ruliam.organizedfw.features.home.domain

import com.ruliam.organizedfw.features.home.model.BalanceCardItem

interface GetBalanceUseCase {
    suspend operator fun invoke(): List<BalanceCardItem>
}
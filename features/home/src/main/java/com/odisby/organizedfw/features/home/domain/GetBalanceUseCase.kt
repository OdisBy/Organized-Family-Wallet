package com.odisby.organizedfw.features.home.domain

import com.odisby.organizedfw.features.home.model.BalanceCardItem

interface GetBalanceUseCase {
    suspend operator fun invoke(): List<BalanceCardItem>
}
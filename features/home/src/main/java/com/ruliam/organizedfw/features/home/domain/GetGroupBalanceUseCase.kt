package com.ruliam.organizedfw.features.home.domain

interface GetGroupBalanceUseCase {
    suspend operator fun invoke(): Double
}
package com.odisby.organizedfw.features.home.domain

interface GetGroupBalanceUseCase {
    suspend operator fun invoke(): Double
}
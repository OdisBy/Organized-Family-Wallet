package com.odisby.organizedfw.features.home.di


import com.odisby.organizedfw.features.home.domain.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class CollectionsModule {

    @Singleton
    @Binds
    abstract fun providesGetBalanceUseCase(
        impl: GetBalanceUseCaseImpl
    ) : GetBalanceUseCase

    @Singleton
    @Binds
    abstract fun providesGetFinancesOfMonthUseCase(
        impl: GetFinancesOfMonthUseCaseImpl
    ) : GetFinancesOfMonthUseCase

    @Singleton
    @Binds
    abstract fun providesGetGroupBalanceUseCase(
        impl: GetGroupBalanceUseCaseImpl
    ) : GetGroupBalanceUseCase
}
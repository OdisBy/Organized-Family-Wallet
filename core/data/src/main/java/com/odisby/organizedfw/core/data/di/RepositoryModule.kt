package com.odisby.organizedfw.core.data.di

import com.odisby.organizedfw.core.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun providesFinanceRepository(impl: FinanceRepositoryImpl) : FinanceRepository

    @Singleton
    @Binds
    abstract fun providesUsersRepository(impl: UsersRepositoryImpl) : UsersRepository

    @Singleton
    @Binds
    abstract fun providesAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    abstract fun providesGroupRepository(impl: GroupRepositoryImpl): GroupRepository

    @Singleton
    @Binds
    abstract fun providesAvatarRepository(impl: AvatarRepositoryImpl): AvatarRepository
}
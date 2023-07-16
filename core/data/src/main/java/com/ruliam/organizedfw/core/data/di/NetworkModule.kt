package com.ruliam.organizedfw.core.data.di

import com.ruliam.organizedfw.core.data.network.ApiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object NetworkModule {
    @Singleton
    @Provides
    fun providesApiManager() = ApiManager()
}
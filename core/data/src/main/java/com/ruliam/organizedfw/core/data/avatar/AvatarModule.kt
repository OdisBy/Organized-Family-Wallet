package com.ruliam.organizedfw.core.data.avatar

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AvatarModule {
    @Provides
    @Singleton
    fun providesUserAvatar(@ApplicationContext context: Context) = UserAvatar(context)
}
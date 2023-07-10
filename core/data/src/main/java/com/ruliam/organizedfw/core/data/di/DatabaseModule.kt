package com.ruliam.organizedfw.core.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object DatabaseModule {

    @Singleton
    @Provides
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun providesFirebaseFirestore() = Firebase.firestore

    @Singleton
    @Provides
    fun providesFirebaseStorage() = Firebase.storage

    @Singleton
    @Provides
    fun providesFirebaseMessaging() = FirebaseMessaging.getInstance()
}
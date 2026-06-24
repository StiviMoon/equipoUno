package com.example.pb.di

import com.example.pb.data.repository.RetosRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRetosRepository(
        firestore: FirebaseFirestore
    ): RetosRepository {
        return RetosRepository(firestore)
    }
}

package com.example.ydown.di

import android.app.Application
import com.example.ydown.repositories.PythonRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePythonRepository(context: Application): PythonRepository {
        return PythonRepository(context)
    }
}
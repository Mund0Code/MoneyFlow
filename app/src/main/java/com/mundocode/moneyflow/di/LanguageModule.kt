package com.mundocode.moneyflow.di

import android.content.Context
import com.mundocode.moneyflow.core.LanguageDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LanguageModule {

    @Provides
    @Singleton
    fun provideLanguageDataStore(@ApplicationContext context: Context): LanguageDataStore {
        return LanguageDataStore(context)
    }
}
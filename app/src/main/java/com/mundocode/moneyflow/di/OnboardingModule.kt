package com.mundocode.moneyflow.di

import android.content.Context
import com.mundocode.moneyflow.core.OnboardingDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OnboardingModule {

    @Provides
    @Singleton
    fun provideOnboardingDataStore(@ApplicationContext context: Context): OnboardingDataStore {
        return OnboardingDataStore(context)
    }
}

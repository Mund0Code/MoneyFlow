package com.mundocode.moneyflow.core

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.onboardingDataStore by preferencesDataStore("onboarding")

class OnboardingDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ONBOARDING_KEY = booleanPreferencesKey("onboarding_completed")
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.onboardingDataStore.data
        .map { it[ONBOARDING_KEY] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.onboardingDataStore.edit {
            it[ONBOARDING_KEY] = completed
        }
    }
}
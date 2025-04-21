package com.mundocode.moneyflow.ui.screens.onBoarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundocode.moneyflow.core.OnboardingDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.bouncycastle.crypto.params.Blake3Parameters.context
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingDataStore: OnboardingDataStore
) : ViewModel() {

    val isCompleted = onboardingDataStore.onboardingCompletedFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun markCompletedAsync() {
        viewModelScope.launch {
            onboardingDataStore.setOnboardingCompleted(true)
        }
    }
}

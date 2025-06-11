package com.mundocode.moneyflow.ui.screens.onBoarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundocode.moneyflow.core.OnboardingDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingDataStore: OnboardingDataStore,
) : ViewModel() {

    val isCompleted = onboardingDataStore.onboardingCompletedFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun markCompletedAsync() {
        viewModelScope.launch {
            onboardingDataStore.setOnboardingCompleted(true)
        }
    }
}

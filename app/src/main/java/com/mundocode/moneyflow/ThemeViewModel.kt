package com.mundocode.moneyflow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundocode.moneyflow.core.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode = _isDarkMode.asStateFlow()

    private val _selectedColorIndex = MutableStateFlow(0)
    val selectedColorIndex = _selectedColorIndex.asStateFlow()

    init {
        viewModelScope.launch {
            _isDarkMode.value = settingsDataStore.darkModeFlow.first()
            _selectedColorIndex.value = settingsDataStore.colorSchemeFlow.first()
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.saveDarkMode(enabled)
            _isDarkMode.value = enabled
        }
    }

    fun setColorScheme(index: Int) {
        viewModelScope.launch {
            settingsDataStore.saveColorScheme(index)
            _selectedColorIndex.value = index
        }
    }
}


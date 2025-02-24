package com.mundocode.moneyflow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mundocode.moneyflow.core.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class ThemeViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)

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

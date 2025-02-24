package com.mundocode.moneyflow.core

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsDataStore(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val COLOR_SCHEME_KEY = intPreferencesKey("color_scheme")
    }

    val darkModeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    val colorSchemeFlow: Flow<Int> = dataStore.data.map { preferences ->
        preferences[COLOR_SCHEME_KEY] ?: 0 // Color por defecto
    }

    suspend fun saveDarkMode(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[DARK_MODE_KEY] = enabled }
    }

    suspend fun saveColorScheme(colorIndex: Int) {
        dataStore.edit { preferences -> preferences[COLOR_SCHEME_KEY] = colorIndex }
    }
}

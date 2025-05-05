package com.mundocode.moneyflow.core

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.languageDataStore by preferencesDataStore(name = "settingsLanguage")

class LanguageDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("selected_language")
    }

    val languageFlow = context.languageDataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "es"
    }

    suspend fun setLanguage(languageCode: String) {
        context.languageDataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = languageCode
        }
    }

    fun getLanguageBlocking(): String {
        return runBlocking { languageFlow.first() }
    }
}
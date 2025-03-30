package com.mundocode.moneyflow

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundocode.moneyflow.core.LanguageDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val dataStore: LanguageDataStore
) : ViewModel() {

    val languageFlow = dataStore.languageFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, "es")

    fun changeLanguage(languageCode: String) {
        viewModelScope.launch {
            dataStore.setLanguage(languageCode)
        }
    }

    fun restartApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

}
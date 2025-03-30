package com.mundocode.moneyflow

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.mundocode.moneyflow.core.LanguageDataStore
import com.mundocode.moneyflow.core.LanguageManager
import com.mundocode.moneyflow.navigation.AppNavigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val dataStore = LanguageDataStore(newBase)
        val lang = dataStore.getLanguageBlocking()
        val context = LanguageManager.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel: ThemeViewModel = viewModel() // Instancia centralizada
            FirebaseApp.initializeApp(this) // Inicializa Firebase
            MyAppTheme(themeViewModel) {  // Pasamos el ViewModel a MyAppTheme
                AppNavigator(themeViewModel)
            }
        }
    }
}
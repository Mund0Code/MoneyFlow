package com.mundocode.moneyflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.mundocode.moneyflow.navigation.AppNavigator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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
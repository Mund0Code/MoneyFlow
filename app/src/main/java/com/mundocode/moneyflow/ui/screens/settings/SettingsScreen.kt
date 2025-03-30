package com.mundocode.moneyflow.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mundocode.moneyflow.LanguageViewModel
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.ThemeViewModel
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    navController: NavHostController,
    viewModel: LanguageViewModel = hiltViewModel()
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = { CustomTopAppBar(navController, "Configuración") },
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Column(modifier = Modifier.padding(it)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Text("Modo Oscuro", fontWeight = FontWeight.Bold)
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { themeViewModel.toggleDarkMode(it) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {
                Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
                Box {
                    Button(onClick = {
                        Firebase.auth.signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(16.dp)) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(id = R.string.actualLanguaje), fontSize = 20.sp)
                    Text(stringResource(id = R.string.language), fontSize = 20.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
                ) {
                    Button(onClick = {
                        viewModel.changeLanguage("es")
                        viewModel.restartApp(context)
                    }) {
                        Text("Español")
                    }

                    Button(onClick = {
                        viewModel.changeLanguage("en")
                        viewModel.restartApp(context)
                    }) {
                        Text("English")
                    }

                    Button(onClick = {
                        viewModel.changeLanguage("de")
                        viewModel.restartApp(context)
                    }) {
                        Text("Deutsch")
                    }
                }
            }

        }
    }
}

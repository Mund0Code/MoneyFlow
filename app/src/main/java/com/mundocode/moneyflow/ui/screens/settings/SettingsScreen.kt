package com.mundocode.moneyflow.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.ThemeViewModel
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeViewModel: ThemeViewModel = viewModel(),
    navController: NavHostController,
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val selectedColorIndex by themeViewModel.selectedColorIndex.collectAsState()
    val colors = listOf("Azul", "Verde", "Rojo")
    var expanded by remember { mutableStateOf(false) }

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
                Text("Color de la App", fontWeight = FontWeight.Bold)
                Box {
                    Button(onClick = { expanded = true }) {
                        Text(colors[selectedColorIndex])
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        colors.forEachIndexed { index, color ->
                            DropdownMenuItem(
                                text = { Text(color) },
                                onClick = {
                                    themeViewModel.setColorScheme(index)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

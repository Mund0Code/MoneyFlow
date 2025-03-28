package com.mundocode.moneyflow

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun MyAppTheme(themeViewModel: ThemeViewModel = hiltViewModel(), content: @Composable () -> Unit) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val selectedColorIndex by themeViewModel.selectedColorIndex.collectAsState()

    val colorSchemes = listOf(
        lightColorScheme(primary = Color.Black, secondary = Color.Cyan),
        lightColorScheme(primary = Color.Green, secondary = Color.Yellow),
        lightColorScheme(primary = Color.Red, secondary = Color.Magenta)
    )

    val darkColorSchemes = listOf(
        darkColorScheme(primary = Color.White, secondary = Color.Cyan),
        darkColorScheme(primary = Color.Green, secondary = Color.Yellow),
        darkColorScheme(primary = Color.Red, secondary = Color.Magenta)
    )

    val colorScheme = if (isDarkMode) darkColorSchemes[selectedColorIndex] else colorSchemes[selectedColorIndex]

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

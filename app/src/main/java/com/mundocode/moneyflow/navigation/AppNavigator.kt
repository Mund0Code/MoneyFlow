package com.mundocode.moneyflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.mundocode.moneyflow.ThemeViewModel

@Composable
fun AppNavigator(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    NavigationGraph(
        navController,
        themeViewModel = themeViewModel
    )
}
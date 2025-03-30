package com.mundocode.moneyflow.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.mundocode.moneyflow.ThemeViewModel

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun AppNavigator(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    NavigationGraph(
        navController,
        themeViewModel = themeViewModel
    )
}
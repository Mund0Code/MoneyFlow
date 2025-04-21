package com.mundocode.moneyflow.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.mundocode.moneyflow.ThemeViewModel
import com.mundocode.moneyflow.ui.screens.SplashScreen
import com.mundocode.moneyflow.ui.screens.auth.LoginScreen
import com.mundocode.moneyflow.ui.screens.auth.RegisterScreen
import com.mundocode.moneyflow.ui.screens.calendario.CalendarScreen
import com.mundocode.moneyflow.ui.screens.clientes.ClientesScreen
import com.mundocode.moneyflow.ui.screens.facturas.CrearFacturaScreen
import com.mundocode.moneyflow.ui.screens.facturas.DetalleFacturaScreen
import com.mundocode.moneyflow.ui.screens.facturas.ListaFacturasScreen
import com.mundocode.moneyflow.ui.screens.facturas.ScanFacturaScreen
import com.mundocode.moneyflow.ui.screens.gananciasGastos.AgregarTransaccionScreen
import com.mundocode.moneyflow.ui.screens.gananciasGastos.DetalleTransaccionScreen
import com.mundocode.moneyflow.ui.screens.gananciasGastos.GananciasGastosScreen
import com.mundocode.moneyflow.ui.screens.gananciasGastos.ListaTransaccionesScreen
import com.mundocode.moneyflow.ui.screens.home.HomeScreen
import com.mundocode.moneyflow.ui.screens.onBoarding.OnboardingScreen
import com.mundocode.moneyflow.ui.screens.proyectos.ProyectosScreen
import com.mundocode.moneyflow.ui.screens.settings.SettingsScreen

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    themeViewModel: ThemeViewModel
) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController = navController) }
        composable("onboarding") { OnboardingScreen(navController) }
        composable("login") { LoginScreen(navController = navController) }
        composable("register") { RegisterScreen(navController = navController) }
        composable("home") { HomeScreen(navController = navController) }
        composable("clientes") { ClientesScreen(navController = navController) }
        composable("calendario") { CalendarScreen(themeViewModel, navController = navController) }
        composable("proyectos") { ProyectosScreen(navController = navController) }
        composable("ganancias_gastos") { GananciasGastosScreen(navController = navController) }
        composable("facturas") { ListaFacturasScreen(navController = navController) } // 🆕 Nueva pantalla
        composable("detalle_factura/{facturaId}") { backStackEntry ->
            val facturaId = backStackEntry.arguments?.getString("facturaId") ?: ""
            DetalleFacturaScreen(navController = navController, facturaId = facturaId)
        }
        composable("crear_factura") { CrearFacturaScreen(navController = navController) }
        composable("settings") { SettingsScreen(
            themeViewModel,
            navController = navController
        ) }
        composable("scanfacturascreen") { ScanFacturaScreen(navController = navController) }
        composable("transacciones") { ListaTransaccionesScreen(navController = navController) }
        composable("agregar_transaccion") { AgregarTransaccionScreen(navController = navController) }
        composable("detalle_transaccion/{id}") { backStackEntry ->
            DetalleTransaccionScreen(transaccionId = backStackEntry.arguments?.getString("id") ?: "", navController = navController)
        }
    }
}

package com.mundocode.moneyflow.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
    var showMenu by remember { mutableStateOf(false) } // Estado para mostrar el menú emergente

    val items = listOf(
        BottomNavItem("home", "Inicio", Icons.Default.Home),
        BottomNavItem("clientes", "Clientes", Icons.Default.Person),
        BottomNavItem("calendario", "Calendario", Icons.Default.DateRange),
        BottomNavItem("proyectos", "Proyectos", Icons.Default.Build),
        BottomNavItem("ganancias_gastos", "Finanzas", Icons.Default.AttachMoney)
    )

    val extraItems = listOf(
        BottomNavItem("facturas", "Facturas", Icons.Default.Receipt),
        BottomNavItem("transacciones", "Transacciones", Icons.Default.Money)
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        NavigationBar {
            items.forEach { item ->
                NavigationBarItem(
                    label = { Text(item.label) },
                    selected = currentDestination == item.route,
                    onClick = { navController.navigate(item.route) },
                    icon = {
                        Icon(item.icon, contentDescription = "Navegar a ${item.label}")
                    }
                )
            }

            // Botón de "Más" con menú desplegable
            NavigationBarItem(
                label = { Text("Más") },
                selected = false,
                onClick = { showMenu = true },
                icon = { Icon(Icons.Default.MoreHoriz, contentDescription = "Más opciones") }
            )

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false },
                offset = DpOffset(1110.dp, 350.dp)
            ) {
                extraItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.label) },
                        onClick = {
                            showMenu = false
                            navController.navigate(item.route)
                        },
                        leadingIcon = { Icon(item.icon, contentDescription = null) }
                    )
                }
            }
        }
    }
}

// ✅ Modelo para los ítems del BottomNavigation
data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

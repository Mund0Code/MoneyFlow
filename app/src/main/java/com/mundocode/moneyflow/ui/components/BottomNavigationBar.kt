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
import com.mundocode.moneyflow.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route
    var showMenu by remember { mutableStateOf(false) } // Estado para mostrar el menú emergente

    val items = listOf(
        BottomNavItem("home", stringResource(R.string.home), Icons.Default.Home),
        BottomNavItem("clientes", stringResource(R.string.clients), Icons.Default.Person),
        BottomNavItem("calendario", stringResource(R.string.calendar), Icons.Default.DateRange),
    )

    val extraItems = listOf(
        BottomNavItem("facturas", stringResource(R.string.invoices), Icons.Default.Receipt),
        BottomNavItem("transacciones", stringResource(R.string.transactions), Icons.Default.Money),
        BottomNavItem("ganancias_gastos", stringResource(R.string.finances), Icons.Default.AttachMoney),
        BottomNavItem("proyectos", stringResource(R.string.projects), Icons.Default.Build)
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
                label = { Text(stringResource(R.string.more)) },
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

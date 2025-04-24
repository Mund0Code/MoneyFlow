package com.mundocode.moneyflow.ui.screens.facturas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleFacturaScreen(
    viewModel: FacturaViewModel = hiltViewModel(),
    navController: NavHostController,
    facturaId: String
) {
    val factura by viewModel.facturaSeleccionada.collectAsState(initial = null)
    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("Pendiente", "Pagado", "Cancelado")
    var estadoSeleccionado by remember(factura?.estado) { mutableStateOf(factura?.estado ?: "") }

    LaunchedEffect(facturaId) {
        viewModel.cargarFacturaPorId(facturaId)
    }

    Scaffold(
        topBar = { CustomTopAppBar(navController, stringResource(id = R.string.invoice_details)) },
        bottomBar = { BottomNavigationBar(navController) },
    ) { paddingValues ->
        factura?.let { factura ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🧾 ${stringResource(id = R.string.invoice_nr)}° ${factura.id}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("📅 ${stringResource(id = R.string.date)}: ${factura.fecha}", fontSize = 16.sp)
                Text("👤 ${stringResource(id = R.string.client)}: ${factura.clienteNombre}", fontSize = 16.sp)
                Text("💰 ${stringResource(id = R.string.total)}: ${NumberFormat.getCurrencyInstance().format(factura.montoTotal)}", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))

                Text("📦 ${stringResource(id = R.string.products)}:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                factura.detalles.forEach { producto -> // ✅ Cambiado de productos a detalles
                    Text("- $producto")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { viewModel.enviarFacturaPorCorreo(factura, navController.context) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = "Enviar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.send_email))
                    }

                    Button(
                        onClick = { viewModel.descargarFacturaPDF(factura, navController.context) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = "Descargar PDF")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.download_pdf))
                    }
                }

                Text("📌 Estado:", fontWeight = FontWeight.Bold, fontSize = 18.sp)

                EstadoFacturaDropdown(
                    estadoActual = factura.estado,
                    onEstadoSeleccionado = { nuevoEstado ->
                        viewModel.actualizarEstadoFactura(factura.copy(estado = nuevoEstado))
                    }
                )


            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadoFacturaDropdown(
    estadoActual: String,
    onEstadoSeleccionado: (String) -> Unit
) {
    val opcionesEstado = listOf("Pendiente", "Pagado", "Cancelado")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = estadoActual,
            onValueChange = {}, // No editable manualmente
            readOnly = true,
            label = { Text("Estado") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir selector"
                )
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clickable { expanded = !expanded } // <-- aquí se asegura que se abra desde todo el campo
        )

        DropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opcionesEstado.forEach { estado ->
                DropdownMenuItem(
                    text = { Text(estado) },
                    onClick = {
                        onEstadoSeleccionado(estado)
                        expanded = false
                    }
                )
            }
        }
    }
}
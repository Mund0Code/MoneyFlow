package com.mundocode.moneyflow.ui.screens.facturas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
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

package com.mundocode.moneyflow.ui.screens.facturas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.database.entity.Factura
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaFacturasScreen(viewModel: FacturaViewModel = hiltViewModel(), navController: NavHostController) {
    val context = LocalContext.current
    val facturas by viewModel.facturas.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { CustomTopAppBar(navController, stringResource(R.string.invoices)) },
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("crear_factura") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva Factura")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "📜 ${stringResource(R.string.recorded_invoices)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (facturas.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_invoices),
                    modifier = Modifier.padding(16.dp),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(facturas.size) { factura ->
                        FacturaCard(facturas[factura], navController, onDeleteClick = { viewModel.eliminarFactura(facturas[factura]) })
                    }
                }
            }
        }
    }
}

@Composable
fun FacturaCard(factura: Factura, navController: NavHostController, onDeleteClick: () -> Unit = {}) {
    val estadoColor = when (factura.estado) {
        stringResource(R.string.paid) -> Color.Green
        stringResource(R.string.pending) -> Color(0xFFFFA500) // Naranja
        stringResource(R.string.canceled) -> Color.Red
        else -> Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("detalle_factura/${factura.id}") },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("${stringResource(R.string.client)}: ${factura.clienteNombre}", fontWeight = FontWeight.Bold)
                Text("${stringResource(R.string.date)}: ${factura.fecha}", fontSize = 14.sp, color = Color.Gray)
                Text(
                    "${stringResource(R.string.total)}: ${NumberFormat.getCurrencyInstance().format(factura.montoTotal)}",
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier.padding(start = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(estadoColor)
                ) // 🔹 Indicador de estado

                IconButton(onClick = { onDeleteClick() }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar Factura",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

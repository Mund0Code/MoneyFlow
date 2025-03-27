package com.mundocode.moneyflow.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mundocode.moneyflow.database.Transaccion
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import java.text.NumberFormat
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navController: NavHostController) {
    val totalIngresos by viewModel.totalIngresos.collectAsState(initial = 0.0)
    val totalGastos by viewModel.totalGastos.collectAsState(initial = 0.0)
    val flujoDeCaja by viewModel.flujoDeCaja.collectAsState(initial = 0.0)
    val prediccionIngresos by viewModel.prediccionIngresos.collectAsState(initial = 0.0)
    val prediccionGastos by viewModel.prediccionGastos.collectAsState(initial = 0.0)

    val transaccionesFiltradas by viewModel.transaccionesFiltradas.collectAsState(initial = emptyList())

    Log.d("HomeScreen", "transaccionesFiltradas: $transaccionesFiltradas")

    Scaffold(
        topBar = { CustomTopAppBar(navController, "Dashboard Financiero") },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CardResumenFinanciero(totalIngresos, totalGastos, flujoDeCaja)
                CardPrediccionFinanzas(prediccionIngresos, prediccionGastos)
                FiltroPorCategoria { categoria ->
                    Log.d("HomeScreen", "Filtrando por categoría: $categoria") // 🔍 Depuración
                    viewModel.filtrarTransacciones(categoria)
                }

                ListaTransacciones(transaccionesFiltradas) // ✅ Mostrar las filtradas
            }
        }
    }
}


@Composable
fun CardResumenFinanciero(ingresos: Double, gastos: Double, flujoCaja: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📊 Resumen Financiero", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ResumenItem("💰", "Ingresos", ingresos, Color(0xFF2E7D32))
                ResumenItem("📉", "Gastos", gastos, Color(0xFF8B0000))
                ResumenItem("🔹", "Flujo de Caja", flujoCaja, Color(0xFF00BFFF))
            }
        }
    }
}

@Composable
fun ResumenItem(icon: String, label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 24.sp)
        Text(label, fontWeight = FontWeight.Bold)
        Text(
            NumberFormat.getCurrencyInstance().format(amount),
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CardPrediccionFinanzas(predIngresos: Double, predGastos: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📈 Predicciones Financieras", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("🔹 Ingresos Estimados: ${NumberFormat.getCurrencyInstance().format(predIngresos)}")
            Text("🔻 Gastos Estimados: ${NumberFormat.getCurrencyInstance().format(predGastos)}")
        }
    }
}

@Composable
fun FiltroPorCategoria(
    viewModel: HomeViewModel = hiltViewModel(),
    onCategoriaSeleccionada: (String) -> Unit,
) {
    val categorias by viewModel.categoriasDisponibles.collectAsState(initial = listOf("Todas"))
    var categoriaSeleccionada by remember { mutableStateOf("Todas") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        categorias.forEach { categoria ->
            Button(
                onClick = {
                    categoriaSeleccionada = categoria
                    onCategoriaSeleccionada(categoria)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (categoriaSeleccionada == categoria) Color.Gray else MaterialTheme.colorScheme.primary
                )
            ) {
                Text(categoria)
            }
        }
    }
}


@Composable
fun ListaTransacciones(transacciones: List<Transaccion>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("📋 Últimas Transacciones", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        if (transacciones.isEmpty()) {
            Text("No hay transacciones en esta categoría.", modifier = Modifier.padding(16.dp))
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 🔹 Dos filas
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // 🔹 Evitar restricciones infinitas
            ) {
                items(transacciones.size) { transaccion -> // ✅ Se usa items(), no forEach
                    val transaccion = transacciones[transaccion]
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.9f), // 🔹 Tamaño relativo
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) { // ✅ Column para organizar
                            Text("Tipo: ${transaccion.tipo}", fontWeight = FontWeight.Bold)
                            Text("Monto: ${NumberFormat.getCurrencyInstance().format(transaccion.monto)}")
                            Text("Fecha: ${transaccion.fecha}")
                        }
                    }
                }
            }
        }
    }
}

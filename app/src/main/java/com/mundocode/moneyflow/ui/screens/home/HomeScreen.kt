package com.mundocode.moneyflow.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.core.TipoTransaccion
import com.mundocode.moneyflow.database.entity.Transaccion
import java.text.NumberFormat
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navController: NavHostController) {
    val totalIngresos by viewModel.totalIngresos.collectAsState(initial = 0.0)
    val totalGastos by viewModel.totalGastos.collectAsState(initial = 0.0)
    val flujoDeCaja by viewModel.flujoDeCaja.collectAsState(initial = 0.0)
    val prediccionIngresos by viewModel.prediccionIngresos.collectAsState(initial = 0.0)
    val prediccionGastos by viewModel.prediccionGastos.collectAsState(initial = 0.0)
    val context = LocalContext.current

    val transaccionesFiltradas by viewModel.transaccionesFiltradas.collectAsState(initial = emptyList())

    Timber.tag("HomeScreen").d("transaccionesFiltradas: $transaccionesFiltradas")

    Scaffold(
        topBar = { CustomTopAppBar(navController, context.getString(R.string.financial_dashboard)) },
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
                    Timber.tag("HomeScreen").d("Filtrando por categoría: $categoria") // 🔍 Depuración
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
            Text("📊 ${stringResource(R.string.financial_summary)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ResumenItem("💰", stringResource(R.string.incomes), ingresos, Color(0xFF00FF0D))
                ResumenItem("📉", stringResource(R.string.expenses), gastos, Color(0xFFFF0000))
                ResumenItem("🔹", stringResource(R.string.cash_flow), flujoCaja, Color(0xFF00BFFF))
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
            Text("📈 ${stringResource(R.string.financial_forecasts)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("🔹 ${stringResource(R.string.estimated_revenues)}: ${NumberFormat.getCurrencyInstance().format(predIngresos)}")
            Text("🔻 ${stringResource(R.string.estimed_expenses)}: ${NumberFormat.getCurrencyInstance().format(predGastos)}")
        }
    }
}

@Composable
fun FiltroPorCategoria(
    viewModel: HomeViewModel = hiltViewModel(),
    onCategoriaSeleccionada: (String) -> Unit,
) {
    val context = LocalContext.current
    val categorias by viewModel.categoriasDisponibles.collectAsState(initial = listOf(stringResource(R.string.all)))
    var categoriaSeleccionada by remember { mutableStateOf(context.getString(R.string.all)) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {

        items(categorias) { categoria ->
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
        Text("📋 ${stringResource(R.string.latest_transactions)}", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        if (transacciones.isEmpty()) {
            Text(stringResource(R.string.no_transactions), modifier = Modifier.padding(16.dp))
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
                            val tipoTraducido = when (transaccion.tipo) {
                                TipoTransaccion.INGRESO -> stringResource(R.string.income)
                                TipoTransaccion.GASTO -> stringResource(R.string.expense)
                                else -> transaccion.tipo
                            }
                            Text("${stringResource(R.string.type)}: $tipoTraducido", fontWeight = FontWeight.Bold)
                            Text("${stringResource(R.string.amount)}: ${NumberFormat.getCurrencyInstance().format(transaccion.monto)}")
                            Text("${stringResource(R.string.date)}: ${transaccion.fecha}")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun CardResumenFinancieroPreview() {
    CardResumenFinanciero(
        ingresos = 1000.0,
        gastos = 500.0,
        flujoCaja = 500.0
    )
}

@Preview(showBackground = true)
@Composable
fun ListaTransaccionesPreview() {
    ListaTransacciones(
        transacciones = listOf()
    )
}


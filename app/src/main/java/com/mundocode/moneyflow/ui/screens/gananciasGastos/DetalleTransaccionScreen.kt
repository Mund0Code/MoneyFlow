package com.mundocode.moneyflow.ui.screens.gananciasGastos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun DetalleTransaccionScreen(
    transaccionId: String,
    viewModel: TransaccionViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val transacciones by viewModel.transacciones.collectAsState(initial = emptyList())
    val transaccion = transacciones.find { it.id == transaccionId }

    Scaffold(
        topBar = { CustomTopAppBar(navController, stringResource(R.string.transaction_detail)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        transaccion?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("📝 ${stringResource(R.string.transaction_detail)}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("📅 ${stringResource(R.string.date)}: ${it.fecha}")
                Text("💰 ${stringResource(R.string.amount)}: ${NumberFormat.getCurrencyInstance().format(it.monto)}")
                Text("📂 ${stringResource(R.string.type)}: ${it.tipo}")
                it.categoria?.let { categoria ->
                    Text("📌 ${stringResource(R.string.category)}: $categoria")
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.eliminarTransaccion(it); navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.delete_transaction))
                }
            }
        } ?: run {
            Text(stringResource(id = R.string.not_found), modifier = Modifier.padding(16.dp), fontSize = 18.sp)
        }
    }
}

package com.mundocode.moneyflow.ui.screens.facturas

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.ui.components.CustomTopAppBar
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearFacturaScreen(viewModel: FacturaViewModel = viewModel(), navController: NavHostController) {
    var clienteNombre by remember { mutableStateOf("") }
    var montoTotal by remember { mutableStateOf("") }
    var detalles by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = { CustomTopAppBar(navController, "Nueva Factura") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("📝 Crear Factura", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            OutlinedTextField(
                value = clienteNombre,
                onValueChange = { clienteNombre = it },
                label = { Text("Nombre del Cliente") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = montoTotal,
                onValueChange = { montoTotal = it },
                label = { Text("Monto Total") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = detalles,
                onValueChange = { detalles = it },
                label = { Text("Detalles de la Factura") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isSaving = true
                    viewModel.guardarFactura(
                        clienteId = UUID.randomUUID().toString(),
                        clienteNombre = clienteNombre,
                        monto = montoTotal.toDoubleOrNull() ?: 0.0,
                        detalles = detalles.split("\n")
                    )
                    isSaving = false
                    Toast.makeText(context, "Factura guardada", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = clienteNombre.isNotBlank() && montoTotal.isNotBlank() && detalles.isNotBlank()
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar Factura")
                }
            }
        }
    }
}

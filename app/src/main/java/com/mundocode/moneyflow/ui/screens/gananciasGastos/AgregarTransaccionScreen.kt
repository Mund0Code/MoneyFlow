package com.mundocode.moneyflow.ui.screens.gananciasGastos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarTransaccionScreen(viewModel: TransaccionViewModel = hiltViewModel(), navController: NavHostController) {
    val context = LocalContext.current
    var tipo by remember { mutableStateOf(context.getString(R.string.income)) }
    var monto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = { CustomTopAppBar(navController, stringResource(R.string.add_transaction)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("📌 ${stringResource(R.string.add_new_transaction)}", fontWeight = FontWeight.Bold, fontSize = 20.sp)

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                var expanded by remember { mutableStateOf(false) }
                Column {
                    OutlinedTextField(
                        value = tipo,
                        onValueChange = {},
                        label = { Text(stringResource(R.string.type)) },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { expanded = true }
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.income)) }, onClick = {
                            tipo = context.getString(R.string.income)
                            expanded = false
                        })
                        DropdownMenuItem(text = { Text(stringResource(R.string.expense)) }, onClick = {
                            tipo = context.getString(R.string.expense)
                            expanded = false
                        })
                    }
                }
            }

            OutlinedTextField(
                value = monto,
                onValueChange = {
                    if (it.matches(Regex("\\d*\\.?\\d*"))) {
                        monto = it
                        errorMessage = ""
                    } else {
                        errorMessage = context.getString(R.string.valid_mount)
                    }
                },
                label = { Text(stringResource(R.string.amount)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.isNotEmpty()
            )

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text(stringResource(R.string.category)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red, modifier = Modifier.padding(4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (monto.isNotEmpty() && monto.toDoubleOrNull() != null) {
                        viewModel.agregarTransaccion(tipo, context,monto.toDouble(), categoria.trim())
                        navController.popBackStack()
                    } else {
                        errorMessage = context.getString(R.string.valid_mount)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = "Guardar")
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.save_transaction))
            }
        }
    }
}

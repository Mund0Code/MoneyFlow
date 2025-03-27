package com.mundocode.moneyflow.ui.screens.clientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ClientesScreen(viewModel: ClienteViewModel = hiltViewModel(), navController: NavHostController) {
    val clientes by viewModel.clientes.collectAsState(initial = emptyList())
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = { CustomTopAppBar(navController, "Gestión de Clientes") },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Cliente")
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Column(modifier = Modifier.padding(it)) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar Cliente") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
            LazyColumn {
                val clientesFiltrados = clientes.filter { it.nombre.contains(searchQuery.text, ignoreCase = true) }
                items(clientesFiltrados.size) { cliente ->
                    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                        val cliente = clientesFiltrados[cliente]
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre: ${cliente.nombre}")
                            Text("Teléfono: ${cliente.telefono}")
                            Text("Correo: ${cliente.correo}")
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(onClick = { viewModel.eliminarCliente(cliente) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Cliente", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            confirmButton = {
                Button(onClick = {
                    if (nombre.isNotEmpty() && telefono.isNotEmpty() && correo.isNotEmpty()) {
                        viewModel.agregarCliente(nombre, telefono, correo)
                        mostrarDialogo = false
                        nombre = ""
                        telefono = ""
                        correo = ""
                    }
                }) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            },
            text = {
                Column {
                    TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre Completo") })
                    TextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                    TextField(value = correo, onValueChange = { correo = it }, label = { Text("Correo Electrónico") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                }
            }
        )
    }
}

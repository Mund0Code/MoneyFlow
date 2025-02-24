package com.mundocode.moneyflow.ui.screens.proyectos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProyectosScreen(viewModel: ProyectoViewModel = viewModel(), navController: NavHostController) {
    val proyectos by viewModel.proyectos.collectAsState(initial = emptyList())
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fechaInicio by remember { mutableStateOf("") }
    var fechaFin by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("En progreso") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { CustomTopAppBar(navController, "Gestión de Proyectos") },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Proyecto")
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Column(modifier = Modifier.padding(it)) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar Proyecto") },
                modifier = Modifier.fillMaxWidth().padding(8.dp)
            )
            LazyColumn {
                val proyectosFiltrados = proyectos.filter { it.nombre.contains(searchQuery.text, ignoreCase = true) }
                items(proyectosFiltrados.size) { proyecto ->
                    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                        val proyecto = proyectos[proyecto]
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre: ${proyecto.nombre}")
                            Text("Descripción: ${proyecto.descripcion}")
                            Text("Fecha Inicio: ${proyecto.fechaInicio}")
                            Text("Fecha Fin: ${proyecto.fechaFin}")
                            Text("Estado: ${proyecto.estado}")
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(onClick = { viewModel.eliminarProyecto(proyecto) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Proyecto", tint = MaterialTheme.colorScheme.error)
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
                    if (nombre.isNotEmpty() && descripcion.isNotEmpty() && fechaInicio.isNotEmpty() && fechaFin.isNotEmpty()) {
                        viewModel.agregarProyecto(nombre, descripcion, fechaInicio, fechaFin, estado)
                        mostrarDialogo = false
                        nombre = ""
                        descripcion = ""
                        fechaInicio = ""
                        fechaFin = ""
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
                    TextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre del Proyecto") })
                    TextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") })
                    TextField(value = fechaInicio, onValueChange = { fechaInicio = it }, label = { Text("Fecha de Inicio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    TextField(value = fechaFin, onValueChange = { fechaFin = it }, label = { Text("Fecha de Fin") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Box {
                        Button(onClick = { isDropdownExpanded = true }) {
                            Text(estado)
                        }
                        DropdownMenu(expanded = isDropdownExpanded, onDismissRequest = { isDropdownExpanded = false }) {
                            listOf("En progreso", "Completado", "En espera").forEach { estadoProyecto ->
                                DropdownMenuItem(
                                    onClick = {
                                        estado = estadoProyecto
                                        isDropdownExpanded = false
                                    },
                                    text = { Text(estadoProyecto) }
                                )
                            }
                        }
                    }
                }
            }
        )
    }
}

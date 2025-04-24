package com.mundocode.moneyflow.ui.screens.calendario

import android.widget.CalendarView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.ThemeViewModel
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    viewModel: EventoViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val eventos by viewModel.eventos.collectAsState(initial = emptyList())
    val eventoSeleccionado by viewModel.eventoSeleccionado.collectAsState()
    val selectedDate = remember { mutableStateOf("") }
    var tituloEvento by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    val categorias = listOf(
        stringResource(id = R.string.job),
        stringResource(id = R.string.personal),
        stringResource(id = R.string.meet),
        stringResource(id = R.string.other)
    )
    var selectedCategory by remember { mutableStateOf(categorias[0]) }

    Scaffold(
        topBar = { CustomTopAppBar(navController, stringResource(id = R.string.calendar_title)) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.limpiarEventoSeleccionado()
                tituloEvento = ""
                selectedCategory = categorias[0]
                mostrarDialogo = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Evento")
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Column(modifier = Modifier.padding(it)) {

            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { context ->
                    CalendarView(context).apply {
                        setOnDateChangeListener { _, year, month, dayOfMonth ->
                            selectedDate.value = "$dayOfMonth/${month + 1}/$year"
                        }

                        dateTextAppearance =
                            if (isDarkMode) android.R.style.TextAppearance_DeviceDefault_Large_Inverse
                            else android.R.style.TextAppearance_DeviceDefault_Large

                        weekDayTextAppearance =
                            if (isDarkMode) android.R.style.TextAppearance_DeviceDefault_Medium_Inverse
                            else android.R.style.TextAppearance_DeviceDefault_Medium
                    }
                }
            )

            LazyColumn {
                items(eventos) { evento ->
                    Card(modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("${stringResource(id = R.string.event)}: ${evento.titulo}")
                                Text("${stringResource(id = R.string.date)}: ${evento.fecha}")
                                Text("${stringResource(id = R.string.category)}: ${evento.categoria}")
                            }
                            Row {
                                IconButton(onClick = {
                                    viewModel.seleccionarEvento(evento)
                                    tituloEvento = evento.titulo
                                    selectedDate.value = evento.fecha
                                    selectedCategory = evento.categoria
                                    mostrarDialogo = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar Evento")
                                }

                                IconButton(onClick = { viewModel.eliminarEvento(evento) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar Evento")
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
            onDismissRequest = {
                mostrarDialogo = false
                viewModel.limpiarEventoSeleccionado()
            },
            confirmButton = {
                Button(onClick = {
                    if (tituloEvento.isNotEmpty() && selectedDate.value.isNotEmpty()) {
                        if (eventoSeleccionado != null) {
                            viewModel.editarEvento(tituloEvento, selectedDate.value, selectedCategory)
                        } else {
                            viewModel.agregarEvento(tituloEvento, selectedDate.value, selectedCategory)
                        }
                        mostrarDialogo = false
                        tituloEvento = ""
                    }
                }) {
                    Text(stringResource(id = R.string.save))
                }
            },
            dismissButton = {
                Button(onClick = {
                    mostrarDialogo = false
                    viewModel.limpiarEventoSeleccionado()
                }) {
                    Text(stringResource(id = R.string.cancel))
                }
            },
            text = {
                Column {
                    TextField(
                        value = tituloEvento,
                        onValueChange = { tituloEvento = it },
                        label = { Text(stringResource(id = R.string.event_title)) }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("${stringResource(id = R.string.date_selected)}: ${selectedDate.value}")

                    Spacer(Modifier.height(8.dp))
                    DropdownMenuCategoria(
                        categorias = categorias,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuCategoria(
    categorias: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCategory,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.category)) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor() // ¡Importante! Necesario para que se posicione bien
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categorias.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(categoria) },
                    onClick = {
                        onCategorySelected(categoria)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDropdownMenuCategoria() {
    val categorias = listOf("Trabajo", "Personal", "Reunión", "Otro")
    var selectedCategory by remember { mutableStateOf(categorias[0]) }

    MaterialTheme {
        Surface {
            DropdownMenuCategoria(
                categorias = categorias,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEventoDialog() {
    var tituloEvento by remember { mutableStateOf("Reunión con cliente") }
    val selectedDate = remember { mutableStateOf("20/04/2025") }
    val categorias = listOf("Trabajo", "Personal", "Reunión", "Otro")
    var selectedCategory by remember { mutableStateOf(categorias[2]) }

    MaterialTheme {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {
                Button(onClick = {}) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = {}) {
                    Text("Cancelar")
                }
            },
            text = {
                Column {
                    TextField(
                        value = tituloEvento,
                        onValueChange = { tituloEvento = it },
                        label = { Text("Título del evento") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fecha seleccionada: ${selectedDate.value}")
                    Spacer(modifier = Modifier.height(8.dp))
                    DropdownMenuCategoria(
                        categorias = categorias,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it }
                    )
                }
            }
        )
    }
}


// Nombre de la app
// MoneyFlow
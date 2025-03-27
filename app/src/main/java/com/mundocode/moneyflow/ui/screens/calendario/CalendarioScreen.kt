package com.mundocode.moneyflow.ui.screens.calendario


import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.ThemeViewModel
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    themeViewModel: ThemeViewModel = hiltViewModel(),
    viewModel: EventoViewModel = hiltViewModel(),
    navController: NavHostController
) {
    val eventos by viewModel.eventos.collectAsState(initial = emptyList())
    val selectedDate = remember { mutableStateOf("") }
    var tituloEvento by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val categorias = listOf("Trabajo", "Personal", "Reunión", "Otro")
    var selectedCategory by remember { mutableStateOf(categorias[0]) }

        Scaffold(
            topBar = { CustomTopAppBar(navController, "Calendario de Eventos") },
            floatingActionButton = {
                FloatingActionButton(onClick = { mostrarDialogo = true }) {
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

                            // Cambia el color de los números del calendario
                            dateTextAppearance = if (isDarkMode) android.R.style.TextAppearance_DeviceDefault_Large_Inverse
                            else android.R.style.TextAppearance_DeviceDefault_Large

                            // Cambia el color de los días de la semana (Lunes, Martes, etc.)
                            weekDayTextAppearance = if (isDarkMode) android.R.style.TextAppearance_DeviceDefault_Medium_Inverse
                            else android.R.style.TextAppearance_DeviceDefault_Medium

                            // 🚀 Modificar el color del mes y año dinámicamente 🚀
                            post {
                                try {
                                    val rootView = getChildAt(0) as? ViewGroup
                                    rootView?.let { vg ->
                                        for (i in 0 until vg.childCount) {
                                            val view = vg.getChildAt(i)
                                            if (view is TextView) {
                                                view.setTextColor(
                                                    if (isDarkMode) Color.White.toArgb() else Color.Black.toArgb()
                                                )
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                )

                LazyColumn {
                    items(eventos.size) { evento ->
                        Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                            val evento = eventos[evento]
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Evento: ${evento.titulo}",
                                    color = if (isDarkMode) Color.White else Color.Black
                                )
                                Text(
                                    text = "Fecha: ${evento.fecha}",
                                    color = if (isDarkMode) Color.White else Color.Black
                                )
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
                        if (tituloEvento.isNotEmpty() && selectedDate.value.isNotEmpty()) {
                            viewModel.agregarEvento(
                                tituloEvento,
                                selectedDate.value,
                                selectedCategory
                            )
                            mostrarDialogo = false
                            tituloEvento = ""
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
                        TextField(
                            value = tituloEvento,
                            onValueChange = { tituloEvento = it },
                            label = { Text("Título del Evento") })
                        Text("Fecha seleccionada: ${selectedDate.value}")
                    }
                }
            )
        }
    }

// Nombre de la app
// MoneyFlow
package com.mundocode.moneyflow.ui.screens.calendario

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.database.entity.Evento
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: EventoViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    val eventos by viewModel.eventos.collectAsState(initial = emptyList())
    val eventoSeleccionado by viewModel.eventoSeleccionado.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var currentMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var currentYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance().time) }
    val categorias = listOf("Trabajo", "Personal", "Reunión", "Otro")
    var mostrarDialogo by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { CustomTopAppBar(navController, context.getString(R.string.calendar_title)) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.limpiarEventoSeleccionado()
                mostrarDialogo = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Evento")
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CalendarHeader(
                currentMonth = currentMonth,
                currentYear = currentYear,
                onPrevMonth = {
                    if (currentMonth == 0) {
                        currentMonth = 11
                        currentYear--
                    } else {
                        currentMonth--
                    }
                },
                onNextMonth = {
                    if (currentMonth == 11) {
                        currentMonth = 0
                        currentYear++
                    } else {
                        currentMonth++
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            CalendarDaysOfWeek()

            Spacer(modifier = Modifier.height(8.dp))

            CalendarGrid(
                currentMonth = currentMonth,
                currentYear = currentYear,
                selectedDate = selectedDate,
                eventos = eventos,
                onDateSelected = { selectedDate = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.events_of_day, SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(selectedDate)),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            val eventosDelDia = eventos.filter {
                it.fecha == SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(selectedDate)
            }

            if (eventosDelDia.isEmpty()) {
                Text(
                    text = context.getString(R.string.no_events),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                eventosDelDia.forEach { evento ->
                    EventCard(
                        evento = evento,
                        onClickDelete = { viewModel.eliminarEvento(evento) },
                        onClickEdit = { eventoSeleccionadoNuevo ->
                           viewModel.seleccionarEvento(evento) // 🎯 Cuando presionas "editar", guardas el evento aquí
                            mostrarDialogo = true
                        }
                    )
                }
            }
        }
    }

    if (mostrarDialogo) {
        EventDialog(
            initialDate = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(selectedDate),
            categoriasDisponibles = categorias,
            eventoExistente = eventoSeleccionado,
            onDismiss = {
                mostrarDialogo = false
                viewModel.limpiarEventoSeleccionado() // 🧹 Limpiamos el evento seleccionado siempre
            },
            onConfirm = { titulo, fecha, categoria ->
                if (eventoSeleccionado != null) {
                    // 🚀 Editar evento existente
                    viewModel.editarEvento(
                        eventoSeleccionado!!,
                        titulo,
                        fecha,
                        categoria,
                        context
                    )
                } else {
                    // ✨ Crear nuevo evento
                    viewModel.agregarEvento(
                        titulo,
                        fecha,
                        categoria,
                        context
                    )
                }
                mostrarDialogo = false
                viewModel.limpiarEventoSeleccionado() // 🧹 Limpiamos el evento seleccionado siempre
            }
        )
    }
}


@Composable
private fun CalendarHeader(
    currentMonth: Int,
    currentYear: Int,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.MONTH, currentMonth)
        set(Calendar.YEAR, currentYear)
    }
    val monthYear = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
        .replaceFirstChar { it.uppercaseChar() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Mes Anterior")
        }
        Text(
            text = monthYear,
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Mes Siguiente")
        }
    }
}

@Composable
private fun CalendarDaysOfWeek() {
    val diasSemana = listOf("D", "L", "M", "M", "J", "V", "S")
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        userScrollEnabled = false,
        modifier = Modifier.fillMaxWidth().height(48.dp)
    ) {
        items(diasSemana) { dia ->
            Text(
                text = dia,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    currentMonth: Int,
    currentYear: Int,
    selectedDate: Date,
    eventos: List<Evento>,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.MONTH, currentMonth)
        set(Calendar.YEAR, currentYear)
        set(Calendar.DAY_OF_MONTH, 1)
    }

    val today = Calendar.getInstance().time
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        userScrollEnabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .height(300.dp)
    ) {
        repeat(firstDayOfWeek) {
            item {
                Box(modifier = Modifier.size(48.dp))
            }
        }

        items(daysInMonth) { index ->
            val day = index + 1
            val date = Calendar.getInstance().apply {
                set(Calendar.YEAR, currentYear)
                set(Calendar.MONTH, currentMonth)
                set(Calendar.DAY_OF_MONTH, day)
            }.time

            val isToday = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(today) ==
                    SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(date)

            val isSelected = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(selectedDate) ==
                    SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(date)

            val hasEvent = eventos.any {
                it.fecha == SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(date)
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            isSelected -> MaterialTheme.colorScheme.primary
                            isToday -> Color.Cyan
                            else -> Color.Transparent
                        }
                    )
                    .clickable { onDateSelected(date) },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = day.toString(),
                        color = if (isSelected || isToday) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (hasEvent) {
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.Green)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCard(
    evento: Evento,
    onClickDelete: () -> Unit,
    onClickEdit: (Evento) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "🎯 ${evento.titulo}", style = MaterialTheme.typography.titleSmall)
                Text(text = "📅 ${evento.fecha}", style = MaterialTheme.typography.bodySmall)
                Text(text = "🏷️ ${evento.categoria}", style = MaterialTheme.typography.bodySmall)
            }
            Row {
                IconButton(onClick = { onClickEdit(evento) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { onClickDelete() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDialog(
    initialDate: String,
    categoriasDisponibles: List<String>,
    eventoExistente: Evento? = null,
    onDismiss: () -> Unit,
    onConfirm: (titulo: String, fecha: String, categoria: String) -> Unit
) {
    var titulo by remember(eventoExistente) { mutableStateOf(eventoExistente?.titulo ?: "") }
    var selectedCategoria by remember(eventoExistente) { mutableStateOf(eventoExistente?.categoria ?: categoriasDisponibles.first()) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (titulo.isNotBlank()) {
                        onConfirm(titulo, initialDate, selectedCategoria)
                    }
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                TextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text(stringResource(R.string.event_title)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text("${stringResource(R.string.date_selected)}: $initialDate")
                Spacer(Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.category)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categoriasDisponibles.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    selectedCategoria = categoria
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
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
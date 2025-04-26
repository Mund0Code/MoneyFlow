package com.mundocode.moneyflow.ui.screens.calendario

import androidx.compose.animation.animateContentSize
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.mundocode.moneyflow.ThemeViewModel
import com.mundocode.moneyflow.database.entity.Evento
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.R
import kotlinx.coroutines.launch
import java.util.*

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
                            viewModel.seleccionarEvento(eventoSeleccionadoNuevo)
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
                viewModel.limpiarEventoSeleccionado()
            },
            onConfirm = { titulo, fecha, categoria ->
                if (eventoSeleccionado != null) {
                    viewModel.editarEvento(titulo, fecha, categoria)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("✅ Evento actualizado")
                    }
                } else {
                    viewModel.agregarEvento(titulo, fecha, categoria)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("✅ Evento creado")
                    }
                }
                mostrarDialogo = false
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
package com.mundocode.moneyflow.ui.screens.gananciasGastos

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.database.entity.Transaccion
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GananciasGastosScreen(viewModel: TransaccionViewModel = hiltViewModel(), navController: NavHostController) {
    val transacciones by viewModel.transacciones.collectAsState(initial = emptyList())
    var mostrarDialogo by remember { mutableStateOf(false) }
    val context = LocalContext.current // Obtener el contexto para mostrar Toast

    Scaffold(
        topBar = { CustomTopAppBar(navController, context.getString(R.string.profit_expense)) },
        floatingActionButton = {
            FloatingActionButton(onClick = { mostrarDialogo = true }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Transacción")
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

                Button(
                    onClick = {
                        navController.navigate("scanfacturascreen")
                    }
                ) {
                    Text(stringResource(R.string.scan_invoice))
                }

            LazyColumn(
                modifier = Modifier
                    .weight(1f) // Hace que la lista ocupe todo el espacio restante
                    .fillMaxWidth()
            ) {
                items(transacciones.size) { index ->
                    val transaccion = transacciones[index]
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("${stringResource(R.string.type)}: ${transaccion.tipo}")
                                Text("${stringResource(R.string.amount)}: \$${transaccion.monto}")
                            }
                            IconButton(
                                onClick = { viewModel.eliminarTransaccion(transaccion) }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Eliminar Transacción"
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        exportarPDF(transacciones, context)
                    }
                }) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = "Exportar PDF")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.pdf_export))
                }

                Button(onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        exportarExcel(transacciones, context)
                    }
                }) {
                    Icon(Icons.Default.TableChart, contentDescription = "Exportar Excel")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.excel_export))
                }
            }

            Spacer(modifier = Modifier.height(16.dp)) // Espaciado final
        }
    }

    AddTransactionDialog(
        showDialog = mostrarDialogo,
        onDismiss = { mostrarDialogo = false },
        onConfirm = { tipo, monto ->
            viewModel.agregarTransaccion(tipo, monto, SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
            mostrarDialogo = false
        }
    )
}


fun exportarPDF(transacciones: List<Transaccion>, context: Context) {
    val pdfFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Financial-report.pdf")

    try {
        val writer = PdfWriter(pdfFile)
        val pdfDocument = PdfDocument(writer)
        val document = Document(pdfDocument)

        document.add(Paragraph(context.getString(R.string.financial_report)).setBold().setFontSize(18f))
        document.add(Paragraph("${context.getString(R.string.date)}: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"))

        val table = Table(floatArrayOf(1f, 2f)).useAllAvailableWidth()
        table.addHeaderCell(Cell().add(Paragraph(context.getString(R.string.type))))
        table.addHeaderCell(Cell().add(Paragraph(context.getString(R.string.amount))))

        transacciones.forEach { transaccion ->
            table.addCell(Cell().add(Paragraph(transaccion.tipo)))
            table.addCell(Cell().add(Paragraph("\$${transaccion.monto}")))
        }

        document.add(table)
        document.close()

        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "${context.getString(R.string.pdf_generated)} ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, context.getString(R.string.error_pdf), Toast.LENGTH_LONG).show()
    }
}

fun exportarExcel(transacciones: List<Transaccion>, context: Context) {
    val excelFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Reporte_Financiero.xlsx")

    try {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet(context.getString(R.string.transactions))

        // Crear encabezados
        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue(context.getString(R.string.type))
        headerRow.createCell(1).setCellValue(context.getString(R.string.amount))

        // Insertar datos
        transacciones.forEachIndexed { index, transaccion ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(transaccion.tipo)
            row.createCell(1).setCellValue(transaccion.monto)
        }

        val fos = FileOutputStream(excelFile)
        workbook.write(fos)
        fos.close()
        workbook.close()

        // Mostrar Toast en el hilo principal
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(context, "${context.getString(R.string.excel_generated)} ${excelFile.absolutePath}", Toast.LENGTH_LONG).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, context.getString(R.string.error_excel), Toast.LENGTH_LONG).show()
    }
}


@Composable
fun AddTransactionDialog(showDialog: Boolean, onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    if (showDialog) {
        val keyboardController = LocalSoftwareKeyboardController.current
        Dialog(onDismissRequest = onDismiss) {
            Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val context = LocalContext.current
                    var tipo by remember { mutableStateOf(context.getString(R.string.income)) }
                    var monto by remember { mutableStateOf("") }
                    var expanded by remember { mutableStateOf(false) }
                    var isLoading by remember { mutableStateOf(false) }
                    var triggerSave by remember { mutableStateOf(false) }
                    var errorMessage by remember { mutableStateOf("") }

                    LaunchedEffect(triggerSave) {
                        if (triggerSave) {
                            delay(1000)
                            keyboardController?.hide()
                            onConfirm(tipo, monto.toDouble())
                            isLoading = false
                            onDismiss()
                            triggerSave = false
                        }
                    }

                    Text(stringResource(R.string.add_transaction), fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column {
                            OutlinedTextField(
                                value = tipo,
                                onValueChange = {},
                                label = { Text(stringResource(R.string.type)) },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().clickable { expanded = true }
                            )
                            AnimatedVisibility(visible = expanded, enter = fadeIn(), exit = fadeOut()) {
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
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = Color.Red, modifier = Modifier.padding(4.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
                        Button(
                            onClick = {
                                if (monto.isNotEmpty() && monto.toDoubleOrNull() != null) {
                                    isLoading = true
                                    triggerSave = true
                                } else {
                                    errorMessage = context.getString( R.string.valid_mount)
                                }
                            },
                            enabled = monto.isNotEmpty() && monto.toDoubleOrNull() != null
                        ) {
                            AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            }
                            if (!isLoading) {
                                Text(stringResource(id = R.string.save))
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.mundocode.moneyflow.ui.screens.facturas

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.database.AppDatabase
import com.mundocode.moneyflow.database.Factura
import com.mundocode.moneyflow.database.FacturaDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@HiltViewModel
class FacturaViewModel @Inject constructor(
    private val facturaDao: FacturaDao
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    val facturas: StateFlow<List<Factura>> = facturaDao.getAllFacturas()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _facturaSeleccionada = MutableStateFlow<Factura?>(null)
    val facturaSeleccionada: StateFlow<Factura?> = _facturaSeleccionada.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        cargarFacturas()
    }

    fun cargarFacturas() {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val facturasRoom = facturaDao.getAllFacturas().firstOrNull() ?: emptyList()
                _facturaSeleccionada.emit(facturasRoom.firstOrNull())

                db.collection("facturas").get()
                    .addOnSuccessListener { result ->
                        val facturasFirestore = result.toObjects(Factura::class.java)
                        viewModelScope.launch(Dispatchers.IO) {
                            facturaDao.insertarTodas(facturasFirestore)
                        }
                    }
                    .addOnFailureListener {
                        Log.e("FacturaViewModel", "Error al obtener facturas", it)
                    }
                    .addOnCompleteListener {
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                Log.e("FacturaViewModel", "Error cargando facturas", e)
                _isLoading.value = false
            }
        }
    }

    fun guardarFactura(clienteId: String, clienteNombre: String, monto: Double, detalles: List<String>) {
        val id = UUID.randomUUID().toString()
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        val factura = Factura(
            id = id,
            clienteId = clienteId,
            clienteNombre = clienteNombre,
            fecha = fecha,
            montoTotal = monto,
            detalles = detalles
        )

        viewModelScope.launch(Dispatchers.IO) {
            facturaDao.insertFactura(factura)
            db.collection("facturas").document(id).set(factura)
        }
    }

    fun cargarFacturaPorId(facturaId: String) {
        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val facturaRoom = facturaDao.getFacturaById(facturaId)
            _facturaSeleccionada.emit(facturaRoom)

            db.collection("facturas").document(facturaId).get()
                .addOnSuccessListener { document ->
                    val factura = document.toObject(Factura::class.java)
                    _facturaSeleccionada.value = factura
                }
                .addOnFailureListener {
                    Log.e("FacturaViewModel", "Error al obtener factura", it)
                }
                .addOnCompleteListener {
                    _isLoading.value = false
                }
        }
    }

    fun enviarFacturaPorCorreo(factura: Factura, context: Context) {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("cliente@example.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Factura N° ${factura.id}")
            putExtra(Intent.EXTRA_TEXT, "Adjunto la factura correspondiente a su compra.")
            factura.pdfUrl?.let { putExtra(Intent.EXTRA_STREAM, Uri.parse(it)) }
        }

        try {
            context.startActivity(Intent.createChooser(emailIntent, "Enviar factura"))
        } catch (e: Exception) {
            Log.e("FacturaViewModel", "Error al enviar factura por correo", e)
        }
    }

    fun descargarFacturaPDF(factura: Factura, context: Context) {
        viewModelScope.launch {
            val pdfFile = generarFacturaPDF(factura, context)
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", pdfFile)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("FacturaViewModel", "Error al abrir PDF", e)
            }
        }
    }

    private suspend fun generarFacturaPDF(factura: Factura, context: Context): File {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Factura_${factura.id}.pdf")

        withContext(Dispatchers.IO) {
            try {
                val pdfDocument = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(600, 900, 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas
                val paint = Paint().apply { textSize = 16f }
                val titlePaint = Paint().apply {
                    textSize = 22f
                    typeface = Typeface.DEFAULT_BOLD
                }
                val boldPaint = Paint().apply {
                    textSize = 18f
                    typeface = Typeface.DEFAULT_BOLD
                }
                val linePaint = Paint().apply {
                    color = Color.BLACK
                    strokeWidth = 2f
                }

                var yPos = 40f

                val logoBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.ic_launcher)
                val logoX = 20f
                val logoY = yPos
                canvas.drawBitmap(Bitmap.createScaledBitmap(logoBitmap, 100, 100, false), logoX, logoY, paint)
                canvas.drawText("FACTURA", 250f, logoY + 40, titlePaint)

                yPos += 120f
                canvas.drawLine(20f, yPos, 580f, yPos, linePaint)

                yPos += 30f
                canvas.drawText("Factura N°: ${factura.id}", 20f, yPos, boldPaint)
                yPos += 30f
                canvas.drawText("Cliente: ${factura.clienteNombre}", 20f, yPos, paint)
                yPos += 30f
                canvas.drawText("Fecha: ${factura.fecha}", 20f, yPos, paint)
                yPos += 30f
                canvas.drawText("Total: ${NumberFormat.getCurrencyInstance().format(factura.montoTotal)}", 20f, yPos, paint)

                yPos += 50f
                canvas.drawLine(20f, yPos, 580f, yPos, linePaint)

                yPos += 30f
                canvas.drawText("📦 Productos:", 20f, yPos, boldPaint)

                yPos += 30f
                canvas.drawText("Cantidad", 30f, yPos, boldPaint)
                canvas.drawText("Descripción", 250f, yPos, boldPaint)
                yPos += 20f
                canvas.drawLine(20f, yPos, 580f, yPos, linePaint)

                factura.detalles.forEach { producto ->
                    yPos += 40f
                    canvas.drawText("1", 30f, yPos, paint)
                    canvas.drawText(producto, 250f, yPos, paint)
                }

                yPos += 50f
                canvas.drawLine(20f, yPos, 580f, yPos, linePaint)
                yPos += 30f
                canvas.drawText("Gracias por su compra", 250f, yPos, boldPaint)

                pdfDocument.finishPage(page)
                pdfDocument.writeTo(FileOutputStream(file))
                pdfDocument.close()
            } catch (e: Exception) {
                Log.e("FacturaViewModel", "Error al generar PDF", e)
            }
        }

        return file
    }
}

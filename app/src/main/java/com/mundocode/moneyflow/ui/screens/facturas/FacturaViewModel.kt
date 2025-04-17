package com.mundocode.moneyflow.ui.screens.facturas

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.database.entity.Factura
import com.mundocode.moneyflow.database.daos.FacturaDao
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.scale
import androidx.core.net.toUri

@HiltViewModel
class FacturaViewModel @Inject constructor(
    private val facturaDao: FacturaDao,
    @ApplicationContext private val context: Context,
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
                        Timber.tag("FacturaViewModel").e(it, "Error al obtener facturas")
                    }
                    .addOnCompleteListener {
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                Timber.tag("FacturaViewModel").e(e, "Error cargando facturas")
                _isLoading.value = false
            }
        }
    }

    fun actualizarEstadoFactura(facturaActualizada: Factura) {
        viewModelScope.launch(Dispatchers.IO) {
            facturaDao.updateFactura(facturaActualizada)
            db.collection("facturas").document(facturaActualizada.id).set(facturaActualizada)
            _facturaSeleccionada.emit(facturaActualizada)
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

    fun eliminarFactura(factura: Factura) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Eliminar de Room
                facturaDao.deleteFactura(factura)

                // Eliminar de Firestore
                db.collection("facturas").document(factura.id).delete()
                    .addOnSuccessListener {
                        Timber.tag("FacturaViewModel").d("Factura eliminada: ${factura.id}")
                    }
                    .addOnFailureListener {
                        Timber.tag("FacturaViewModel").e(it, "Error al eliminar factura de Firestore")
                    }
            } catch (e: Exception) {
                Timber.tag("FacturaViewModel").e(e, "Error al eliminar factura")
            }
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
                    Timber.tag("FacturaViewModel").e(it, "Error al obtener factura")
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
            putExtra(Intent.EXTRA_SUBJECT, "${context.getString(R.string.invoice_nr)}° ${factura.id}")
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.invoice_email))
            factura.pdfUrl?.let { putExtra(Intent.EXTRA_STREAM, it.toUri()) }
        }

        try {
            context.startActivity(Intent.createChooser(emailIntent, context.getString(R.string.send_invoice)))
        } catch (e: Exception) {
            Timber.tag("FacturaViewModel").e(e, "Error al enviar factura por correo")
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
                Timber.tag("FacturaViewModel").e(e, "Error al abrir PDF")
            }
        }
    }

    private suspend fun generarFacturaPDF(factura: Factura, context: Context): File {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Invoice_${factura.id}.pdf")

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
                canvas.drawBitmap(logoBitmap.scale(100, 100, false), logoX, logoY, paint)
                canvas.drawText(context.getString(R.string.invoice_name), 250f, logoY + 40, titlePaint)

                yPos += 120f
                canvas.drawLine(20f, yPos, 580f, yPos, linePaint)

                yPos += 30f
                canvas.drawText("${context.getString(R.string.invoice_nr)}°: ${factura.id}", 20f, yPos, boldPaint)
                yPos += 30f
                canvas.drawText("${context.getString(R.string.client)}: ${factura.clienteNombre}", 20f, yPos, paint)
                yPos += 30f
                canvas.drawText("${context.getString(R.string.date)}: ${factura.fecha}", 20f, yPos, paint)
                yPos += 30f
                canvas.drawText("${context.getString(R.string.total)}: ${NumberFormat.getCurrencyInstance().format(factura.montoTotal)}", 20f, yPos, paint)

                yPos += 50f
                canvas.drawLine(20f, yPos, 580f, yPos, linePaint)

                yPos += 30f
                canvas.drawText("📦 ${context.getString(R.string.products)}:", 20f, yPos, boldPaint)

                yPos += 30f
                canvas.drawText(context.getString(R.string.quantity), 30f, yPos, boldPaint)
                canvas.drawText(context.getString(R.string.description), 250f, yPos, boldPaint)
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
                canvas.drawText(context.getString(R.string.thanks), 250f, yPos, boldPaint)

                pdfDocument.finishPage(page)
                pdfDocument.writeTo(FileOutputStream(file))
                pdfDocument.close()
            } catch (e: Exception) {
                Timber.tag("FacturaViewModel").e(e, "Error al generar PDF")
            }
        }

        return file
    }
}

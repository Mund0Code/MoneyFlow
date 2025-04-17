package com.mundocode.moneyflow.ui.screens.facturas

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.database.daos.TransaccionDao
import com.mundocode.moneyflow.database.entity.Transaccion
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class OCRViewModel @Inject constructor(
    private val transaccionDao: TransaccionDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _facturaTexto = MutableStateFlow<String?>(null)
    val facturaTexto: StateFlow<String?> = _facturaTexto

    private val _productosEscaneados = MutableStateFlow<List<String>>(emptyList())
    val productosEscaneados: StateFlow<List<String>> = _productosEscaneados

    /**
     * Procesa una imagen usando ML Kit OCR.
     */
    fun procesarImagen(uri: Uri, context: Context) {
        val image: InputImage = InputImage.fromFilePath(context, uri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                _facturaTexto.value = visionText.text
            }
            .addOnFailureListener { e ->
                Timber.tag("OCRViewModel").e(e, "Error al reconocer texto")
            }
    }

    /**
     * Escanea un código de barras en una imagen.
     */
    fun procesarCodigoBarras(uri: Uri, context: Context) {
        val image = InputImage.fromFilePath(context, uri)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val productos = barcodes.mapNotNull { it.rawValue }
                _productosEscaneados.value = productos
            }
            .addOnFailureListener { e ->
                Timber.tag("OCRViewModel").e(e, "Error al escanear código de barras")
            }
    }

    /**
     * Guarda el gasto extraído en Firestore y Room.
     */
    fun guardarGasto(texto: String, productos: List<String>) {
        val montoRegex = Regex("\\d+[.,]?\\d*")
        val montoEncontrado = montoRegex.find(texto)?.value?.replace(",", ".")?.toDoubleOrNull() ?: 0.0

        val transaccion = Transaccion(
            id = UUID.randomUUID().toString(),
            tipo = context.getString(R.string.expense),
            monto = montoEncontrado,
            fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
        )

        // Guardar en Firestore
        db.collection("transacciones").document(transaccion.id).set(transaccion)

        // Guardar en Room
        viewModelScope.launch {
            transaccionDao.insertar(transaccion)
        }
    }
}

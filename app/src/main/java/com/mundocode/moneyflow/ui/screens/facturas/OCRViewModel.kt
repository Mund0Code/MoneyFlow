package com.mundocode.moneyflow.ui.screens.facturas

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.mundocode.moneyflow.database.AppDatabase
import com.mundocode.moneyflow.database.Transaccion
import com.mundocode.moneyflow.database.TransaccionDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@HiltViewModel
class OCRViewModel @Inject constructor(
    private val transaccionDao: TransaccionDao
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
                Log.e("OCRViewModel", "Error al reconocer texto", e)
            }
    }

    fun procesarImagenOCR(context: Context, uri: Uri) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromFilePath(context, uri)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                _facturaTexto.value = visionText.text
            }
            .addOnFailureListener {
                _facturaTexto.value = "Error al procesar la imagen"
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
                Log.e("OCRViewModel", "Error al escanear código de barras", e)
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
            tipo = "Gasto",
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

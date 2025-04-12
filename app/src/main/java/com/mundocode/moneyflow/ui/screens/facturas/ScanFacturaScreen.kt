package com.mundocode.moneyflow.ui.screens.facturas

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import androidx.navigation.NavHostController
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.ui.components.BottomNavigationBar
import com.mundocode.moneyflow.ui.components.CustomTopAppBar
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanFacturaScreen(viewModel: OCRViewModel = hiltViewModel(), navController: NavHostController) {
    val context = LocalContext.current
    val facturaTexto by viewModel.facturaTexto.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }


    // 🔹 Lanzador para tomar fotos
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri?.let { viewModel.procesarImagen(it, context) }
        } else {
            Toast.makeText(context, context.getString(R.string.error_picture), Toast.LENGTH_SHORT).show()
        }
    }

    // 🔹 Solicitar permisos de cámara antes de abrir la cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = getImageUri(context)
            imageUri = uri
            takePictureLauncher.launch(uri)
        } else {
            Toast.makeText(context, context.getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = { CustomTopAppBar(navController, stringResource(id = R.string.scan_title)) },
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(id = R.string.select_invoice), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Factura Escaneada",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Capturar Imagen")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.take_photo))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            facturaTexto?.let {
                Text("📄 ${stringResource(id = R.string.data_extracted)}:", fontWeight = FontWeight.Bold)
                Text(it, modifier = Modifier.padding(8.dp))
                Button(onClick = { viewModel.guardarGasto(it, viewModel.productosEscaneados.value) }) {
                    Icon(Icons.Default.Save, contentDescription = "Guardar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(id = R.string.save_expense))
                }
            }
        }
    }
}

/**
 * Función para obtener la URI de la imagen donde se guardará la foto.
 */
fun getImageUri(context: Context): Uri {
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "captured.jpg")
    return FileProvider.getUriForFile(context, "com.mundocode.mundocode.provider", file)
}

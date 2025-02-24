package com.mundocode.moneyflow.ui.screens.gananciasGastos

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mundocode.moneyflow.database.AppDatabase
import com.mundocode.moneyflow.database.Transaccion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class TransaccionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "app-database"
    ).build()

    private val transaccionDao = db.transaccionDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val connectivityManager = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val transacciones: Flow<List<Transaccion>> = transaccionDao.getAllTransacciones()
    private val _transaccionesPendientes = MutableStateFlow<List<Transaccion>>(emptyList())

    init {
        viewModelScope.launch {
            transaccionDao.getTransaccionesNoSincronizadas().collect { lista ->
                _transaccionesPendientes.value = lista
            }
        }

        // 🔹 Detectar cambios en la conexión
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                sincronizarConFirestore()
            }
        })
    }

    fun agregarTransaccion(tipo: String, monto: Double, categoria: String) {
        viewModelScope.launch {
            val nuevaTransaccion = Transaccion(
                id = UUID.randomUUID().toString(),
                tipo = tipo,
                monto = monto,
                fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                categoria = categoria,
                syncStatus = false // 🔹 No está sincronizada aún
            )
            transaccionDao.insertar(nuevaTransaccion)
            sincronizarConFirestore()
        }
    }

    private fun sincronizarConFirestore() {
        viewModelScope.launch {
            val transaccionesPendientes = transaccionDao.getTransaccionesNoSincronizadas().firstOrNull() ?: emptyList()
            for (transaccion in transaccionesPendientes) {
                val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: continue
                firestore.collection("usuarios")
                    .document(usuarioId)
                    .collection("transacciones")
                    .document(transaccion.id)
                    .set(transaccion)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            transaccionDao.marcarComoSincronizada(transaccion.id) // 🔹 Marcar como sincronizada
                        }
                    }
            }
        }
    }

    fun eliminarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch {
            transaccionDao.deleteTransaccion(transaccion)
            // 🔹 También eliminar de Firestore
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            firestore.collection("usuarios")
                .document(usuarioId)
                .collection("transacciones")
                .document(transaccion.id)
                .delete()
        }
    }
}

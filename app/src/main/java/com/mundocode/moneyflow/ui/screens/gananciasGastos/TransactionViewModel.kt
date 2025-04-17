package com.mundocode.moneyflow.ui.screens.gananciasGastos

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.core.TipoTransaccion
import com.mundocode.moneyflow.database.daos.TransaccionDao
import com.mundocode.moneyflow.database.entity.Transaccion
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@HiltViewModel
class TransaccionViewModel @Inject constructor(
    private val transaccionDao: TransaccionDao,
    @ApplicationContext context: Context
) : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val transacciones: Flow<List<Transaccion>> = transaccionDao.getAllTransacciones()

    private val _transaccionesPendientes = MutableStateFlow<List<Transaccion>>(emptyList())

    init {
        // Escuchar transacciones no sincronizadas
        viewModelScope.launch {
            transaccionDao.getTransaccionesNoSincronizadas().collect { lista ->
                _transaccionesPendientes.value = lista
            }
        }

        // Registrar callback de red
        val networkRequest = NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    sincronizarConFirestore()
                }
            }
        )
    }

    fun agregarTransaccion(tipo: String, context: Context, monto: Double, categoria: String) {
        viewModelScope.launch {
            val nuevaTransaccion = Transaccion(
                id = UUID.randomUUID().toString(),
                tipo = if (tipo == context.getString(R.string.income)) TipoTransaccion.INGRESO else TipoTransaccion.GASTO,
                monto = monto,
                fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                categoria = categoria,
                syncStatus = false
            )
            transaccionDao.insertar(nuevaTransaccion)
            sincronizarConFirestore()
        }
    }

    private fun sincronizarConFirestore() {
        viewModelScope.launch {
            val pendientes = transaccionDao.getTransaccionesNoSincronizadas().firstOrNull() ?: emptyList()
            for (transaccion in pendientes) {
                val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: continue
                firestore.collection("usuarios")
                    .document(usuarioId)
                    .collection("transacciones")
                    .document(transaccion.id)
                    .set(transaccion)
                    .addOnSuccessListener {
                        viewModelScope.launch {
                            transaccionDao.marcarComoSincronizada(transaccion.id)
                        }
                    }
            }
        }
    }

    fun eliminarTransaccion(transaccion: Transaccion) {
        viewModelScope.launch {
            transaccionDao.deleteTransaccion(transaccion)
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            firestore.collection("usuarios")
                .document(usuarioId)
                .collection("transacciones")
                .document(transaccion.id)
                .delete()
        }
    }
}

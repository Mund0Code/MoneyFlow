package com.mundocode.moneyflow.ui.screens.clientes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.mundocode.moneyflow.database.AppDatabase
import com.mundocode.moneyflow.database.Cliente
import com.mundocode.moneyflow.database.ClienteDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@HiltViewModel
class ClienteViewModel @Inject constructor(
    private val clienteDao: ClienteDao
) : ViewModel() {

    val clientes: Flow<List<Cliente>> = clienteDao.getAllClientes()

    fun agregarCliente(nombre: String, telefono: String, correo: String) {
        viewModelScope.launch {
            clienteDao.insertCliente(
                Cliente(nombre = nombre, telefono = telefono, correo = correo)
            )
        }
    }

    fun eliminarCliente(cliente: Cliente) {
        viewModelScope.launch {
            clienteDao.deleteCliente(cliente)
        }
    }

    fun editarCliente(cliente: Cliente, nuevoNombre: String) {
        viewModelScope.launch {
            cliente.nombre = nuevoNombre
            clienteDao.updateCliente(cliente)
        }
    }
}

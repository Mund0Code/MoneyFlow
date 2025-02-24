package com.mundocode.moneyflow.ui.screens.clientes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.mundocode.moneyflow.database.AppDatabase
import com.mundocode.moneyflow.database.Cliente
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ClienteViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "app-database"
    ).build()

    private val clienteDao = db.clienteDao()
    val clientes: Flow<List<Cliente>> = clienteDao.getAllClientes()

    fun agregarCliente(nombre: String, telefono: String, correo: String) {
        viewModelScope.launch {
            clienteDao.insertCliente(Cliente(nombre = nombre, telefono = telefono, correo = correo))
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

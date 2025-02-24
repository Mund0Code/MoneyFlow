package com.mundocode.moneyflow.ui.screens.proyectos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.mundocode.moneyflow.database.AppDatabase
import com.mundocode.moneyflow.database.Proyecto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProyectoViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "app-database"
    ).build()

    private val proyectoDao = db.proyectoDao()
    val proyectos: Flow<List<Proyecto>> = proyectoDao.getAllProyectos()

    fun agregarProyecto(
        nombre: String,
        descripcion: String,
        fechaInicio: String,
        fechaFin: String,
        estado: String
    ) {
        viewModelScope.launch {
            proyectoDao.insertProyecto(Proyecto(
                nombre = nombre,
                descripcion = descripcion,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                estado = estado
            ))
        }
    }

    fun eliminarProyecto(proyecto: Proyecto) {
        viewModelScope.launch {
            proyectoDao.deleteProyecto(proyecto)
        }
    }

    fun editarProyecto(proyecto: Proyecto, nuevoNombre: String) {
        viewModelScope.launch {
            proyecto.nombre = nuevoNombre
            proyectoDao.updateProyecto(proyecto)
        }
    }

}
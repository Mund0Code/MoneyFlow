package com.mundocode.moneyflow.ui.screens.proyectos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundocode.moneyflow.database.Proyecto
import com.mundocode.moneyflow.database.ProyectoDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@HiltViewModel
class ProyectoViewModel @Inject constructor(
    private val proyectoDao: ProyectoDao
) : ViewModel() {

    val proyectos: Flow<List<Proyecto>> = proyectoDao.getAllProyectos()

    fun agregarProyecto(
        nombre: String,
        descripcion: String,
        fechaInicio: String,
        fechaFin: String,
        estado: String
    ) {
        viewModelScope.launch {
            proyectoDao.insertProyecto(
                Proyecto(
                    nombre = nombre,
                    descripcion = descripcion,
                    fechaInicio = fechaInicio,
                    fechaFin = fechaFin,
                    estado = estado
                )
            )
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

package com.mundocode.moneyflow.ui.screens.calendario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundocode.moneyflow.database.daos.EventoDao
import com.mundocode.moneyflow.database.entity.Evento
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@HiltViewModel
class EventoViewModel @Inject constructor(
    private val eventoDao: EventoDao
) : ViewModel() {

    val eventos: Flow<List<Evento>> = eventoDao.getAllEventos()

    fun agregarEvento(titulo: String, fecha: String, selectedCategoria: String) {
        viewModelScope.launch {
            eventoDao.insertEvent(
                Evento(
                    titulo = titulo,
                    fecha = fecha,
                    categoria = selectedCategoria
                )
            )
        }
    }

    fun eliminarEvento(evento: Evento) {
        viewModelScope.launch {
            eventoDao.deleteEvent(evento)
        }
    }

    fun actualizarEvento(evento: Evento) {
        viewModelScope.launch {
            eventoDao.updateEvent(evento)
        }
    }
}

package com.mundocode.moneyflow.ui.screens.calendario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundocode.moneyflow.database.daos.EventoDao
import com.mundocode.moneyflow.database.entity.Evento
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EventoViewModel @Inject constructor(
    private val eventoDao: EventoDao,
) : ViewModel() {

    val eventos: Flow<List<Evento>> = eventoDao.getAllEventos()

    private val _eventoSeleccionado = MutableStateFlow<Evento?>(null)
    val eventoSeleccionado: StateFlow<Evento?> = _eventoSeleccionado

    fun seleccionarEvento(evento: Evento) {
        _eventoSeleccionado.value = evento
    }

    fun limpiarEventoSeleccionado() {
        _eventoSeleccionado.value = null
    }

    fun agregarEvento(titulo: String, fecha: String, categoria: String) {
        viewModelScope.launch {
            eventoDao.insertEvent(Evento(titulo = titulo, fecha = fecha, categoria = categoria))
        }
    }

    fun editarEvento(titulo: String, fecha: String, categoria: String) {
        viewModelScope.launch {
            _eventoSeleccionado.value?.let { original ->
                eventoDao.updateEvent(
                    original.copy(titulo = titulo, fecha = fecha, categoria = categoria)
                )
                _eventoSeleccionado.value = null
            }
        }
    }

    fun eliminarEvento(evento: Evento) {
        viewModelScope.launch {
            eventoDao.deleteEvent(evento)
        }
    }
}

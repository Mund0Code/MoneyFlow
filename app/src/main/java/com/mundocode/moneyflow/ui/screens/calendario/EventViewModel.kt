package com.mundocode.moneyflow.ui.screens.calendario

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mundocode.moneyflow.database.daos.EventoDao
import com.mundocode.moneyflow.database.entity.Evento
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@HiltViewModel
class EventoViewModel @Inject constructor(
    private val eventoDao: EventoDao
) : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val eventosCollection = db.collection("eventos")

    val eventos: Flow<List<Evento>> = eventoDao.getAllEventos()

    private val _eventoSeleccionado = MutableStateFlow<Evento?>(null)
    val eventoSeleccionado: StateFlow<Evento?> = _eventoSeleccionado

    init {
        sincronizarEventosDesdeFirestore()
    }

    fun seleccionarEvento(evento: Evento) {
        _eventoSeleccionado.value = evento
    }

    fun limpiarEventoSeleccionado() {
        _eventoSeleccionado.value = null
    }

    fun agregarEvento(titulo: String, fecha: String, categoria: String, context: Context) {
        viewModelScope.launch {
            val evento = Evento(
                id = UUID.randomUUID().toString(),
                titulo = titulo,
                fecha = fecha,
                categoria = categoria
            )
            eventoDao.insertEvent(evento)
            eventosCollection.document(evento.id).set(evento)
            programarRecordatorio(context, evento) // 🔥 Programar recordatorio aquí
        }
    }

    fun editarEvento(evento: Evento, nuevoTitulo: String, nuevaFecha: String, nuevaCategoria: String, context: Context) {
        viewModelScope.launch {
            val eventoActualizado = evento.copy(
                id = evento.id, // ✅ No cambies el ID al editar
                titulo = nuevoTitulo,
                fecha = nuevaFecha,
                categoria = nuevaCategoria
            )
            eventoDao.updateEvent(eventoActualizado)
            eventosCollection.document(eventoActualizado.id).set(eventoActualizado)
            programarRecordatorio(context, eventoActualizado) // 🔥 Programar nueva alarma
        }
    }


    fun eliminarEvento(evento: Evento) {
        viewModelScope.launch {
            eventoDao.deleteEvent(evento)
            eventosCollection.document(evento.id.toString()).delete()
        }
    }

    private fun sincronizarEventosDesdeFirestore() {
        eventosCollection.addSnapshotListener { snapshot, e ->
            if (e != null || snapshot == null) {
                return@addSnapshotListener
            }
            viewModelScope.launch {
                val eventosFirestore = snapshot.toObjects(Evento::class.java)
                eventoDao.insertarEventos(eventosFirestore)
            }
        }
    }

    private fun programarRecordatorio(context: Context, evento: Evento) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, EventReminderReceiver::class.java).apply {
            putExtra("TITULO", evento.titulo)
            putExtra("FECHA", evento.fecha)
            putExtra("ID", evento.id?.hashCode() ?: evento.hashCode())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            evento.id?.hashCode() ?: evento.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val formato = SimpleDateFormat("d/M/yyyy", Locale.getDefault())
        val date = formato.parse(evento.fecha)

        date?.let {
            val calendar = Calendar.getInstance().apply {
                time = it
                set(Calendar.HOUR_OF_DAY, 9) // Notificar a las 9:00 AM
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }


}

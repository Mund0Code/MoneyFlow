package com.mundocode.moneyflow.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mundocode.moneyflow.database.entity.Evento
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {

    @Query("SELECT * FROM Evento")
    fun getAllEventos(): Flow<List<Evento>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(evento: Evento)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEventos(eventos: List<Evento>) // 🔥 Para sincronizar

    @Update
    suspend fun updateEvent(evento: Evento)

    @Delete
    suspend fun deleteEvent(evento: Evento)
}

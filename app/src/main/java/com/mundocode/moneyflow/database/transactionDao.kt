package com.mundocode.moneyflow.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransaccionDao {
    @Query("SELECT * FROM Transaccion")
    fun getAllTransacciones(): Flow<List<Transaccion>>

    @Query("SELECT * FROM Transaccion WHERE tipo = :categoria")
    fun getTransaccionesPorCategoria(categoria: String): Flow<List<Transaccion>>

    @Query("SELECT * FROM Transaccion WHERE syncStatus = 0") // 🔹 Obtener transacciones no sincronizadas
    fun getTransaccionesNoSincronizadas(): Flow<List<Transaccion>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(transaccion: Transaccion)

    @Query("UPDATE Transaccion SET syncStatus = 1 WHERE id = :id") // 🔹 Marcar como sincronizada
    suspend fun marcarComoSincronizada(id: String)

    @Delete
    suspend fun deleteTransaccion(transaccion: Transaccion)
}


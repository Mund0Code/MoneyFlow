package com.mundocode.moneyflow.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mundocode.moneyflow.database.entity.Cliente
import kotlinx.coroutines.flow.Flow

@Dao
interface ClienteDao {
    @Query("SELECT * FROM Cliente")
    fun getAllClientes(): Flow<List<Cliente>>

    @Insert
    suspend fun insertCliente(cliente: Cliente)

    @Update
    suspend fun updateCliente(cliente: Cliente)

    @Delete
    suspend fun deleteCliente(cliente: Cliente)
}

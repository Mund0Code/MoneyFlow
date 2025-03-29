package com.mundocode.moneyflow.database.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mundocode.moneyflow.database.entity.Proyecto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProyectoDao {
    @Query("SELECT * FROM Proyecto")
    fun getAllProyectos(): Flow<List<Proyecto>>

    @Insert
    suspend fun insertProyecto(proyecto: Proyecto)

    @Update
    suspend fun updateProyecto(proyecto: Proyecto)

    @Delete
    suspend fun deleteProyecto(proyecto: Proyecto)
}

package com.mundocode.moneyflow.database.daos

import androidx.room.*
import com.mundocode.moneyflow.database.entity.Factura
import kotlinx.coroutines.flow.Flow

@Dao
interface FacturaDao {

    @Query("SELECT * FROM facturas")
    fun getAllFacturas(): Flow<List<Factura>> // ✅ Devuelve Flow para uso reactivo


    @Query("SELECT * FROM facturas WHERE id = :id")
    fun getFacturaById(id: String): Factura?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFactura(factura: Factura)

    @Delete
    suspend fun deleteFactura(factura: Factura)

    // ✅ Método para insertar una lista de facturas en Room
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(facturas: List<Factura>)
}

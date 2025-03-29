package com.mundocode.moneyflow.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mundocode.moneyflow.core.Converters
import com.mundocode.moneyflow.database.daos.ClienteDao
import com.mundocode.moneyflow.database.daos.EventoDao
import com.mundocode.moneyflow.database.daos.FacturaDao
import com.mundocode.moneyflow.database.daos.ProyectoDao
import com.mundocode.moneyflow.database.daos.TransaccionDao
import com.mundocode.moneyflow.database.entity.Cliente
import com.mundocode.moneyflow.database.entity.Evento
import com.mundocode.moneyflow.database.entity.Factura
import com.mundocode.moneyflow.database.entity.Proyecto
import com.mundocode.moneyflow.database.entity.Transaccion

@Database(entities = [Cliente::class, Proyecto::class, Transaccion::class, Evento::class, Factura::class], version = 1)
@TypeConverters(Converters::class) // 🔹 Agregar TypeConverters
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun proyectoDao(): ProyectoDao
    abstract fun transaccionDao(): TransaccionDao
    abstract fun eventoDao(): EventoDao
    abstract fun facturaDao(): FacturaDao
}


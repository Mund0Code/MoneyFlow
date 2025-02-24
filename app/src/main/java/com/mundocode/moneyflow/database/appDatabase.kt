package com.mundocode.moneyflow.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mundocode.moneyflow.core.Converters

@Database(entities = [Cliente::class, Proyecto::class, Transaccion::class, Evento::class, Factura::class], version = 1)
@TypeConverters(Converters::class) // 🔹 Agregar TypeConverters
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun proyectoDao(): ProyectoDao
    abstract fun transaccionDao(): TransaccionDao
    abstract fun eventoDao(): EventoDao
    abstract fun facturaDao(): FacturaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}


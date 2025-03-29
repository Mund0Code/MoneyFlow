package com.mundocode.moneyflow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Transaccion(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val tipo: String, // "Ingreso" o "Gasto"
    val monto: Double,
    val fecha: String,
    val categoria: String? = null,
    val syncStatus: Boolean = false // 🔹 Nueva propiedad para saber si está sincronizada
) {
    constructor() : this("", "", 0.0, "", null, false) // ✅ Constructor sin argumentos
}
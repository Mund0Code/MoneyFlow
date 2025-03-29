package com.mundocode.moneyflow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facturas")
data class Factura(
    @PrimaryKey val id: String = "",
    val clienteId: String = "",
    val clienteNombre: String = "",
    val fecha: String = "",
    val montoTotal: Double = 0.0,
    val detalles: List<String> = emptyList(),
    val estado: String = "Pendiente", // "Pendiente", "Pagado", "Cancelado"
    val pdfUrl: String? = null // URL del PDF si se almacena en Firebase Storage
) {
    // 🔹 Constructor vacío requerido por Firestore
    constructor() : this("", "", "", "", 0.0, emptyList(), "Pendiente", null)
}

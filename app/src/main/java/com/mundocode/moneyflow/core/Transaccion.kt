package com.mundocode.moneyflow.core

import androidx.annotation.Keep

@Keep
data class Transaccion(
    val id: String = "",
    val tipo: String = "",  // "Ingreso" o "Gasto"
    val monto: Double = 0.0,
    val fecha: String = "",
    val categoria: String? = null
) {
    constructor() : this("", "", 0.0, "", null) // ✅ Constructor sin argumentos
}

package com.mundocode.moneyflow.core

data class Transaccion(
    val id: String = "",
    val tipo: String = "",  // "Ingreso" o "Gasto"
    val monto: Double = 0.0,
    val fecha: String = ""
)

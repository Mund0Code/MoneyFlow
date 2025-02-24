package com.mundocode.moneyflow.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Proyecto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var nombre: String,
    var descripcion: String,
    var fechaInicio: String,
    var fechaFin: String,
    var estado: String
)
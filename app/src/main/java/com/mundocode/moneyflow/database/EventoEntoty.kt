package com.mundocode.moneyflow.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Evento(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val fecha: String,
    val categoria: String
)
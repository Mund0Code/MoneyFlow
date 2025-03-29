package com.mundocode.moneyflow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cliente(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var nombre: String,
    var telefono: String,
    var correo: String
)
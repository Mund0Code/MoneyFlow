package com.mundocode.moneyflow.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class Evento(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val fecha: String,
    val categoria: String
) {
    constructor() : this("", "", "", "")
}
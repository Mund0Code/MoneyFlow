package com.mundocode.moneyflow.core

import androidx.annotation.Keep

@Keep
data class User(
    val id: String,
    val email: String
)

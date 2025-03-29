package com.mundocode.moneyflow.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.mundocode.moneyflow.database.daos.TransaccionDao
import com.mundocode.moneyflow.database.entity.Transaccion
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transaccionDao: TransaccionDao
) : ViewModel() {

    private val _prediccionIngresos = MutableStateFlow(0.0)
    val prediccionIngresos: StateFlow<Double> = _prediccionIngresos

    private val _prediccionGastos = MutableStateFlow(0.0)
    val prediccionGastos: StateFlow<Double> = _prediccionGastos

    val totalIngresos: Flow<Double> = transaccionDao.getAllTransacciones().map { transacciones ->
        transacciones.filter { it.tipo == "Ingreso" }.sumOf { it.monto }
    }

    val totalGastos: Flow<Double> = transaccionDao.getAllTransacciones().map { transacciones ->
        transacciones.filter { it.tipo == "Gasto" }.sumOf { it.monto }
    }

    val flujoDeCaja: Flow<Double> = combine(totalIngresos, totalGastos) { ingresos, gastos ->
        ingresos - gastos
    }

    private val _categoriasDisponibles = MutableStateFlow<List<String>>(emptyList())
    val categoriasDisponibles: StateFlow<List<String>> = _categoriasDisponibles

    private val _transaccionesFiltradas = MutableStateFlow<List<Transaccion>>(emptyList())
    val transaccionesFiltradas: StateFlow<List<Transaccion>> = _transaccionesFiltradas

    init {
        viewModelScope.launch {
            transaccionDao.getAllTransacciones().collect { lista ->
                _transaccionesFiltradas.value = lista
                actualizarCategorias(lista)
            }
        }
    }

    private fun actualizarCategorias(transacciones: List<Transaccion>) {
        val categoriasUnicas = transacciones.mapNotNull { it.categoria }.distinct().sorted()
        _categoriasDisponibles.value = listOf("Todas") + categoriasUnicas
    }

    fun filtrarTransacciones(categoria: String) {
        viewModelScope.launch {
            val lista = if (categoria == "Todas") {
                transaccionDao.getAllTransacciones().firstOrNull() ?: emptyList()
            } else {
                transaccionDao.getAllTransacciones().firstOrNull()
                    ?.filter { it.categoria?.trim() == categoria.trim() } ?: emptyList()
            }
            _transaccionesFiltradas.value = lista
        }
    }
}
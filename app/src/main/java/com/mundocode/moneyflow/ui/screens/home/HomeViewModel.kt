package com.mundocode.moneyflow.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mundocode.moneyflow.R
import com.mundocode.moneyflow.database.daos.TransaccionDao
import com.mundocode.moneyflow.database.entity.Transaccion
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val transaccionDao: TransaccionDao,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _prediccionIngresos = MutableStateFlow(0.0)
    val prediccionIngresos: StateFlow<Double> = _prediccionIngresos.asStateFlow()

    private val _prediccionGastos = MutableStateFlow(0.0)
    val prediccionGastos: StateFlow<Double> = _prediccionGastos

//    val totalIngresos: StateFlow<Double> = transaccionDao.getAllTransacciones()
//        .filter {
//            Timber.d("Ingreso?: ${context.getString(R.string.income)}")
//            context.getString(R.string.income) in it.map{it.tipo}
//        }
//        .map{
//            Timber.d("sum")
//            it.sumOf { it.monto }
//        }
////        .map { transacciones ->
////            Timber.d("transacciones: ${transacciones.first().id}")
////            transacciones
////                .filter { it.tipo == context.getString(R.string.income) }
////                .sumOf {
////                    Timber.d("Monto: ")
////                    it.monto
////                }
////        }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = 10.0
//        )
//
//    val totalGastos: StateFlow<Double> = transaccionDao.getAllTransacciones()
//        .map { transacciones ->
//            transacciones
//                .filter { it.tipo == context.getString(R.string.expense) }
//                .sumOf { it.monto }
//        }.stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = 0.0
//        )
//
//    val flujoDeCaja: StateFlow<Double> = combine(totalIngresos, totalGastos) { ingresos, gastos ->
//        ingresos - gastos
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = 0.0
//    )


    val totalIngresos: StateFlow<Double>
        field:MutableStateFlow<Double> = MutableStateFlow(0.0)

    val totalGastos: StateFlow<Double>
        field:MutableStateFlow<Double> = MutableStateFlow(0.0)

    val flujoDeCaja: StateFlow<Double>
        field:MutableStateFlow<Double> = MutableStateFlow(0.0)


//    private val _categoriasDisponibles = MutableStateFlow<List<String>>(emptyList())
//    val categoriasDisponibles: StateFlow<List<String>> = _categoriasDisponibles
//
//    private val _transaccionesFiltradas = MutableStateFlow<List<Transaccion>>(emptyList())
//    val transaccionesFiltradas: StateFlow<List<Transaccion>> = _transaccionesFiltradas

    val categoriasDisponibles: StateFlow<List<String>>
        field:MutableStateFlow<List<String>> = MutableStateFlow(emptyList())

    //    val transaccionesFiltradas: StateFlow<List<Transaccion>>
//        field:MutableStateFlow<List<Transaccion>> = MutableStateFlow(emptyList())
    val transaccionesFiltradas: StateFlow<List<Transaccion>> = transaccionDao.getAllTransacciones()
        .also {
            viewModelScope.launch {
                it.collectLatest {
                    Timber.d("-- transacciones --: ${it.size}")

                    it.forEach {transaction->
                        Timber.d("-- transacciones tipo --: ${transaction.tipo}")
                        Timber.d("-- string --: ${context.getString(R.string.income)}")
                        when(transaction.tipo){
                            context.getString(R.string.income) -> totalIngresos.value += transaction.monto
                            context.getString(R.string.expense) -> totalGastos.value += transaction.monto
                        }
                    }
                    // Calcular cashflow
                    flujoDeCaja.update{totalIngresos.value - totalGastos.value}
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

//    init {
//        viewModelScope.launch {
//            transaccionDao.getAllTransacciones().collectLatest { lista ->
//                _transaccionesFiltradas.value = lista
//                actualizarCategorias(lista)
//            }
//        }
//    }

    private fun actualizarCategorias(transacciones: List<Transaccion>) {
//        val categoriasUnicas = transacciones.mapNotNull { it.categoria }.distinct().sorted()
//        _categoriasDisponibles.value = listOf(context.getString(R.string.all)) + categoriasUnicas
    }

    fun filtrarTransacciones(categoria: String) {
//        viewModelScope.launch {
//            val lista = if (categoria == context.getString(R.string.all)) {
//                transaccionDao.getAllTransacciones().firstOrNull() ?: emptyList()
//            } else {
//                transaccionDao.getAllTransacciones().firstOrNull()
//                    ?.filter { it.categoria?.trim() == categoria.trim() } ?: emptyList()
//            }
//            _transaccionesFiltradas.value = lista
//        }
    }
}
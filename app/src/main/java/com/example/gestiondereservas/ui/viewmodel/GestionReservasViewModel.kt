package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GestionReservasViewModel(
    private val repository: ReservaRepository
): ViewModel() {
    private val reservasPendiente = MutableStateFlow<List<ReservaEntity>>(emptyList())
    val reservasPendientes: StateFlow<List<ReservaEntity>> = reservasPendiente.asStateFlow()

    private val totalAprobadas = MutableStateFlow(0)
    val aprobadas: StateFlow<Int> = totalAprobadas.asStateFlow()

    private val totalRechazadas = MutableStateFlow(0)
    val rechazadas: StateFlow<Int> = totalRechazadas.asStateFlow()

    private val totalPendientes = MutableStateFlow(0)
    val pendientes: StateFlow<Int> = totalPendientes.asStateFlow()

    init {
        cargarDatos()
    }


    fun cargarDatos() {
        viewModelScope.launch {
            reservasPendiente.value = repository.obtenerReservasPendientes()
            totalAprobadas.value = repository.obtenerAprobadas()
            totalRechazadas.value = repository.obtenerRechazadas()
            totalPendientes.value = repository.obtenerPendiente()
        }
    }

    fun modificarEstado(id: Long, estado: String) {
        viewModelScope.launch {
            repository.modificarEstado(id, estado)
        }
    }



}
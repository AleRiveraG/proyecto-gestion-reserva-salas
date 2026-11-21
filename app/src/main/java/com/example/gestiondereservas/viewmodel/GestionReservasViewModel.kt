package com.example.gestiondereservas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import com.example.gestiondereservas.data.remote.dto.DocumentoResponse
import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import com.example.gestiondereservas.data.room.entidades.EstadoReserva

class GestionReservasViewModel(
    private val repository: ReservaRepository,
) : ViewModel() {

    private val reservasPendiente = MutableStateFlow<List<ReservaResponse>>(emptyList())
    val reservasPendientes: StateFlow<List<ReservaResponse>> = reservasPendiente.asStateFlow()

    private val totalAprobadas = MutableStateFlow(0L)
    val aprobadas: StateFlow<Long> = totalAprobadas.asStateFlow()

    private val totalRechazadas = MutableStateFlow(0L)
    val rechazadas: StateFlow<Long> = totalRechazadas.asStateFlow()

    private val totalPendientes = MutableStateFlow(0L)
    val pendientes: StateFlow<Long> = totalPendientes.asStateFlow()

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

    fun modificarEstado(id: Long, estado: EstadoReserva, estadoActual: EstadoReserva) {
        viewModelScope.launch {
            repository.modificarEstado(id, estado, estadoActual)
            cargarDatos()
        }
    }

    suspend fun buscarSala(id: Long): SalaResponse? {
        return repository.obtenerSalaPorId(id)
    }

    suspend fun obtenerDocumento(idReserva: Long): String {
        return repository.obtenerDocumento(idReserva)
    }


}
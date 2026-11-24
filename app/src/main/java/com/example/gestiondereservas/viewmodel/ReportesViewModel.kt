package com.example.gestiondereservas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.remote.dto.ReporteResponse
import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.repository.SalaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.emptyMap

class ReportesViewModel(
    private val reservaRepository: ReservaRepository,
    private val salaRepository: SalaRepository
): ViewModel() {

    private val _salas = MutableStateFlow<List<SalaResponse>>(emptyList())
    val salas: StateFlow<List<SalaResponse>> = _salas.asStateFlow()

    private val _reporte = MutableStateFlow(ReporteResponse())
    val reporte: StateFlow<ReporteResponse> = _reporte.asStateFlow()

    private val _reportesPorSala = MutableStateFlow<Map<Long, ReporteResponse>>(emptyMap())
    val reportesPorSala: StateFlow<Map<Long, ReporteResponse>> = _reportesPorSala.asStateFlow()

    init {
        cargarSalas()
    }

    fun cargarSalas() {
        viewModelScope.launch {
            _salas.value = salaRepository.listarSalas()
        }
    }

    fun generarReporteGeneral(tiempo: String) {
        viewModelScope.launch {
            _reporte.value = reservaRepository.contarReservasGeneral(tiempo)
        }
    }

    fun generarReporteSala(salaId: Long, tiempo: String) {
        viewModelScope.launch {
            val resultado = reservaRepository.contarReservasSala(salaId, tiempo)
            _reportesPorSala.value += (salaId to resultado)
        }
    }

    fun generarReportes(salas: List<SalaResponse>, tiempo: String) {
        salas.forEach { sala ->
            generarReporteSala(sala.salaId, tiempo)
        }
    }
}
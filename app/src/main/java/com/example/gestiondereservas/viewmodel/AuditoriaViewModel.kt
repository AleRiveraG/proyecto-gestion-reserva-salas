package com.example.gestiondereservas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.remote.dto.AuditoriaDto
import com.example.gestiondereservas.data.remote.dto.AuditoriaResponse
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuditoriaViewModel(
    private val auditoriaRepository: AuditoriaRepository
): ViewModel() {
    private val auditorias = MutableStateFlow<List<AuditoriaResponse>>(emptyList())
    val totalAuditorias: StateFlow<List<AuditoriaResponse>> = auditorias.asStateFlow()

    init {
        cargarDatos()
    }


    fun registrarReserva(auditoria: AuditoriaDto) {
        viewModelScope.launch {
            auditoriaRepository.registrarAuditoria(auditoria)
            cargarDatos()
        }
    }

    fun cargarDatos(){
        viewModelScope.launch {
            auditorias.value = auditoriaRepository.obtenerTodas()
        }
    }



    private val auditoriaPorTipo = MutableStateFlow<List<AuditoriaResponse>>(emptyList())
    val totalAuditoriasPorTipo: StateFlow<List<AuditoriaResponse>> = auditoriaPorTipo.asStateFlow()

    private val auditoriaPorFecha = MutableStateFlow<List<AuditoriaResponse>>(emptyList())
    val totalAuditoriasPorFecha: StateFlow<List<AuditoriaResponse>> = auditoriaPorFecha.asStateFlow()

}
package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.AuditoriaRepository
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuditoriaViewModel(
    private val auditoriaRepository: AuditoriaRepository
): ViewModel() {
    private val auditorias = MutableStateFlow<List<AuditoriaEntity>>(emptyList())
    val totalAuditorias: StateFlow<List<AuditoriaEntity>> = auditorias.asStateFlow()

    init {
        cargarDatos()
    }

    fun registrarReserva(auditoria: AuditoriaEntity) {
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

    private val auditoriaPorTipo = MutableStateFlow<List<AuditoriaEntity>>(emptyList())
    val totalAuditoriasPorTipo: StateFlow<List<AuditoriaEntity>> = auditoriaPorTipo.asStateFlow()

    private val auditoriaPorFecha = MutableStateFlow<List<AuditoriaEntity>>(emptyList())
    val totalAuditoriasPorFecha: StateFlow<List<AuditoriaEntity>> = auditoriaPorFecha.asStateFlow()

}
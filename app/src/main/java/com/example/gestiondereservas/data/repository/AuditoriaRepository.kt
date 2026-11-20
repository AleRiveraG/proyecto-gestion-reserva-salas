package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.remote.AuditoriaApiService
import com.example.gestiondereservas.data.remote.dto.AuditoriaDto
import com.example.gestiondereservas.data.remote.dto.AuditoriaResponse
import com.example.gestiondereservas.data.room.AuditoriaDao
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import java.time.LocalDate

class AuditoriaRepository(
    private val apiAuditoria: AuditoriaApiService
){

    suspend fun registrarAuditoria(auditoria: AuditoriaDto) {
        apiAuditoria.registrarAuditoria(auditoria)
    }

    suspend fun obtenerTodas(): List<AuditoriaResponse> {
        return apiAuditoria.obtenerAuditorias()
    }

    suspend fun obtenerPorTipo(tipo: String): List<AuditoriaResponse> {
        return apiAuditoria.obtenerPorTipo(tipo)
    }

    suspend fun obtenerPorFecha(fecha: LocalDate): List<AuditoriaResponse> {
        return apiAuditoria.obtenerPorFecha(fecha)
    }

}
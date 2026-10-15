package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.room.AuditoriaDao
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity
import kotlinx.coroutines.flow.StateFlow

class AuditoriaRepository(
    private val auditoriaDao: AuditoriaDao
){

    suspend fun registrarAuditoria(auditoria: AuditoriaEntity) {
        auditoriaDao.registrarAuditoria(auditoria)
    }

    suspend fun obtenerTodas(): List<AuditoriaEntity> {
        return auditoriaDao.obtenerTodas()
    }

    suspend fun obtenerPorTipo(tipo: String): List<AuditoriaEntity> {
        return auditoriaDao.obtenerPorTipo(tipo)
    }

    suspend fun obtenerPorFecha(fecha: String): List<AuditoriaEntity> {
        return auditoriaDao.obtenerPorFecha(fecha)
    }

}
package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gestiondereservas.data.room.entidades.AuditoriaEntity

@Dao
interface AuditoriaDao {

    @Insert
    suspend fun registrarAuditoria(auditoria: AuditoriaEntity)

    @Query("SELECT * FROM auditoria")
    suspend fun obtenerTodas(): List<AuditoriaEntity>

    @Query("SELECT * FROM auditoria WHERE tipo = :tipo")
    suspend fun obtenerPorTipo(tipo: String): List<AuditoriaEntity>

    @Query("SELECT * FROM auditoria WHERE fecha = :fecha")
    suspend fun obtenerPorFecha(fecha: String): List<AuditoriaEntity>
}
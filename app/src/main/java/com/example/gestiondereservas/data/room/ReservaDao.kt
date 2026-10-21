package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {

    @Insert
    suspend fun registrar(reserva: ReservaEntity)

    @Query("UPDATE reserva SET estado = :estadoNuevo WHERE id = :idReserva")
    suspend fun modificarEstado(idReserva: Long, estadoNuevo: String)

    @Query("SELECT * FROM reserva WHERE estado = 'PENDIENTE'")
    suspend fun obtenerReservasPendientes(): List<ReservaEntity>

    @Query("SELECT COUNT(*) FROM reserva WHERE estado = 'APROBADA' ")
    suspend fun obtenerAprobadas(): Int

    @Query("SELECT COUNT(*) FROM reserva WHERE estado = 'RECHAZADA'")
    suspend fun obtenerRechazadas(): Int

    @Query("SELECT COUNT(*) FROM reserva WHERE estado = 'PENDIENTE'")
    suspend fun obtenerPendientes(): Int

    // Notificaciones: nuevas solicitudes
    @Query("SELECT * FROM reserva WHERE estado = 'PENDIENTE'")
    fun observarReservasPendientes(): Flow<List<ReservaEntity>>

    @Query("SELECT * FROM reserva WHERE salaId = :salaId")
    suspend fun obtenerReservasPorSala(salaId: Long): List<ReservaEntity>

    @Query("SELECT * FROM reserva WHERE idUsuario = :idUsuario")
    suspend fun obtenerReservasPorUsuario(idUsuario: Long): List<ReservaEntity>

    // Buscar una reserva por su ID (Uso de Sala)
    @Query("SELECT * FROM reserva WHERE id = :id LIMIT 1")
    suspend fun obtenerReservaPorId(id: Long): ReservaEntity?

    // Recordatorios: cambios en reservas aprobadas
    @Query("SELECT * FROM reserva WHERE estado = 'APROBADA'")
    fun observarReservasAprobadas(): Flow<List<ReservaEntity>>

    // Notificaciones: aprobaciones y rechazos
    @Query("""
        SELECT * FROM reserva
        WHERE estado IN ('APROBADA', 'RECHAZADA')
    """)
    fun observarReservasResueltas(): Flow<List<ReservaEntity>>

    // Uso de Sala: consultar reservas aprobadas
    @Query("SELECT * FROM reserva WHERE estado = 'APROBADA'")
    suspend fun obtenerReservasAprobadas(): List<ReservaEntity>

    // Consultar reservas de una sala
    @Query("SELECT * FROM reserva WHERE salaId = :salaId")
    suspend fun obtenerReservasPorSalas(salaId: Long): List<ReservaEntity>

    // Uso de Sala: reservas aprobadas del relator
    @Query("""
        SELECT * FROM reserva
        WHERE LOWER(TRIM(correoResponsable)) = LOWER(TRIM(:correo))
          AND estado = 'APROBADA'
        ORDER BY fechaActividad ASC, horaInicio ASC
    """)
    suspend fun obtenerReservasAprobadasPorCorreo(
        correo: String
    ): List<ReservaEntity>

    // Uso de Sala: cancelar una reserva aprobada
    @Query("""
        UPDATE reserva
        SET estado = 'CANCELADA'
        WHERE id = :reservaId
          AND estado = 'APROBADA'
    """)
    suspend fun cancelarReservaAprobada(reservaId: Long): Int
}

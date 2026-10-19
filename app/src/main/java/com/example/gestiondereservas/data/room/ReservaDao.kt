
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

    @Query("SELECT COUNT(*) FROM reserva WHERE estado = 'APROBADA'")
    suspend fun obtenerAprobadas(): Int

    @Query("SELECT COUNT(*) FROM reserva WHERE estado = 'RECHAZADA'")
    suspend fun obtenerRechazadas(): Int

    @Query("SELECT COUNT(*) FROM reserva WHERE estado = 'PENDIENTE'")
    suspend fun obtenerPendientes(): Int

    // Buscar una reserva por su ID (Uso de Sala)
    @Query("SELECT * FROM reserva WHERE id = :id LIMIT 1")
    suspend fun obtenerReservaPorId(id: Long): ReservaEntity?

    // Obtener reservas aprobadas
    @Query("SELECT * FROM reserva WHERE estado = 'APROBADA'")
    suspend fun obtenerReservasAprobadas(): List<ReservaEntity>

    // Observar reservas aprobadas para recordatorios
    @Query("SELECT * FROM reserva WHERE estado = 'APROBADA'")
    fun observarReservasAprobadas(): Flow<List<ReservaEntity>>

    // Observar aprobaciones y rechazos para notificaciones
    @Query("""
        SELECT * FROM reserva
        WHERE estado IN ('APROBADA', 'RECHAZADA')
    """)
    fun observarReservasResueltas(): Flow<List<ReservaEntity>>

    // Consultar reservas de una sala (main)
    @Query("SELECT * FROM reserva WHERE salaId = :salaId")
    suspend fun obtenerReservasPorSala(salaId: Long): List<ReservaEntity>

    @Query
        ("""
        SELECT * FROM reserva
        WHERE LOWER(TRIM(correoResponsable)) = LOWER(TRIM(:correo))
        AND estado = 'APROBADA'
        ORDER BY fechaActividad ASC, horaInicio ASC
        """)

    suspend fun obtenerReservasAprobadasPorCorreo(
        correo: String
    ): List<ReservaEntity>

    @Query
        ("""
    UPDATE reserva
    SET estado = 'CANCELADA'
    WHERE id = :reservaId
      AND estado = 'APROBADA'
    """)

    suspend fun cancelarReservaAprobada(reservaId: Long): Int
}

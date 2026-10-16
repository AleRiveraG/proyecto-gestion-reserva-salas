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

    @Query("SELECT * FROM reserva WHERE estado = 'APROBADA'")
    fun observarReservasAprobadas(): Flow<List<ReservaEntity>>

    @Query("SELECT * FROM reserva WHERE id = :id LIMIT 1")
    suspend fun obtenerReservaPorId(id: Long): ReservaEntity?

    @Query("""
    SELECT * FROM reserva
    WHERE estado IN ('APROBADA', 'RECHAZADA')
""")
    fun observarReservasResueltas(): Flow<List<ReservaEntity>>
}
package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

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

    @Query("SELECT * FROM reserva WHERE id = :idReserva")
    suspend fun obtenerReservaPorId (idReserva: Long) : ReservaEntity?

    @Query("SELECT * FROM reserva WHERE estado = 'APROBADA'")
    suspend fun obtenerReservasAprobadas(): List<ReservaEntity>
}
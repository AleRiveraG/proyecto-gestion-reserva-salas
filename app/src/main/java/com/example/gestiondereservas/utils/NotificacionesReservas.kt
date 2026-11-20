package com.example.gestiondereservas.utils

import android.content.Context
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.NotificacionEntity
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.EstadoReserva

object NotificacionesReservas {

    suspend fun registrarCambio(
        context: Context,
        reserva: ReservaEntity
    ) {
        val tipo = when (reserva.estado) {
            EstadoReserva.APROBADA -> "APROBADA"
            EstadoReserva.RECHAZADA -> "RECHAZADA"
            else -> return
        }

        val dao = AppDatabase.getInstance(context).notificacionDao()

        if (dao.contarPorReservaYTipo(reserva.id, tipo) > 0) {
            return
        }

        val titulo = when (reserva.estado) {
            EstadoReserva.APROBADA -> "Reserva aprobada"
            EstadoReserva.RECHAZADA -> "Reserva rechazada"
            else -> return
        }

        val descripcion = when (reserva.estado) {
            EstadoReserva.APROBADA ->
                "${reserva.nombreActividad} · ${reserva.fechaActividad}"

            EstadoReserva.RECHAZADA ->
                "Tu solicitud para ${reserva.nombreActividad} fue rechazada"

            else -> return
        }

        dao.guardarNotificacion(
            NotificacionEntity(
                titulo = titulo,
                descripcion = descripcion,
                tipo = tipo,
                esSupervisor = false,
                reservaId = reserva.id
            )
        )
    }
}
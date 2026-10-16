package com.example.gestiondereservas.utils

import android.content.Context
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.NotificacionEntity
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import kotlinx.coroutines.flow.collectLatest

object ObservadorNotificaciones {

    suspend fun observar(context: Context) {

        val database = AppDatabase.getInstance(context)
        val reservaDao = database.reservaDao()
        val notificacionDao = database.notificacionDao()

        reservaDao.observarReservasResueltas().collectLatest { reservas ->

            reservas.forEach { reserva ->

                val tipo = when (reserva.estado) {
                    EstadoReserva.APROBADA -> "APROBADA"
                    EstadoReserva.RECHAZADA -> "RECHAZADA"
                    else -> return@forEach
                }

                val existe = notificacionDao.contarPorReservaYTipo(
                    reserva.id,
                    tipo
                )

                if (existe == 0) {

                    val titulo = if (tipo == "APROBADA") {
                        "Reserva aprobada"
                    } else {
                        "Reserva rechazada"
                    }

                    val descripcion = if (tipo == "APROBADA") {
                        "${reserva.nombreActividad} · ${reserva.fechaActividad}"
                    } else {
                        "Tu solicitud para ${reserva.nombreActividad} fue rechazada"
                    }

                    notificacionDao.guardarNotificacion(
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
        }
    }
}
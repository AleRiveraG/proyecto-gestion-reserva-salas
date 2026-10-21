
package com.example.gestiondereservas.utils

import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.NotificacionEntity
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object ObservadorNotificaciones {

    suspend fun observar(context: Context) = coroutineScope {

        val database = AppDatabase.getInstance(context)
        val reservaDao = database.reservaDao()
        val notificacionDao = database.notificacionDao()

        // Notificaciones de nuevas solicitudes para el supervisor
        launch {
            reservaDao.observarReservasPendientes().collectLatest { reservas ->

                reservas.forEach { reserva ->

                    val tipo = "SOLICITUD"

                    val existe = notificacionDao.contarPorReservaYTipo(
                        reserva.id,
                        tipo
                    )

                    if (existe == 0) {
                        notificacionDao.guardarNotificacion(
                            NotificacionEntity(
                                titulo = "Nueva solicitud de reserva",
                                descripcion = "${reserva.nombreActividad} · ${reserva.fechaActividad}",
                                tipo = tipo,
                                esSupervisor = true,
                                reservaId = reserva.id
                            )
                        )
                        mostrarAvisoSiPermitido(
                            context,
                            "Nueva solicitud de reserva",
                            "${reserva.nombreActividad} · ${reserva.fechaActividad}"
                        )
                    }
                }
            }
        }

        // Notificaciones de aprobación o rechazo para el relator
        launch {
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
                                reservaId = reserva.id,
                                correoDestinatario = reserva.correoResponsable
                                    .trim()
                                    .lowercase()
                            )
                        )
                        mostrarAvisoSiPermitido(context, titulo, descripcion)
                    }
                }
            }
        }
    }

    private fun mostrarAvisoSiPermitido(context: Context, titulo: String, mensaje: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return

        try {
            NotificacionHelper.mostrarNotificacion(context, titulo, mensaje)
        } catch (e: SecurityException) {
            android.util.Log.w("ObservadorNotificaciones", "Sin permiso para mostrar notificación", e)
        }
    }

}


package com.example.gestiondereservas.utils

import android.content.Context
import com.example.gestiondereservas.data.room.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

object ObservadorRecordatorios {

    suspend fun observar(context: Context) {

        val dao = AppDatabase.getInstance(context).reservaDao()

        var reservasAnteriores = emptyMap<Long, Pair<String, String>>()

        dao.observarReservasAprobadas()
            .distinctUntilChanged()
            .collectLatest { reservas ->

                val reservasActuales = reservas.associate {
                    it.id to (it.fechaActividad to it.horaInicio)
                }

                // Cancelar recordatorios de reservas que dejaron de estar aprobadas
                (reservasAnteriores.keys - reservasActuales.keys)
                    .forEach { id ->
                        RecordatoriosReservas.cancelar(context, id)
                    }

                // Programar solamente las reservas nuevas o modificadas
                reservas.forEach { reserva ->

                    val horarioActual =
                        reserva.fechaActividad to reserva.horaInicio

                    if (reservasAnteriores[reserva.id] != horarioActual) {
                        RecordatoriosReservas.programar(context, reserva)
                    }
                }

                reservasAnteriores = reservasActuales
            }
    }
}

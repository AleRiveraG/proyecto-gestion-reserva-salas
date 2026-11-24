
package com.example.gestiondereservas.utils

import android.content.Context
import com.example.gestiondereservas.data.remote.RetrofitClient
import com.example.gestiondereservas.data.repository.ReservaRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.LocalDate
import java.time.LocalTime

object ObservadorRecordatorios {

    suspend fun observar(context: Context) {

        val repository = ReservaRepository(RetrofitClient.apiSalas, RetrofitClient.api,
            RetrofitClient.apiHorarios)

        var reservasAnteriores = emptyMap<Long, Pair<LocalDate, LocalTime>>()

        repository.observarReservasAprobadas()
            .distinctUntilChanged()
            .collectLatest { reservas ->

                val reservasActuales = reservas.associate {
                    it.idReserva to (it.fechaActividad to it.horaInicio)
                }

                val idsAprobadas = reservasActuales.keys


                // Cancelar recordatorios de reservas que dejaron de estar aprobadas
                (reservasAnteriores.keys - reservasActuales.keys)
                    .forEach { id ->
                        RecordatoriosReservas.cancelar(context, id)
                    }

                // Programar solamente las reservas nuevas o modificadas
                reservas.forEach { reserva ->

                    val horarioActual =
                        reserva.fechaActividad to reserva.horaInicio

                    if (reservasAnteriores[reserva.idReserva] != horarioActual) {
                        RecordatoriosReservas.programar(context, reserva)
                    }
                }

                reservasAnteriores = reservasActuales
            }
    }
}

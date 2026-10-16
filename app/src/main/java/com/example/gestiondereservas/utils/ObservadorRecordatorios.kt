package com.example.gestiondereservas.utils

import android.content.Context
import com.example.gestiondereservas.data.room.AppDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

object ObservadorRecordatorios {

    suspend fun observar(context: Context) {

        val dao = AppDatabase.getInstance(context).reservaDao()

        var reservasAnteriores = emptySet<Long>()

        dao.observarReservasAprobadas()
            .distinctUntilChanged()
            .collectLatest { reservas ->

                val idsActuales = reservas.map { it.id }.toSet()

                // Cancelar trabajos de reservas que ya no están aprobadas.
                (reservasAnteriores - idsActuales).forEach { id ->
                    RecordatoriosReservas.cancelar(context, id)
                }

                // Programar o actualizar los recordatorios.
                reservas.forEach { reserva ->
                    RecordatoriosReservas.programar(context, reserva)
                }

                reservasAnteriores = idsActuales
            }
    }
}
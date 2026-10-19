
package com.example.gestiondereservas.workers

import android.content.Context
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.data.room.entidades.UsoSalaEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import com.example.gestiondereservas.utils.calcularInicioReserva
import java.time.format.DateTimeFormatter

class AusenciaWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {


    override suspend fun doWork(): Result {
        val reservaId = inputData.getLong("reservaId", -1L)
        if (reservaId <= 0L) return Result.failure()

        return try {
            val db = AppDatabase.getInstance(applicationContext)

            val reserva = db.reservaDao().obtenerReservaPorId(reservaId)
                ?: return Result.success()

            if (reserva.estado != EstadoReserva.APROBADA) {
                return Result.success()
            }

            val inicio = calcularInicioReserva(
                reserva.fechaActividad,
                reserva.horaInicio
            )

            val vencimiento = inicio.plusMinutes(15)

            if (LocalDateTime.now().isBefore(vencimiento)) {
                return Result.retry()
            }

            db.withTransaction {
                val reservaActual = db.reservaDao()
                    .obtenerReservaPorId(reservaId)
                    ?: return@withTransaction

                if (reservaActual.estado != EstadoReserva.APROBADA) {
                    return@withTransaction
                }

                val uso = db.usoSalaDao().obtenerPorReserva(reservaId)

                if (uso?.estado == "EN_CURSO" ||
                    uso?.estado == "FINALIZADA"
                ) {
                    return@withTransaction
                }

                val cancelada = db.reservaDao()
                    .cancelarReservaAprobada(reservaId)

                if (cancelada > 0) {
                    db.usoSalaDao().guardar(
                        UsoSalaEntity(
                            reservaId = reservaId,
                            estado = "AUSENTE"
                        )
                    )
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

package com.example.gestiondereservas.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.EstadoReserva

class RecordatorioReservaWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val reservaId = inputData.getLong("reservaId", -1L)

            if (reservaId <= 0L) {
                return Result.failure()
            }

            val dao = AppDatabase.getInstance(applicationContext)
                .reservaDao()

            val reserva = dao.obtenerReservaPorId(reservaId)
                ?: return Result.success()

            if (reserva.estado != EstadoReserva.APROBADA) {
                return Result.success()
            }

            val titulo = inputData.getString("titulo")
                ?: "Recordatorio de reserva"

            val mensaje = inputData.getString("mensaje")
                ?: "Tu actividad comienza pronto"

            NotificacionHelper.mostrarNotificacion(
                applicationContext,
                titulo,
                mensaje
            )

            Result.success()

        } catch (e: Exception) {
            Result.retry()
        }
    }
}
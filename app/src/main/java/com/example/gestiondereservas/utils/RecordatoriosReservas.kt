package com.example.gestiondereservas.utils

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object RecordatoriosReservas {

    private fun nombreTrabajo(id: Long): String {
        return "recordatorio_reserva_$id"
    }

    fun cancelar(context: Context, reservaId: Long) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(nombreTrabajo(reservaId))
    }

    fun programar(context: Context, reserva: ReservaResponse) {

        val inicio = obtenerFechaHora(reserva)

        if (inicio == null) {
            cancelar(context, reserva.idReserva)
            return
        }

        val momentoAviso = inicio.minusHours(1)
        val ahora = LocalDateTime.now()

        if (!inicio.isAfter(ahora)) {
            cancelar(context, reserva.idReserva)
            return
        }

        // Si faltan menos de 60 minutos, avisar inmediatamente.
        val demora = if (momentoAviso.isAfter(ahora)) {
            Duration.between(ahora, momentoAviso).toMillis()
        } else {
            0L
        }

        val datos = Data.Builder()
            .putLong("reservaId", reserva.idReserva)
            .putString("titulo", if (demora == 0L) "Tu actividad comienza pronto" else "Tu actividad comienza en 1 hora")
            .putString(
                "mensaje",
                "${reserva.nombreActividad} comienza a las ${reserva.horaInicio}"
            )
            .build()

        val solicitud = OneTimeWorkRequestBuilder<RecordatorioReservaWorker>()
            .setInitialDelay(demora, TimeUnit.MILLISECONDS)
            .setInputData(datos)
            .addTag("recordatorios_reservas")
            .addTag("reserva_id_${reserva.idReserva}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            nombreTrabajo(reserva.idReserva),
            ExistingWorkPolicy.REPLACE,
            solicitud
        )
    }

    private fun obtenerFechaHora(reserva: ReservaResponse): LocalDateTime? {
        val fecha = reserva.fechaActividad
        val hora = reserva.horaInicio

        return fecha.atTime(hora)
    }
}
package com.example.gestiondereservas.utils

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

object RecordatoriosReservas {

    private fun nombreTrabajo(id: Long): String {
        return "recordatorio_reserva_$id"
    }

    fun cancelar(context: Context, reservaId: Long) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(nombreTrabajo(reservaId))
    }

    fun programar(context: Context, reserva: ReservaEntity) {

        val inicio = obtenerFechaHora(reserva)

        if (inicio == null) {
            cancelar(context, reserva.id)
            return
        }

        val momentoAviso = inicio.minusHours(1)
        val ahora = LocalDateTime.now()

        if (!momentoAviso.isAfter(ahora)) {
            cancelar(context, reserva.id)
            return
        }

        val demora = Duration.between(ahora, momentoAviso).toMillis()

        val datos = Data.Builder()
            .putLong("reservaId", reserva.id)
            .putString("titulo", "Tu actividad comienza en 1 hora")
            .putString(
                "mensaje",
                "${reserva.nombreActividad} comienza a las ${reserva.horaInicio}"
            )
            .build()

        val solicitud = OneTimeWorkRequestBuilder<RecordatorioReservaWorker>()
            .setInitialDelay(demora, TimeUnit.MILLISECONDS)
            .setInputData(datos)
            .addTag("recordatorios_reservas")
            .addTag("reserva_id_${reserva.id}")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            nombreTrabajo(reserva.id),
            ExistingWorkPolicy.REPLACE,
            solicitud
        )
    }

    private fun obtenerFechaHora(reserva: ReservaEntity): LocalDateTime? {
        return try {
            val textoFecha = reserva.fechaActividad.trim()

            val fecha = when {
                Regex("\\d{4}-\\d{2}-\\d{2}").matches(textoFecha) ->
                    LocalDate.parse(textoFecha)

                Regex("\\d{2}/\\d{2}/\\d{4}").matches(textoFecha) ->
                    LocalDate.parse(
                        textoFecha,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )

                Regex("\\d{2}/\\d{2}").matches(textoFecha) ->
                    LocalDate.parse(
                        "$textoFecha/${LocalDate.now().year}",
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )

                else -> return null
            }

            val hora = LocalTime.parse(
                reserva.horaInicio.trim(),
                DateTimeFormatter.ofPattern("H:mm")
            )

            fecha.atTime(hora)
        } catch (e: Exception) {
            null
        }
    }
}
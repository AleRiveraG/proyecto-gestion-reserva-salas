
package com.example.gestiondereservas.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun calcularInicioReserva(
    fechaActividad: String,
    horaInicio: String
): LocalDateTime {

    val hoy = LocalDate.now()
    val fechaLimpia = fechaActividad.trim()
    val fecha = if (fechaLimpia.matches(Regex("""\d{4}-\d{2}-\d{2}"""))) {
        LocalDate.parse(
            fechaLimpia,
        )
    } else {
        val fechaSinAnio = LocalDate.parse(
            "$fechaActividad/${hoy.year}",
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )

        if (fechaSinAnio.isBefore(hoy.minusMonths(6))) {
            fechaSinAnio.plusYears(1)
        } else {
            fechaSinAnio
        }
    }

    return LocalDateTime.of(
        fecha,
        LocalTime.parse(horaInicio)
    )
}

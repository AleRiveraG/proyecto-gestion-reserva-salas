
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

    val fecha = if (fechaActividad.matches(Regex("""\d{2}/\d{2}/\d{4}"""))) {
        LocalDate.parse(
            fechaActividad,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
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

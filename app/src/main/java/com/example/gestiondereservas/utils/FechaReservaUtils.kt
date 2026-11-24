
package com.example.gestiondereservas.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun calcularInicioReserva(
    fechaActividad: LocalDate,
    horaInicio: LocalTime
): LocalDateTime {
    return LocalDateTime.of(fechaActividad, horaInicio)
}

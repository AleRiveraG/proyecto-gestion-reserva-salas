package com.example.gestiondereservas.utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun formatearHora(): String {
    val hora = LocalTime.now()

    val formato = DateTimeFormatter.ofPattern("HH:mm:SS")
    return hora.format(formato)
}

fun stringALocalTime(hora: String): LocalTime {
    return LocalTime.parse(hora)
}

fun LocalTimeAString(hora: LocalTime): String {
    val formato = DateTimeFormatter.ofPattern("HH:mm:ss")
    val horaString = hora.format(formato)
    return horaString
}

fun IntALocalTime(hora: Int): LocalTime {
    val normaliza = hora.coerceIn(0, 24 * 60 - 1)
    return LocalTime.of(normaliza / 60, normaliza % 60)
}
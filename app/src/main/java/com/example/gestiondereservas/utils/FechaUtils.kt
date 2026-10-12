package com.example.gestiondereservas.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.MonthDay
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun formatearFecha(): String {
    val fecha = LocalDate.now()

    val formato = DateTimeFormatter.ofPattern("EEEE',' d 'de' MMMM")

    return fecha.format(formato).uppercase()
}

fun formatoFecha(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

fun formatearFechaPerso(fecha: String): String {
    val formatoEntrada = DateTimeFormatter.ofPattern("dd/MM")
    val diaMes = MonthDay.parse(fecha, formatoEntrada)

    val anio = Year.now().value
    val fechaCompleta = diaMes.atYear(anio)

    val formato = DateTimeFormatter.ofPattern("EEEE',' d 'de' MMMM")

    return fechaCompleta.format(formato).replaceFirstChar { it.uppercase() }
}
package com.example.gestiondereservas.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
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
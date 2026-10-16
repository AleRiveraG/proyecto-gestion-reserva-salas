package com.example.gestiondereservas.utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun formatearHora(): String {
    val hora = LocalTime.now()

    val formato = DateTimeFormatter.ofPattern("HH:mm:SS")
    return hora.format(formato)
}
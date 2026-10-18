package com.example.gestiondereservas.utils

object FormatoHora {


    fun minutos(total: Int): String {
        val horas = (total / 60).toString().padStart(2, '0')
        val minutos = (total % 60).toString().padStart(2, '0')
        return "$horas:$minutos"
    }

    fun aMinutos(hora: String): Int? {
        val partes = hora.trim().split(":")
        if (partes.size != 2) return null
        val h = partes[0].toIntOrNull() ?: return null
        val m = partes[1].toIntOrNull() ?: return null
        return if (h in 0..23 && m in 0..59) h * 60 + m else null
    }

    fun rango(inicioMin: Int, finMin: Int): String =
        "${minutos(inicioMin)} - ${minutos(finMin)}"
}

package com.example.gestiondereservas.utils

object ValidacionesSala {
    const val CAPACIDAD_MIN = 1
    const val CAPACIDAD_MAX = 200

    fun codigo(valor: String): String? =
        if (valor.isBlank()) "El código es obligatorio" else null

    fun nombre(valor: String): String? =
        if (valor.isBlank()) "El nombre es obligatorio" else null

    fun encargado(valor: String): String? =
        if (valor.isBlank()) "El encargado es obligatorio" else null

    fun capacidad(valor: Int): String? =
        if (valor !in CAPACIDAD_MIN..CAPACIDAD_MAX) {
            "La capacidad debe estar entre $CAPACIDAD_MIN y $CAPACIDAD_MAX"
        } else null

    fun equipo(nombre: String, existentes: List<String>): String? = when {
        nombre.isBlank() -> "Escribe el nombre del equipo"
        existentes.any { it.equals(nombre, ignoreCase = true) } -> "Ese equipo ya existe"
        else -> null
    }
}
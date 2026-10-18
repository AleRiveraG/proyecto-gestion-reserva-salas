package com.example.gestiondereservas.utils

object ValidacionesHorario {
    const val HORA_MIN = 8 * 60
    const val HORA_MAX = 23 * 60
    const val PASO = 5
    // Entre un bloque y el siguiente de una misma sala siempre hay 10 minutos de diferencia al igual que en nuestro horario de duoc
    const val INTERVALO_MIN = 10
    const val DURACION_MIN = 30

    fun sala(salaId: Long): String? =
        if (salaId == 0L) "Selecciona una sala" else null

    fun rango(inicioMin: Int, finMin: Int): String? = when {
        inicioMin !in HORA_MIN..HORA_MAX || finMin !in HORA_MIN..HORA_MAX ->
            "El horario debe estar entre ${FormatoHora.minutos(HORA_MIN)} y ${FormatoHora.minutos(HORA_MAX)}"
        finMin <= inicioMin ->
            "La hora de término debe ser posterior a la de inicio"
        finMin - inicioMin < DURACION_MIN ->
            "El bloque debe durar al menos $DURACION_MIN minutos"
        else -> null
    }
}

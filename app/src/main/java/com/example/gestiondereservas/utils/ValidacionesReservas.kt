package com.example.gestiondereservas.utils

import java.time.LocalDate
import java.time.LocalTime

object ValidacionesReserva {

    private val CORREO_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
    private val TEXTO_REGEX = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$".toRegex()
    private val TELEFONO_REGEX = "^[0-9]{8,10}$".toRegex()

    private val FECHASHORAS_REGEX = "^[A-Za-zÁÉÍÓÚáéíóúÑñ0-9:/ -]+$".toRegex()

    fun correoValido(correo: String): Boolean {
        return correo.isNotBlank() && CORREO_REGEX.matches(correo)
    }

    fun validarTexto(texto: String): Boolean {
        return texto.isNotBlank() && TEXTO_REGEX.matches(texto)
    }

    fun validarTelefono(telefono: String): Boolean {
        return TELEFONO_REGEX.matches(telefono)
    }

    fun validarMenu(opcion: String): Boolean {
        return opcion.isNotBlank()
    }

    fun validarMenuMultiple(opcion: Set<String>): Boolean {
        return opcion.isNotEmpty()
    }

    fun validarSala(opcion: Long): Boolean {
        return opcion != 0L
    }

    fun validarFechas(opcion: String): Boolean {
        return opcion.isNotBlank() && FECHASHORAS_REGEX.matches(opcion) && StringALocalDate(opcion) >= LocalDate.now()
    }

    fun validarHoras(opcion: String): Boolean {
        return opcion.isNotBlank() && FECHASHORAS_REGEX.matches(opcion) && stringALocalTime(opcion) > LocalTime.of(8,0) && stringALocalTime(opcion) < LocalTime.of(23,0)
    }

    fun validarMontaje(fechaMontaje: String, fechaActividad: String): Boolean {
        if(fechaMontaje.isBlank() || fechaActividad.isBlank() ) return false

        val fechaAct = StringALocalDate(fechaActividad)
        val fechaMon = StringALocalDate(fechaMontaje)

        //En caso de que dejen de un dia para otro montado t0do para la actividad
        return fechaAct >= fechaMon && fechaMon > fechaAct.minusDays(2)
    }

    fun validarHoraMontaje(fechaMontaje: String, fechaActividad: String, horaMontaje: String, horaActividad: String): Boolean {
        if(horaMontaje.isBlank() || horaActividad.isBlank()) return false

        val fechaAct = StringALocalDate(fechaActividad)
        val fechaMon = StringALocalDate(fechaMontaje)
        val horaAct = stringALocalTime(horaActividad)
        val horaMon = stringALocalTime(horaMontaje)

        return if(fechaAct == fechaMon) horaMon < horaAct else true
    }
}


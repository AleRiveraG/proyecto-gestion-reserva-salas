package com.example.gestiondereservas.utils

object ValidacionesReserva {

    private val CORREO_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
    private val TEXTO_REGEX = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$".toRegex()
    private val TELEFONO_REGEX = "^[0-9]{8,10}$".toRegex()

    fun correoValido(correo: String): Boolean {
        return correo.isNotBlank() && CORREO_REGEX.matches(correo)
    }

    fun validarTexto(texto: String): Boolean {
        return texto.isNotBlank() && TEXTO_REGEX.matches(texto)
    }

    fun validarTelefono(telefono: String): Boolean {
        return TELEFONO_REGEX.matches(telefono)
    }

}


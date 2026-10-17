package com.example.gestiondereservas.data

import com.example.gestiondereservas.data.room.entidades.DiaSemana

//datos de prueba que vienen enlazados con los codigos de las salas
object DatosPruebaHorarios {

    data class SemillaHorario(
        val codigoSala: String,
        val dia: DiaSemana,
        val inicioMin: Int,
        val finMin: Int,
        val activo: Boolean = true
    )

    private fun hora(h: Int, m: Int = 0) = h * 60 + m

    private fun bloque(
        codigo: String,
        dia: DiaSemana,
        inicio: Int,
        fin: Int,
        activo: Boolean = true
    ) = SemillaHorario(codigo, dia, inicio, fin, activo)

    val horarios = listOf(
        bloque("C204", DiaSemana.LUNES, hora(8, 30), hora(10)),
        bloque("C204", DiaSemana.LUNES, hora(10, 10), hora(11, 40)),
        bloque("C204", DiaSemana.MIERCOLES, hora(8, 30), hora(10)),
        bloque("C205", DiaSemana.MARTES, hora(14, 30), hora(16)),
        bloque("C205", DiaSemana.JUEVES, hora(14, 30), hora(16)),
        bloque("F101", DiaSemana.LUNES, hora(9), hora(12)),
        bloque("D302", DiaSemana.VIERNES, hora(8, 30), hora(10)),
        bloque("A105", DiaSemana.MARTES, hora(10, 10), hora(11, 40)),
        bloque("A105", DiaSemana.SABADO, hora(9), hora(13), activo = false)
    )
}

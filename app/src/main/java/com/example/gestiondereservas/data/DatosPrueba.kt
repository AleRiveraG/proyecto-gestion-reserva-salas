package com.example.gestiondereservas.data

import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.SalaEntity

object DatosPrueba {

    private fun sala(
        codigo: String,
        nombre: String,
        area: Area,
        capacidad: Int,
        disponibles: List<String>,
        ocupados: List<String> = emptyList()
    ): Pair<SalaEntity, List<Equipamiento>> =
        SalaEntity(
            codigo = codigo,
            nombre = nombre,
            area = area,
            encargado = area.encargadoPorDefecto,
            capacidad = capacidad
        ) to (
                disponibles.map { Equipamiento(salaId = 0, nombre = it) } +
                        ocupados.map { Equipamiento(salaId = 0, nombre = it, ocupado = true) }
                )

    val salas = listOf(
        sala("C204", "Lab. Multimedia", Area.INFORMATICA, 30,
            listOf("Proyector", "Audio", "Pizarra"), listOf("Computadores")),
        sala("C205", "Lab. Desarrollo", Area.INFORMATICA, 24,
            listOf("Proyector", "Pizarra"), listOf("Computadores")),
        sala("F101", "Estudio Fotográfico", Area.FOTOGRAFIA, 20,
            listOf("Audio")),
        sala("D302", "Taller de Diseño", Area.DISENO, 32,
            listOf("Proyector", "Audio", "Pizarra")),
        sala("A105", "Sala de Conferencias", Area.COMUNICACIONES, 45,
            listOf("Audio", "Videoconferencia"), listOf("Proyector")),
        sala("S210", "Sala Clínica", Area.SALUD, 30,
            listOf("Pizarra")),
        sala("G104", "Aula Gastronómica", Area.GASTRONOMIA, 36,
            listOf("Proyector", "Audio")),
        sala("T305", "Sala Turismo", Area.TURISMO, 18,
            listOf("Pizarra"))
    )
}
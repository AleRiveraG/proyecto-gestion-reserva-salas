package com.example.gestiondereservas.data.simulacionbd

import com.example.gestiondereservas.data.Ocupacion
import com.example.gestiondereservas.data.OcupacionFuente
import com.example.gestiondereservas.data.TipoOcupacion
import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import com.example.gestiondereservas.utils.FechasSemana

//datos de prueba. Se calculan respecto a la semana actual para que siempre se vean
// (semana 0 = esta semana, 1 = la siguiente).
class OcupacionFuentePrueba(private val salaDao: SalaDao) : OcupacionFuente {

    private class Semilla(
        val codigoSala: String,
        val semana: Int,
        val dia: DiaSemana,
        val inicioMin: Int,
        val finMin: Int,
        val tipo: TipoOcupacion,
        val titulo: String,
        val responsable: String
    )

    private fun hora(h: Int, m: Int = 0) = h * 60 + m

    private val semillas = listOf(
        Semilla("A105", 0, DiaSemana.MARTES, hora(9), hora(10),
            TipoOcupacion.MONTAJE, "Montaje: Charla de ciberseguridad", "Camila Rojas"),
        Semilla("A105", 0, DiaSemana.MARTES, hora(10, 10), hora(11, 40),
            TipoOcupacion.RESERVADO, "Charla de ciberseguridad", "Camila Rojas"),
        Semilla("C204", 0, DiaSemana.LUNES, hora(10, 10), hora(11, 40),
            TipoOcupacion.CLASE, "Programación de Apps Móviles", "Andrea Soto"),
        Semilla("C204", 0, DiaSemana.MIERCOLES, hora(8, 30), hora(10),
            TipoOcupacion.RESERVADO, "Workshop de experiencia digital", "Valentina Muñoz"),
        Semilla("C204", 1, DiaSemana.LUNES, hora(10, 10), hora(11, 40),
            TipoOcupacion.RESERVADO, "Reunión de coordinación", "Pedro Mella"),
        Semilla("C205", 0, DiaSemana.JUEVES, hora(14, 30), hora(16),
            TipoOcupacion.CLASE, "Base de Datos", "Luis Paredes")
    )

    override suspend fun obtenerPorSala(salaId: Long): List<Ocupacion> {
        val sala = salaDao.obtenerPorId(salaId)?.sala ?: return emptyList()

        return semillas
            .filter { it.codigoSala.equals(sala.codigo, ignoreCase = true) }
            .map { semilla ->
                val dias = FechasSemana.dias(semilla.semana)
                Ocupacion(
                    salaId = salaId,
                    fecha = FechasSemana.ddMM(dias[semilla.dia.ordinal]),
                    inicioMin = semilla.inicioMin,
                    finMin = semilla.finMin,
                    tipo = semilla.tipo,
                    titulo = semilla.titulo,
                    responsable = semilla.responsable
                )
            }
    }
}


package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.DatosPruebaHorarios
import com.example.gestiondereservas.data.Ocupacion
import com.example.gestiondereservas.data.OcupacionMapper
import com.example.gestiondereservas.data.TipoOcupacion
import com.example.gestiondereservas.data.room.HorarioDao
import com.example.gestiondereservas.data.room.ReservaDao
import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.room.UsoSalaDao
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import com.example.gestiondereservas.data.room.entidades.HorarioConSala
import com.example.gestiondereservas.data.room.entidades.HorarioEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.utils.ValidacionesHorario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class HorarioRepository(
    private val dao: HorarioDao,
    private val salaDao: SalaDao,
    private val reservaDao: ReservaDao,
    private val usoSalaDao: UsoSalaDao
) {

    fun observarTodos(busqueda: String): Flow<List<HorarioConSala>> =
        dao.observarTodos(busqueda.trim())

    suspend fun obtener(id: Long): HorarioConSala? =
        dao.obtenerPorId(id)

    suspend fun cambiarEstado(id: Long, activo: Boolean) =
        dao.cambiarEstado(id, activo)

    suspend fun guardar(horario: HorarioEntity): Long =
        dao.guardar(horario)

    suspend fun eliminar(id: Long) =
        dao.eliminar(id)

    suspend fun hayCruce(
        salaId: Long,
        dia: DiaSemana,
        inicioMin: Int,
        finMin: Int,
        excluirId: Long
    ): Boolean =
        dao.contarCruces(
            salaId,
            dia.name,
            inicioMin - ValidacionesHorario.INTERVALO_MIN,
            finMin + ValidacionesHorario.INTERVALO_MIN,
            excluirId
        ) > 0

    fun observarSalas(): Flow<List<SalaEntity>> =
        salaDao.observarTodas("").map { lista ->
            lista.map { it.salaEntity }
        }

    suspend fun salasDisponibles(): List<SalaEntity> =
        salaDao.observarTodas("").first().map {
            it.salaEntity
        }

    suspend fun sembrarSiVacio() {
        if (dao.contarHorarios() > 0) return

        // Los horarios dependen de las salas.
        runCatching {
            SalaRepository(salaDao).sembrarSiVacio()
        }

        val salasPorCodigo = salasDisponibles()
            .associateBy { it.codigo.lowercase() }

        DatosPruebaHorarios.horarios.forEach { semilla ->
            val sala = salasPorCodigo[semilla.codigoSala.lowercase()]
                ?: return@forEach

            dao.guardar(
                HorarioEntity(
                    salaId = sala.id,
                    dia = semilla.dia,
                    inicioMin = semilla.inicioMin,
                    finMin = semilla.finMin,
                    activo = semilla.activo
                )
            )
        }
    }

    suspend fun obtenerOcupacionesPorSala(
        salaId: Long
    ): List<Ocupacion> {

        val reservas = reservaDao.obtenerReservasPorSala(salaId)
        val lista = mutableListOf<Ocupacion>()

        reservas.forEach { reserva ->

            if (reserva.estado.name == "APROBADA") {

                val uso = usoSalaDao.obtenerPorReserva(reserva.id)
                val ocupaciones = OcupacionMapper.desdeReserva(reserva)

                if (
                    uso?.estado == "FINALIZADA" &&
                    uso.fechaHoraFin != null
                ) {
                    val horaFinReal = LocalDateTime
                        .parse(uso.fechaHoraFin)
                        .toLocalTime()
                        .toSecondOfDay() / 60

                    ocupaciones.forEach { ocupacion ->

                        if (ocupacion.tipo == TipoOcupacion.MONTAJE) {
                            lista.add(ocupacion)
                        } else if (horaFinReal > ocupacion.inicioMin) {
                            lista.add(
                                ocupacion.copy(
                                    finMin = minOf(
                                        ocupacion.finMin,
                                        horaFinReal
                                    )
                                )
                            )
                        }
                    }

                } else {
                    lista.addAll(ocupaciones)
                }
            }
        }

        return lista
    }
}


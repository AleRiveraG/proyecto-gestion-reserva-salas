
package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.Ocupacion
import com.example.gestiondereservas.data.OcupacionMapper
import com.example.gestiondereservas.data.TipoOcupacion
import com.example.gestiondereservas.data.remote.HorarioApiService
import com.example.gestiondereservas.data.remote.ReservaApiService
import com.example.gestiondereservas.data.remote.SalaApiService
import com.example.gestiondereservas.data.remote.dto.HorarioDto
import com.example.gestiondereservas.data.remote.dto.HorarioResponse
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import com.example.gestiondereservas.data.room.UsoSalaDao
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import com.example.gestiondereservas.utils.ValidacionesHorario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime

class HorarioRepository(
    private val horarioApi: HorarioApiService,
    private val salaApi: SalaApiService,
    private val reservaApi: ReservaApiService,
    private val usoSalaDao: UsoSalaDao
) {


    suspend fun obtenerHorariosPorSalaYDia(salaId: Long, dia: DiaSemana): List<HorarioResponse> =
        horarioApi.listar(salaId, dia)

    suspend fun obtener(id: Long): HorarioResponse =
        horarioApi.obtener(id)

    suspend fun cambiarEstado(id: Long, activo: Boolean) =
        horarioApi.cambiarEstado(id, activo)

    suspend fun guardar(horario: HorarioDto): HorarioResponse =
        horarioApi.crear(horario)

    suspend fun eliminar(id: Long) =
        horarioApi.eliminar(id)


    suspend fun hayCruce(
        salaId: Long,
        dia: DiaSemana,
        inicioMin: Int,
        finMin: Int,
        excluirId: Long
    ): Boolean {
        val margen = ValidacionesHorario.INTERVALO_MIN
        val nuevoInicio = inicioMin - margen
        val nuevoFin = finMin + margen

        val horariosDia = horarioApi.listar(salaId, dia)

        return horariosDia.any { existente ->
            existente.id != excluirId &&
                    nuevoInicio < existente.horaInicio.toSecondOfDay() / 60 &&
                    nuevoFin > existente.horaTermino.toSecondOfDay() / 60
        }
    }

    fun observarSalas(): Flow<List<SalaResponse>> = flow {
        emit(salaApi.listar().filter { it.activa })
    }


    suspend fun salasDisponibles(): List<SalaResponse> =
        salaApi.listar().filter { it.activa }

    suspend fun obtenerOcupacionesPorSala(
        salaId: Long
    ): List<Ocupacion> {

        val reservas = reservaApi.obtenerReservasPorSala(salaId)
        val lista = mutableListOf<Ocupacion>()

        reservas.forEach { reserva ->

            if (reserva.estado.name == "APROBADA") {

                val uso = usoSalaDao.obtenerPorReserva(reserva.idReserva)
                val ocupaciones = OcupacionMapper.desdeReserva(reserva)

                if (
                    uso?.estado == "FINALIZADA" &&
                    uso.fechaHoraFin != null
                ) {
                    val horaFinReal = LocalDateTime
                        .parse(uso.fechaHoraFin)
                        .toLocalTime()

                    ocupaciones.forEach { ocupacion ->

                        if (ocupacion.tipo == TipoOcupacion.MONTAJE) {
                            lista.add(ocupacion)
                        } else if (horaFinReal.isAfter(ocupacion.inicioMin)) {
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


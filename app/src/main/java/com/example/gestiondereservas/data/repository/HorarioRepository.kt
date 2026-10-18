package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.DatosPruebaHorarios
import com.example.gestiondereservas.data.Ocupacion
import com.example.gestiondereservas.data.OcupacionMapper
import com.example.gestiondereservas.data.room.HorarioDao
import com.example.gestiondereservas.data.room.ReservaDao
import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import com.example.gestiondereservas.data.room.entidades.HorarioConSala
import com.example.gestiondereservas.data.room.entidades.HorarioEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.utils.ValidacionesHorario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class HorarioRepository(
    private val dao: HorarioDao,
    private val salaDao: SalaDao,
    private val reservaDao: ReservaDao
) {
    fun observarTodos(busqueda: String): Flow<List<HorarioConSala>> =
        dao.observarTodos(busqueda.trim())

    suspend fun obtener(id: Long): HorarioConSala? = dao.obtenerPorId(id)

    suspend fun cambiarEstado(id: Long, activo: Boolean) =
        dao.cambiarEstado(id, activo)

    suspend fun guardar(horario: HorarioEntity): Long = dao.guardar(horario)

    suspend fun eliminar(id: Long) = dao.eliminar(id)

    suspend fun hayCruce(
        salaId: Long,
        dia: DiaSemana,
        inicioMin: Int,
        finMin: Int,
        excluirId: Long
    ): Boolean =
    // Se amplía el bloque con el intervalo mínimo: así también cuenta como cruce
        // quedar a menos de 10 minutos de otro bloque.
        dao.contarCruces(
            salaId,
            dia.name,
            inicioMin - ValidacionesHorario.INTERVALO_MIN,
            finMin + ValidacionesHorario.INTERVALO_MIN,
            excluirId
        ) > 0

    fun observarSalas(): Flow<List<SalaEntity>> =
        salaDao.observarTodas("").map { lista -> lista.map { it.salaEntity } }

    suspend fun salasDisponibles(): List<SalaEntity> =
        salaDao.observarTodas("").first().map { it.salaEntity }

    suspend fun sembrarSiVacio() {
        if (dao.contarHorarios() > 0) return

        // Los horarios dependen de las salas, así que primero deben existir.
        runCatching { SalaRepository(salaDao).sembrarSiVacio() }

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

    suspend fun obtenerOcupacionesPorSala(salaId: Long): List<Ocupacion> {
        val reservas = reservaDao.obtenerReservasPorSala(salaId)
        val lista = mutableListOf<Ocupacion>()

        reservas.forEach { reserva ->
            lista.addAll(OcupacionMapper.desdeReserva(reserva))
        }
        return lista
    }
}

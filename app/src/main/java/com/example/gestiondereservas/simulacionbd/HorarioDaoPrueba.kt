package com.example.gestiondereservas.data.simulacionbd

import com.example.gestiondereservas.data.room.HorarioDao
import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.room.entidades.Horario
import com.example.gestiondereservas.data.room.entidades.HorarioConSala
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

// Simula los horarios en memoria para integrar la base centralizada después.
// Necesita el SalaDao para poder armar HorarioConSala que es la relacion con sala
class HorarioDaoPrueba(private val salaDao: SalaDao) : HorarioDao {

    private val tablaHorarios = MutableStateFlow<List<Horario>>(emptyList())
    private var siguienteId = 1L

    override fun observarTodos(
        busqueda: String
    ): Flow<List<HorarioConSala>> =
        combine(tablaHorarios, salaDao.observarTodas("")) { horarios, salas ->
            val salasPorId = salas.associate { it.sala.id to it.sala }

            horarios
                .mapNotNull { horario ->
                    val sala = salasPorId[horario.salaId]
                        ?: return@mapNotNull null
                    HorarioConSala(horario, sala)
                }
                .filter { item ->
                    item.sala.codigo.contains(busqueda, ignoreCase = true) ||
                            item.sala.nombre.contains(busqueda, ignoreCase = true)
                }
        }

    override suspend fun obtenerPorId(id: Long): HorarioConSala? {
        val horario = tablaHorarios.value.firstOrNull { it.id == id }
            ?: return null
        val sala = salaDao.obtenerPorId(horario.salaId)?.sala
            ?: return null

        return HorarioConSala(horario, sala)
    }

    override suspend fun contarCruces(
        salaId: Long,
        dia: String,
        inicioMin: Int,
        finMin: Int,
        excluirId: Long
    ): Int =
        tablaHorarios.value.count {
            it.salaId == salaId &&
                    it.dia.name == dia &&
                    it.id != excluirId &&
                    it.inicioMin < finMin &&
                    it.finMin > inicioMin
        }

    override suspend fun cambiarEstado(id: Long, activo: Boolean) {
        tablaHorarios.update { horarios ->
            horarios.map { horario ->
                if (horario.id == id) horario.copy(activo = activo) else horario
            }
        }
    }

    override suspend fun eliminar(id: Long) {
        tablaHorarios.update { horarios ->
            horarios.filter { it.id != id }
        }
    }

    override suspend fun contarHorarios(): Int =
        tablaHorarios.value.size

    override suspend fun insertarHorario(horario: Horario): Long {
        val id = siguienteId++
        tablaHorarios.update { horarios -> horarios + horario.copy(id = id) }
        return id
    }

    override suspend fun actualizarHorario(horario: Horario) {
        tablaHorarios.update { horarios ->
            horarios.map { existente ->
                if (existente.id == horario.id) horario else existente
            }
        }
    }
}

package com.example.gestiondereservas.data.simulacionbd


import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.data.room.entidades.SalaConEquipamiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

// Simula salas y equipamientos en memoria para integrar la base centralizada después.
class SalaDaoPrueba : SalaDao {

    private val tablaSalas = MutableStateFlow<List<Sala>>(emptyList())
    private val tablaEquipos = MutableStateFlow<List<Equipamiento>>(emptyList())

    private var siguienteIdSala = 1L
    private var siguienteIdEquipo = 1L

    override fun observarTodas(
        busqueda: String
    ): Flow<List<SalaConEquipamiento>> =
        combine(tablaSalas, tablaEquipos) { salas, equipos ->
            salas
                .filter { sala ->
                    sala.codigo.contains(busqueda, ignoreCase = true) ||
                            sala.nombre.contains(busqueda, ignoreCase = true)
                }
                .sortedBy { it.codigo.lowercase() }
                .map { sala ->
                    SalaConEquipamiento(
                        sala,
                        equipos.filter { it.salaId == sala.id }
                    )
                }
        }

    override suspend fun obtenerPorId(id: Long): SalaConEquipamiento? {
        val sala = tablaSalas.value.firstOrNull { it.id == id }
            ?: return null

        return SalaConEquipamiento(
            sala,
            tablaEquipos.value.filter { it.salaId == id }
        )
    }

    override suspend fun contarCodigo(
        codigo: String,
        excluirId: Long
    ): Int =
        tablaSalas.value.count { sala ->
            sala.codigo.equals(codigo, ignoreCase = true) &&
                    sala.id != excluirId
        }

    override suspend fun contarSalas(): Int =
        tablaSalas.value.size

    override suspend fun cambiarEstado(id: Long, activa: Boolean) {
        tablaSalas.update { salas ->
            salas.map { sala ->
                if (sala.id == id) sala.copy(activa = activa) else sala
            }
        }
    }

    override suspend fun insertarSala(sala: Sala): Long {
        check(
            tablaSalas.value.none {
                it.codigo.equals(sala.codigo, ignoreCase = true)
            }
        ) {
            "Ya existe una sala con ese código"
        }

        val id = siguienteIdSala++
        tablaSalas.update { salas -> salas + sala.copy(id = id) }
        return id
    }

    override suspend fun actualizarSala(sala: Sala) {
        check(
            tablaSalas.value.none {
                it.id != sala.id &&
                        it.codigo.equals(sala.codigo, ignoreCase = true)
            }
        ) {
            "Ya existe una sala con ese código"
        }

        tablaSalas.update { salas ->
            salas.map { existente ->
                if (existente.id == sala.id) sala else existente
            }
        }
    }

    override suspend fun borrarEquipamientos(salaId: Long) {
        tablaEquipos.update { equipos ->
            equipos.filter { it.salaId != salaId }
        }
    }

    override suspend fun insertarEquipamientos(
        equipos: List<Equipamiento>
    ) {
        tablaEquipos.update { existentes ->
            existentes + equipos.map { equipo ->
                equipo.copy(id = siguienteIdEquipo++)
            }
        }
    }
}

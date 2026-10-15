package com.example.gestiondereservas.data.simulacionbd

import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.data.room.entidades.SalaConEquipamiento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update


//esto es para simular salas equipamientos y demas cosas
//esta pensando asi para despues solo tener una bd centralizada
class SalaDaoPrueba : SalaDao {

    private val tablaSalas = MutableStateFlow<List<SalaEntity>>(emptyList())
    private val tablaEquipos = MutableStateFlow<List<Equipamiento>>(emptyList())
    private var siguienteIdSala = 1L
    private var siguienteIdEquipo = 1L

    override fun observarPorArea(area: Area, busqueda: String): Flow<List<SalaConEquipamiento>> =
        combine(tablaSalas, tablaEquipos) { salas, equipos ->
            salas
                .filter {
                    it.area == area &&
                            (it.codigo.contains(busqueda, ignoreCase = true) ||
                                    it.nombre.contains(busqueda, ignoreCase = true))
                }
                .sortedBy { it.codigo.lowercase() }
                .map { sala ->
                    SalaConEquipamiento(sala, equipos.filter { it.salaId == sala.id })
                }
        }

    override suspend fun obtenerPorId(id: Long): SalaConEquipamiento? {
        val sala = tablaSalas.value.firstOrNull { it.id == id } ?: return null
        return SalaConEquipamiento(sala, tablaEquipos.value.filter { it.salaId == id })
    }

    override suspend fun contarCodigo(codigo: String, excluirId: Long): Int =
        tablaSalas.value.count { it.codigo.equals(codigo, ignoreCase = true) && it.id != excluirId }

    override suspend fun contarSalas(): Int = tablaSalas.value.size
    override suspend fun obtenerSalas(): List<SalaEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun obtenerSalaPorId(id: Long): SalaEntity {
        TODO("Not yet implemented")
    }

    override suspend fun cambiarEstado(id: Long, activa: Boolean) {
        tablaSalas.update { lista ->
            lista.map { if (it.id == id) it.copy(activa = activa) else it }
        }
    }

    override suspend fun insertarSala(salaEntity: SalaEntity): Long {
        check(tablaSalas.value.none { it.codigo.equals(salaEntity.codigo, ignoreCase = true) }) {
            "UNIQUE constraint failed: salas.codigo"
        }
        val id = siguienteIdSala++
        tablaSalas.update { it + salaEntity.copy(id = id) }
        return id
    }

    override suspend fun actualizarSala(salaEntity: SalaEntity) {
        check(
            tablaSalas.value.none {
                it.id != salaEntity.id && it.codigo.equals(salaEntity.codigo, ignoreCase = true)
            }
        ) { "UNIQUE constraint failed: salas.codigo" }
        tablaSalas.update { lista -> lista.map { if (it.id == salaEntity.id) salaEntity else it } }
    }

    override suspend fun borrarEquipamientos(salaId: Long) {
        tablaEquipos.update { lista -> lista.filter { it.salaId != salaId } }
    }

    override suspend fun insertarEquipamientos(equipos: List<Equipamiento>) {
        tablaEquipos.update { actual ->
            actual + equipos.map { it.copy(id = siguienteIdEquipo++) }
        }
    }
}
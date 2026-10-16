package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.data.room.entidades.SalaConEquipamiento
import kotlinx.coroutines.flow.Flow
import com.example.gestiondereservas.data.DatosPrueba

class SalaRepository(private val dao: SalaDao) {

    fun observarPorArea(area: Area, busqueda: String): Flow<List<SalaConEquipamiento>> =
        dao.observarPorArea(area, busqueda.trim())

    suspend fun obtener(id: Long): SalaConEquipamiento? = dao.obtenerPorId(id)

    suspend fun cambiarEstado(id: Long, activa: Boolean) = dao.cambiarEstado(id, activa)

    suspend fun guardar(salaEntity: SalaEntity, equipos: List<Equipamiento>): Long =
        dao.guardar(salaEntity, equipos)

    suspend fun codigoDisponible(codigo: String, excluirId: Long): Boolean =
        dao.contarCodigo(codigo.trim(), excluirId) == 0

    suspend fun sembrarSiVacio() {
        if (dao.contarSalas() > 0) return
        DatosPrueba.salas.forEach { (sala, equipos) -> dao.guardar(sala, equipos) }
    }
}
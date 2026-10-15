package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import com.example.gestiondereservas.data.room.entidades.SalaConEquipamiento
import kotlinx.coroutines.flow.Flow

@Dao
interface SalaDao {

    @Transaction
    @Query(
        "SELECT * FROM salas WHERE area = :area " +
                "AND (codigo LIKE '%' || :busqueda || '%' OR nombre LIKE '%' || :busqueda || '%') " +
                "ORDER BY codigo"
    )
    fun observarPorArea(area: Area, busqueda: String): Flow<List<SalaConEquipamiento>>

    @Transaction
    @Query("SELECT * FROM salas WHERE id = :id")
    suspend fun obtenerPorId(id: Long): SalaConEquipamiento?

    @Query("SELECT COUNT(*) FROM salas WHERE codigo = :codigo AND id != :excluirId")
    suspend fun contarCodigo(codigo: String, excluirId: Long): Int

    @Query("UPDATE salas SET activa = :activa WHERE id = :id")
    suspend fun cambiarEstado(id: Long, activa: Boolean)

    @Insert
    suspend fun insertarSala(salaEntity: SalaEntity): Long

    @Update
    suspend fun actualizarSala(salaEntity: SalaEntity)

    @Query("DELETE FROM equipamientos WHERE salaId = :salaId")
    suspend fun borrarEquipamientos(salaId: Long)

    @Insert
    suspend fun insertarEquipamientos(equipos: List<Equipamiento>)

    @Query("SELECT COUNT(*) FROM salas")
    suspend fun contarSalas(): Int

    @Transaction
    suspend fun guardar(salaEntity: SalaEntity, equipos: List<Equipamiento>): Long {
        val id = if (salaEntity.id == 0L) {
            insertarSala(salaEntity)
        } else {
            actualizarSala(salaEntity)
            salaEntity.id
        }
        borrarEquipamientos(id)
        insertarEquipamientos(equipos.map { it.copy(id = 0, salaId = id) })
        return id
    }

    @Query("SELECT * FROM salas")
    suspend fun obtenerSalas(): List<SalaEntity>

    @Query("SELECT * FROM salas WHERE id = :id")
    suspend fun obtenerSalaPorId(id: Long): SalaEntity

}
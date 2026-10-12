package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gestiondereservas.data.room.entidades.Area
import com.example.gestiondereservas.data.room.entidades.Equipamiento
import com.example.gestiondereservas.data.room.entidades.Sala
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
    suspend fun insertarSala(sala: Sala): Long

    @Update
    suspend fun actualizarSala(sala: Sala)

    @Query("DELETE FROM equipamientos WHERE salaId = :salaId")
    suspend fun borrarEquipamientos(salaId: Long)

    @Insert
    suspend fun insertarEquipamientos(equipos: List<Equipamiento>)

    @Query("SELECT COUNT(*) FROM salas")
    suspend fun contarSalas(): Int

    @Transaction
    //aqui si algo falla por x motivo, la sala
    suspend fun guardar(sala: Sala, equipos: List<Equipamiento>): Long {
        val id = if (sala.id == 0L) {
            insertarSala(sala)
        } else {
            actualizarSala(sala)
            sala.id
        }
        borrarEquipamientos(id)
        insertarEquipamientos(equipos.map { it.copy(id = 0, salaId = id) })
        return id
    }
}
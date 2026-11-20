package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.gestiondereservas.data.room.entidades.HorarioConSala
import com.example.gestiondereservas.data.room.entidades.HorarioEntity
import com.example.gestiondereservas.utils.ValidacionesHorario
import kotlinx.coroutines.flow.Flow

@Dao
interface HorarioDao {
    @Transaction
    @Query(
        "SELECT h.* FROM horarios h " +
                "INNER JOIN salas s ON s.id = h.salaId " +
                "WHERE (s.codigo LIKE '%' || :busqueda || '%' " +
                "OR s.nombre LIKE '%' || :busqueda || '%') " +
                "ORDER BY s.codigo, h.inicioMin"
    )
    fun observarTodos(busqueda: String): Flow<List<HorarioConSala>>

    @Transaction
    @Query("SELECT * FROM horarios WHERE id = :id")
    suspend fun obtenerPorId(id: Long): HorarioConSala?

    //los bloques se cruzan cuando uno empieza antes de que el otro termine
    @Query(
        "SELECT COUNT(*) FROM horarios " +
                "WHERE salaId = :salaId AND dia = :dia AND id != :excluirId " +
                "AND inicioMin < :finMin AND finMin > :inicioMin"
    )
    suspend fun contarCruces(
        salaId: Long,
        dia: String,
        inicioMin: Int,
        finMin: Int,
        excluirId: Long
    ): Int

    @Query("UPDATE horarios SET activo = :activo WHERE id = :id")
    suspend fun cambiarEstado(id: Long, activo: Boolean)

    @Query("DELETE FROM horarios WHERE id = :id")
    suspend fun eliminar(id: Long)

    @Query("SELECT COUNT(*) FROM horarios")
    suspend fun contarHorarios(): Int

    @Insert
    suspend fun insertarHorario(horario: HorarioEntity): Long

    @Update
    suspend fun actualizarHorario(horario: HorarioEntity)

    @Transaction
    suspend fun guardar(horario: HorarioEntity): Long {
        check(
            contarCruces(
                horario.salaId,
                horario.dia.name,
                horario.inicioMin - ValidacionesHorario.INTERVALO_MIN,
                horario.finMin + ValidacionesHorario.INTERVALO_MIN,
                horario.id
            ) == 0
        ) {
            "El horario se cruza con otro bloque de la sala o no deja el intervalo mínimo"
        }

        return if (horario.id == 0L) {
            insertarHorario(horario)
        } else {
            actualizarHorario(horario)
            horario.id
        }
    }
}

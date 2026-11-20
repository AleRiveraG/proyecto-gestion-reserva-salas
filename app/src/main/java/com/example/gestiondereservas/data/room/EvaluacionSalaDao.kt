package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gestiondereservas.data.room.entidades.EvaluacionSalaEntity

@Dao
interface EvaluacionSalaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(evaluacion: EvaluacionSalaEntity)

    @Query("SELECT * FROM evaluacion_sala WHERE reservaId = :reservaId LIMIT 1")
    suspend fun obtenerPorReserva(reservaId: Long): EvaluacionSalaEntity?

    @Query("SELECT * FROM evaluacion_sala ORDER BY fechaEvaluacion DESC")
    suspend fun obtenerTodas(): List<EvaluacionSalaEntity>
}

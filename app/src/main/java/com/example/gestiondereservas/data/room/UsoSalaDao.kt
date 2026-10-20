
package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gestiondereservas.data.room.entidades.UsoSalaEntity

@Dao
interface UsoSalaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(uso: UsoSalaEntity)

    @Query("SELECT * FROM uso_sala WHERE reservaId = :reservaId LIMIT 1")
    suspend fun obtenerPorReserva(reservaId: Long): UsoSalaEntity?

    @Query("""
        UPDATE uso_sala
        SET estado = 'FINALIZADA',
            fechaHoraFin = :fechaHoraFin
        WHERE reservaId = :reservaId
          AND estado = 'EN_CURSO'
    """)
    suspend fun finalizar(
        reservaId: Long,
        fechaHoraFin: String
    ): Int

    @Query
        ("""
    SELECT * FROM uso_sala
    WHERE reservaId = :reservaId
    LIMIT 1
    """)
    suspend fun consultarCheckIn(reservaId: Long): UsoSalaEntity?
}

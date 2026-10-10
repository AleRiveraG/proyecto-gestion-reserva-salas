package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReservaDao {

    @Insert
    suspend fun registrar(reserva: ReservaEntity)

    @Query("SELECT * FROM reserva")
    suspend fun obtenerReservas(): List<ReservaEntity>
}
package com.example.gestiondereservas.data

import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.ReservaEntity

class AppState(private val db: AppDatabase) {

    suspend fun registrarReserva(reserva: ReservaEntity) {
        db.reservaDao().registrar(reserva)
    }
}

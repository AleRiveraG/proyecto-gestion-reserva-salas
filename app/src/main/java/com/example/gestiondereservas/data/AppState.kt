package com.example.gestiondereservas.data

import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.EstadoReserva
import com.example.gestiondereservas.data.room.ReservaEntity

class AppState(private val db: AppDatabase) {

    suspend fun registrarReserva(reserva: ReservaEntity) {
        db.reservaDao().registrar(reserva)
    }

    suspend fun consultarReservas(): List<ReservaEntity>{
        return db.reservaDao().obtenerReservasPendientes()
    }

    suspend fun consultarPendientes(): Int {
        return db.reservaDao().obtenerPendientes()
    }

    suspend fun consultarAprobadas(): Int {
        return db.reservaDao().obtenerAprobadas()
    }

    suspend fun consultarRechazadas(): Int {
        return db.reservaDao().obtenerRechazadas()
    }

    suspend fun cambiarEstado(id: Long, estado: String) {
        return db.reservaDao().modificarEstado(id, estado)
    }


}

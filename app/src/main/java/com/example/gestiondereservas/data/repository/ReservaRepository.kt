package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.room.ReservaDao
import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity

class ReservaRepository(
    private val reservaDao: ReservaDao,
    private val salaDao: SalaDao
) {
    suspend fun registrarReserva(reserva: ReservaEntity) {
        reservaDao.registrar(reserva)
    }

    suspend fun modificarEstado(id: Long, estadoNuevo: String) {
        reservaDao.modificarEstado(id, estadoNuevo)
    }

    suspend fun obtenerReservasPendientes(): List<ReservaEntity> {
        return reservaDao.obtenerReservasPendientes()
    }

    suspend fun obtenerAprobadas(): Int {
        return reservaDao.obtenerAprobadas()
    }

    suspend fun obtenerRechazadas(): Int {
        return reservaDao.obtenerRechazadas()
    }

    suspend fun obtenerPendiente(): Int {
        return reservaDao.obtenerPendientes()
    }

    suspend fun obtenerSalas(): List<SalaEntity> {
        return salaDao.obtenerSalas()
    }

    suspend fun obtenerSalaPorId(id: Long): SalaEntity{
        return salaDao.obtenerSalaPorId(id)
    }

}
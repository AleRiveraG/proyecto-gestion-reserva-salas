package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.remote.ReservaApiService
import com.example.gestiondereservas.data.remote.SalaApiService
import com.example.gestiondereservas.data.remote.dto.DocumentoResponse
import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import com.example.gestiondereservas.data.room.ReservaDao
import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ReservaRepository(
    private val salaApi: SalaApiService,
    private val api: ReservaApiService
) {
    suspend fun registrarReserva(reserva: RequestBody, archivo: MultipartBody.Part): ReservaResponse {
        val reservaRespuesta = api.registrarReserva(reserva, archivo)
        return reservaRespuesta
    }

    suspend fun modificarEstado(id: Long, estadoNuevo: EstadoReserva, estadoActual: EstadoReserva) {
        api.modificarEstadoReserva(id, estadoNuevo, estadoActual)
    }

    suspend fun obtenerReservasPendientes(): List<ReservaResponse> {
        val reservas = api.obtenerReservasPorEstado(EstadoReserva.PENDIENTE)
        return reservas
    }

    suspend fun obtenerAprobadas(): Long {
        val cantidad = api.obtenerCantidadPorEstado(EstadoReserva.APROBADA)
        return cantidad
    }

    suspend fun obtenerRechazadas(): Long {
        val cantidad = api.obtenerCantidadPorEstado(EstadoReserva.RECHAZADA)
        return cantidad
    }

    suspend fun obtenerPendiente(): Long {
        val cantidad = api.obtenerCantidadPorEstado(EstadoReserva.PENDIENTE)
        return cantidad
    }

    suspend fun obtenerSalas(): List<SalaResponse> {
        return salaApi.listar()
    }

    suspend fun obtenerSalaPorId(id: Long): SalaResponse? {
        return salaApi.obtener(id)
    }

    suspend fun obtenerReservaPorIdUsuario(id: Long): List<ReservaResponse> {
        val reservas = api.obtenerReservasPorUsuario(id)
        return reservas
    }

    suspend fun obtenerDocumento(idReserva: Long): String {
        val documento = api.obtenerDocumento(idReserva)
        return documento
    }
}
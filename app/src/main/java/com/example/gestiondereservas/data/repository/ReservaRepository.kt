package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.remote.HorarioApiService
import com.example.gestiondereservas.data.remote.ReservaApiService
import com.example.gestiondereservas.data.remote.SalaApiService
import com.example.gestiondereservas.data.remote.dto.DocumentoResponse
import com.example.gestiondereservas.data.remote.dto.ReporteResponse
import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import com.example.gestiondereservas.data.room.ReservaDao
import com.example.gestiondereservas.data.room.SalaDao
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.time.LocalDate
import java.time.LocalTime

class ReservaRepository(
    private val salaApi: SalaApiService,
    private val api: ReservaApiService,
    private val horarioApi: HorarioApiService
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

    suspend fun obtenerReservasAprobadas(): List<ReservaResponse> {
        val reservas = api.obtenerReservasPorEstado(EstadoReserva.APROBADA)
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

    suspend fun horarioDisponible(
        salaId: Long,
        fecha: LocalDate,
        horaInicio: LocalTime,
        horaTermino: LocalTime
    ): Boolean {
        return horarioApi.disponibilidad(salaId, fecha, horaInicio, horaTermino)
    }

    suspend fun crucesReservas(
        salaId: Long,
        fecha: LocalDate,
        horaInicio: LocalTime,
        horaTermino: LocalTime
    ): Boolean {
        val aprobadas = api.obtenerReservasPorEstado(EstadoReserva.APROBADA)
        val pendientes = api.obtenerReservasPorEstado(EstadoReserva.PENDIENTE)

        val candidatas = (aprobadas + pendientes).distinctBy { it.idReserva }

        return candidatas.any { reserva ->
            val mismaSala = reserva.salaId == salaId
            val mismaFecha = reserva.fechaActividad == fecha
            val seCruza = reserva.horaInicio.isBefore(horaTermino) &&
                    reserva.horaTermino.isAfter(horaInicio)

            mismaSala && mismaFecha && seCruza
        }

    }

    suspend fun obtenerReservaPorId(idReserva: Long): ReservaResponse {
        return api.obtenerReservaPorId(idReserva)
    }

    suspend fun contarReservasGeneral(tiempo: String): ReporteResponse {
        return api.contarReservasGeneral(tiempo)
    }

    suspend fun contarReservasSala(salaId: Long, tiempo: String): ReporteResponse {
        return api.contarReservasSala(salaId, tiempo)
    }
}
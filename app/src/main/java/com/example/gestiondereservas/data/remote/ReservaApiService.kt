package com.example.gestiondereservas.data.remote

import com.example.gestiondereservas.data.remote.dto.ReporteResponse
import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ReservaApiService {
    @GET("api/reservas/{estado}")
    suspend fun obtenerReservasPorEstado(
        @Path("estado") estado: EstadoReserva
    ): List<ReservaResponse>

    @GET("api/reservas/cantidad/{estado}")
    suspend fun obtenerCantidadPorEstado(
        @Path("estado") estado: EstadoReserva
    ): Long

    @GET("api/reservas/sala/{salaId}")
    suspend fun obtenerReservasPorSala(
        @Path("salaId") salaId: Long
    ): List<ReservaResponse>

    @GET("api/reservas/usuario/{idUsuario}")
    suspend fun obtenerReservasPorUsuario(
        @Path("idUsuario") idUsuario: Long
    ): List<ReservaResponse>

    @GET("api/reservas/{idUsuario}/{estado}")
    suspend fun obtenerReservasPorUsuarioYEstado(
        @Path("idUsuario") idUsuario: Long,
        @Path("estado") estado: EstadoReserva
    ): List<ReservaResponse>

    @Multipart
    @POST("api/reservas")
    suspend fun registrarReserva(
        @Part("reserva") reserva: RequestBody,
        @Part archivo: MultipartBody.Part
    ): ReservaResponse

    @PATCH("api/reservas/{idReserva}/{estado}/{estadoActual}")
    suspend fun modificarEstadoReserva(
        @Path("idReserva") idReserva: Long,
        @Path("estado") estado: EstadoReserva,
        @Path("estadoActual") estadoActual: EstadoReserva
    )

    @GET("api/reservas/{idReserva}/documento")
    suspend fun obtenerDocumento(
        @Path("idReserva") idReserva: Long
    ): String

    @GET("api/reservas/reserva/{idReserva}")
    suspend fun obtenerReservaPorId(
        @Path("idReserva") idReserva: Long
    ): ReservaResponse

    @GET("api/reservas/reserva")
    suspend fun contarReservasGeneral(
        @Query("tiempo") tiempo: String
    ): ReporteResponse

    @GET("api/reservas/reserva/sala/{salaId}")
    suspend fun contarReservasSala(
        @Path("salaId") salaId: Long,
        @Query("tiempo") tiempo: String
    ): ReporteResponse

}
package com.example.gestiondereservas.data.remote

import com.example.gestiondereservas.data.remote.dto.HorarioDto
import com.example.gestiondereservas.data.remote.dto.HorarioResponse
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate
import java.time.LocalTime

interface HorarioApiService {

    @GET("api/horarios/disponibilidad")
    suspend fun disponibilidad(
        @Query("salaId") salaId: Long,
        @Query("fecha") fecha: LocalDate,
        @Query("horaInicio") horaInicio: LocalTime,
        @Query("horaTermino") horaTermino: LocalTime,
    ): Boolean

    @GET("api/horarios")
    suspend fun listar(
        @Query("salaId") salaId: Long,
        @Query("dia") dia: DiaSemana,
    ): List<HorarioResponse>

    @GET("api/horarios/{id}")
    suspend fun obtener(
        @Path("id") horarioId: Long
    ): HorarioResponse

    @POST("api/horarios")
    suspend fun crear(
        @Body horarioDto: HorarioDto
    ): HorarioResponse

    @PATCH("api/horarios/{id}/estado")
    suspend fun cambiarEstado(
        @Path("id") horarioId: Long,
        @Query("activa") activa: Boolean
    ): HorarioResponse

    @DELETE("api/horarios/{id}")
    suspend fun eliminar(
        @Path("id") horarioId: Long
    )
}
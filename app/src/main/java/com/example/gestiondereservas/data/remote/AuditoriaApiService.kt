package com.example.gestiondereservas.data.remote

import com.example.gestiondereservas.data.remote.dto.AuditoriaDto
import com.example.gestiondereservas.data.remote.dto.AuditoriaResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.time.LocalDate

interface AuditoriaApiService {

    @GET("api/auditoria")
    suspend fun obtenerAuditorias(): List<AuditoriaResponse>

    @POST("api/auditoria")
    suspend fun registrarAuditoria(
        @Body auditoria: AuditoriaDto
    ): AuditoriaResponse

    @GET("api/auditoria/tipo/{tipo}")
    suspend fun obtenerPorTipo(
        @Path("tipo") tipo: String
    ): List<AuditoriaResponse>

    @GET("api/auditoria/fecha/{fecha}")
    suspend fun obtenerPorFecha(
        @Path("fecha") fecha: LocalDate
    ): List<AuditoriaResponse>
}
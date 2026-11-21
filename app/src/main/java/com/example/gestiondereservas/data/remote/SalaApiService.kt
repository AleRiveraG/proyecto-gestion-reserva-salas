package com.example.gestiondereservas.data.remote


import com.example.gestiondereservas.data.remote.dto.SalaDto
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SalaApiService {

    @GET("api/salas")
    suspend fun listar(): List<SalaResponse>

    @GET("api/salas/{id}")
    suspend fun obtener(
        @Path("id") id: Long
    ): SalaResponse

    @GET("api/salas/codigo-disponible")
    suspend fun codigoDisponible(
        @Query("codigo") codigo: String,
        @Query("excluirId") excluirId: Long
    ): Boolean

    @POST("api/salas")
    suspend fun crear(
        @Body dto: SalaDto
    ): SalaResponse

    @PUT("api/salas/{id}")
    suspend fun actualizar(
        @Path("id") id: Long,
        @Body dto: SalaDto
    ): SalaResponse

    @PATCH("api/salas/{id}/estado")
    suspend fun cambiarEstado(
        @Path("id") id: Long,
        @Query("activa") activa: Boolean
    ): SalaResponse
}
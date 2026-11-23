package com.example.gestiondereservas.data.remote

import com.example.gestiondereservas.data.remote.dto.ActualizarUsuarioRequestDto
import com.example.gestiondereservas.data.remote.dto.AuthResponseDto
import com.example.gestiondereservas.data.remote.dto.LoginRequestDto
import com.example.gestiondereservas.data.remote.dto.MensajeResponseDto
import com.example.gestiondereservas.data.remote.dto.RegistroRequestDto
import com.example.gestiondereservas.data.remote.dto.UsuarioResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 Interfaz de Retrofit que define todos los endpoints del microservicio de login (puerto 8081):
 Las rutas NO empiezan con "/" porque BASE_URL en el cliente Retrofit terminará en "/".
 */
interface AutenticacionApiService {

    //=========================================================================
    //Endpoints públicos (/api/auth) 
    //=========================================================================

    @POST("api/auth/register")
    suspend fun registrar(
        @Body request: RegistroRequestDto
    ): Response<AuthResponseDto>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<AuthResponseDto>

    //=========================================================================
    //Endpoints protegidos (/api/usuarios) - Requieren Header "Authorization: Bearer <token>"
    //=========================================================================

    @GET("api/usuarios")
    suspend fun listarUsuarios(
        @Header("Authorization") bearerToken: String
    ): Response<List<UsuarioResponseDto>>

    @GET("api/usuarios/activos")
    suspend fun listarUsuariosActivos(
        @Header("Authorization") bearerToken: String
    ): Response<List<UsuarioResponseDto>>

    @GET("api/usuarios/rol/{rol}")
    suspend fun listarUsuariosPorRol(
        @Header("Authorization") bearerToken: String,
        @Path("rol") rol: String // "RELATOR" o "COORDINADOR"
    ): Response<List<UsuarioResponseDto>>

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuarioPorId(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Long
    ): Response<UsuarioResponseDto>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Long,
        @Body request: ActualizarUsuarioRequestDto
    ): Response<UsuarioResponseDto>

    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(
        @Header("Authorization") bearerToken: String,
        @Path("id") id: Long
    ): Response<MensajeResponseDto>
}
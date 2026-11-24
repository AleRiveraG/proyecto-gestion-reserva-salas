package com.example.gestiondereservas.data.remote.dto

import com.google.gson.annotations.SerializedName

//Petición para POST /api/auth/login (equivale a LoginRequestDTO en backend)
data class LoginRequestDto(
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String,
    @SerializedName("rol") val rol: String  //"RELATOR" o "COORDINADOR"
)

//Petición para POST /api/auth/register (equivale a RegistroRequestDTO en backend)
data class RegistroRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String,
    @SerializedName("telefono") val telefono: String,
    @SerializedName("rol") val rol: String  //"RELATOR" o "COORDINADOR"
)

//Respuesta de login y registro (equivale a AutenticacionResponseDTO en backend)
data class AuthResponseDto(
    @SerializedName("token") val token: String = "",
    @SerializedName("tipo") val tipo: String = "Bearer",
    @SerializedName("idUsuario") val idUsuario: Long = 0L,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("correo") val correo: String = "",
    @SerializedName("telefono") val telefono: String = "",
    @SerializedName("rol") val rol: String = "",
    @SerializedName("mensaje") val mensaje: String? = null
)

//Respuesta del CRUD de usuarios (equivale a UsuarioResponseDTO en backend)
data class UsuarioResponseDto(
    @SerializedName("id") val id: Long = 0L,
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("correo") val correo: String = "",
    @SerializedName("rol") val rol: String = "",
    @SerializedName("telefono") val telefono: String = "",
    @SerializedName("activo") val activo: Boolean = true,
    @SerializedName("fechaCreacion") val fechaCreacion: String? = null
)

//Petición para PUT /api/usuarios/{id} (equivale a ActualizarUsuarioRequestDTO en backend)
data class ActualizarUsuarioRequestDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val password: String? = null,
    @SerializedName("rol") val rol: String,
    @SerializedName("activo") val activo: Boolean? = true
)

//Respuesta de DELETE /api/usuarios/{id}
data class MensajeResponseDto(
    @SerializedName("mensaje") val mensaje: String = ""
)

//Respuesta de error de GlobalExceptionHandler (equivale a ErrorResponseDTO en backend)
data class ErrorResponseDto(
    @SerializedName("status") val status: Int? = null,
    @SerializedName("error") val error: String? = null,
    @SerializedName("mensaje") val mensaje: String? = null,
    @SerializedName("timestamp") val timestamp: String? = null,
    @SerializedName("detalles") val detalles: Map<String, String>? = null
)
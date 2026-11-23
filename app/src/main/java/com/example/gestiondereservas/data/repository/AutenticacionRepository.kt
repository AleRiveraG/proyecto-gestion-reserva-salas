package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.remote.AutenticacionApiService
import com.example.gestiondereservas.data.remote.AutenticacionRetrofitClient
import com.example.gestiondereservas.data.remote.RetrofitClient
import com.example.gestiondereservas.data.remote.SessionManager
import com.example.gestiondereservas.data.remote.dto.AuthResponseDto
import com.example.gestiondereservas.data.remote.dto.ErrorResponseDto
import com.example.gestiondereservas.data.remote.dto.LoginRequestDto
import com.example.gestiondereservas.data.remote.dto.RegistroRequestDto
import com.google.gson.Gson
import kotlinx.coroutines.CancellationException
import java.net.ConnectException
import java.net.SocketTimeoutException

class AutenticacionRepository(
    private val api: AutenticacionApiService = RetrofitClient.apiLogin
) {
    private val gson = Gson()

    suspend fun login(correo: String, password: String, rol: String): Result<AuthResponseDto> {
        return try {
            val response = api.login(
                LoginRequestDto(
                    correo = correo.trim().lowercase(),
                    password = password,
                    rol = rol.uppercase()
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!
                // Aquí se guarda el token JWT y los datos de sesión appena responde el backend
                SessionManager.guardarSesion(
                    token = data.token,
                    idUsuario = data.idUsuario,
                    nombre = data.nombre,
                    correo = data.correo,
                    rol = data.rol
                )
                Result.success(data)
            } else {
                Result.failure(
                    Exception(extraerMensajeError(response.code(), response.errorBody()?.string()))
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado. Verifica que el backend de login esté encendido en el puerto 8081."))
        } catch (e: ConnectException) {
            Result.failure(Exception("No se pudo conectar al servidor de login. Revisa la IP y el puerto 8081."))
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.localizedMessage ?: "desconocido"}"))
        }
    }

    suspend fun registrar(
        nombre: String,
        correo: String,
        password: String,
        rol: String
    ): Result<AuthResponseDto> {
        return try {
            val response = api.registrar(
                RegistroRequestDto(
                    nombre = nombre.trim(),
                    correo = correo.trim().lowercase(),
                    password = password,
                    rol = rol.uppercase()
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception(extraerMensajeError(response.code(), response.errorBody()?.string()))
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: SocketTimeoutException) {
            Result.failure(Exception("Tiempo de espera agotado al registrar. Verifica que el backend esté activo."))
        } catch (e: ConnectException) {
            Result.failure(Exception("No se pudo conectar al servidor de registro."))
        } catch (e: Exception) {
            Result.failure(Exception("Error al registrar: ${e.localizedMessage ?: "desconocido"}"))
        }
    }

    private fun extraerMensajeError(codigoHttp: Int, errorJson: String?): String {
        if (!errorJson.isNullOrBlank()) {
            try {
                val parsed = gson.fromJson(errorJson, ErrorResponseDto::class.java)
                val msg = parsed.mensaje ?: parsed.error
                if (!msg.isNullOrBlank()) return msg
            } catch (_: Exception) {
                // Si el backend no devolvió JSON estándar, cae al mensaje por código HTTP
            }
        }
        return when (codigoHttp) {
            400 -> "Datos inválidos. Revisa formato de correo institucional y contraseña."
            401 -> "Correo o contraseña incorrectos."
            403 -> "El perfil seleccionado no coincide con el rol registrado."
            409 -> "Este correo ya se encuentra registrado."
            else -> "Error del servidor (HTTP $codigoHttp)."
        }
    }
}
package com.example.gestiondereservas.data.remote

object SessionManager {
    var token: String? = null
    var idUsuario: Long? = null
    var nombre: String? = null
    var correo: String? = null
    var rol: String? = null

    fun guardarSesion(token: String, idUsuario: Long, nombre: String, correo: String, rol: String) {
        this.token = token
        this.idUsuario = idUsuario
        this.nombre = nombre
        this.correo = correo
        this.rol = rol
    }

    fun obtenerBearerToken(): String = "Bearer ${token.orEmpty()}"

    fun cerrarSesion() {
        token = null
        idUsuario = null
        nombre = null
        correo = null
        rol = null
    }
}
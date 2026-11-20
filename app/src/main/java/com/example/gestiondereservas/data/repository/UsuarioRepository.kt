package com.example.gestiondereservas.data.repository

import com.example.gestiondereservas.data.room.UsuarioDao
import com.example.gestiondereservas.data.room.entidades.UsuarioEntity

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    suspend fun registrarUsuario(usuario: UsuarioEntity) {
        usuarioDao.registrarUsuario(usuario)
    }

    suspend fun obtenerPorId(id: Long): UsuarioEntity? {
        return usuarioDao.obtenerUsuarioPorId(id)
    }

    suspend fun login(correo: String, contrasena: String): UsuarioEntity? {
        return usuarioDao.login(correo, contrasena)
    }

}
package com.example.gestiondereservas.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gestiondereservas.data.room.entidades.UsuarioEntity

@Dao
interface UsuarioDao {

    @Insert
    suspend fun registrarUsuario(usuario: UsuarioEntity)

    @Query("SELECT * FROM usuario WHERE idUsuario = :idUsuario")
    suspend fun obtenerUsuarioPorId(idUsuario: Long): UsuarioEntity

    @Query("SELECT * FROM usuario WHERE correo = :correo AND contrasena = :contrasena")
    suspend fun login(correo: String, contrasena: String): UsuarioEntity?


}
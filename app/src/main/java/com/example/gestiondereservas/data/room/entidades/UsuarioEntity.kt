package com.example.gestiondereservas.data.room.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val idUsuario: Long = 0,
    val nombre : String,
    val correo : String,
    val contrasena: String,
    val rol: String
)
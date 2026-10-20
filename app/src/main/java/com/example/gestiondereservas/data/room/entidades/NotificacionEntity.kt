package com.example.gestiondereservas.data.room.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notificacion")
data class NotificacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val descripcion: String,
    val tipo: String,
    val esSupervisor: Boolean,
    val leida: Boolean = false,
    val reservaId: Long? = null
)
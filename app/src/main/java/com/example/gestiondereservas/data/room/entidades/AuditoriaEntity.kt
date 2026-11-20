package com.example.gestiondereservas.data.room.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auditoria")
data class AuditoriaEntity (
   @PrimaryKey(autoGenerate = true) val idAuditoria: Long = 0,
    val accion: String,
    val detalle: String,
    val fecha: String,
    val hora: String,
    val tipo: String
)
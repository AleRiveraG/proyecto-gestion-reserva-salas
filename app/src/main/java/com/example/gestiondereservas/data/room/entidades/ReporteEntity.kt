
package com.example.gestiondereservas.data.room.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reporte")
data class ReporteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val reservaId: Long,
    val salaId: Long,
    val descripcion: String,
    val fechaReporte: String,
    val horaReporte: String
)

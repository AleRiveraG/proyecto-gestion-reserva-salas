package com.example.gestiondereservas.data.room.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evaluacion_sala")
data class EvaluacionSalaEntity(
    @PrimaryKey
    val reservaId: Long,
    val salaId: Long,
    val calificacion: Int,
    val observaciones: String,
    val fechaEvaluacion: String
)

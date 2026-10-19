package com.example.gestiondereservas.data.room.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "uso_sala")
data class UsoSalaEntity(
    @PrimaryKey
    val reservaId: Long,
    val estado: String = "PENDIENTE",
    val fechaHoraInicio: String? = null,
    val fechaHoraFin: String? = null
)

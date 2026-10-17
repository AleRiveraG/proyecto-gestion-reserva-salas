package com.example.gestiondereservas.data.room.entidades

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "horarios",
    foreignKeys = [
        ForeignKey(
            entity = Sala::class,
            parentColumns = ["id"],
            childColumns = ["salaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["salaId", "dia"])]
)
data class Horario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val salaId: Long,
    val dia: DiaSemana,
    val inicioMin: Int,
    val finMin: Int,
    val activo: Boolean = true
)

data class HorarioConSala(
    @Embedded val horario: Horario,
    @Relation(parentColumn = "salaId", entityColumn = "id")
    val sala: Sala
)

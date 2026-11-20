package com.example.gestiondereservas.data.room.entidades

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "equipamientos",
    foreignKeys = [
        ForeignKey(
            entity = SalaEntity::class,
            parentColumns = ["id"],
            childColumns = ["salaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("salaId"),
        Index(value = ["salaId", "nombre"], unique = true)
    ]
)
data class Equipamiento(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val salaId: Long,
    val nombre: String,
    val ocupado: Boolean = false
)

data class SalaConEquipamiento(
    @Embedded val salaEntity: SalaEntity,
    @Relation(parentColumn = "id", entityColumn = "salaId")
    val equipamiento: List<Equipamiento>
)
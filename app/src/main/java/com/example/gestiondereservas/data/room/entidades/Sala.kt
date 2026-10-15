package com.example.gestiondereservas.data.room.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "salas",
    indices = [Index(value = ["codigo"], unique = true)]
)
data class Sala(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val codigo: String,

    val nombre: String,
    val area: Area,
    val capacidad: Int,
    val activa: Boolean = true,
    val imagenUri: String? = null
)
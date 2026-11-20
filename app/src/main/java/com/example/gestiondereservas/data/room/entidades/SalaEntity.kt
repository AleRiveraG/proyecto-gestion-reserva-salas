package com.example.gestiondereservas.data.room.entidades

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "salas",
    indices = [Index(value = ["codigo"], unique = true)]
)
data class SalaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val codigo: String,
    //el collate = nocase hace que por ejemplo si yo coloco sala C505 y c505 con miniscula las encuentre igual
    // con o son minuscula y viceversa
    val nombre: String,
    val area: Area,
    val capacidad: Int,
    val activa: Boolean = true,
    val imagenUri: String? = null
)
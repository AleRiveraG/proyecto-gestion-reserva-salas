package com.example.gestiondereservas.data.remote.dto

import com.example.gestiondereservas.data.room.entidades.Area
import com.google.gson.annotations.SerializedName

data class SalaResponse(
    @SerializedName("id") val salaId: Long,
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("area") val area: Area,
    @SerializedName("capacidad") val capacidad: Int,
    @SerializedName("activa") val activa: Boolean,
    @SerializedName("imagenUri") val imagenUri: String,
    @SerializedName("equipamiento") val equipamiento: List<EquipamientoDto>
    )

data class SalaDto(
    @SerializedName("codigo") val codigo: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("area") val area: Area,
    @SerializedName("capacidad") val capacidad: Int,
    @SerializedName("activa") val activa: Boolean?,
    @SerializedName("imagenUri") val imagenUri: String?,
    @SerializedName("equipamiento") val equipamiento: List<EquipamientoDto>?
)

data class EquipamientoDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("ocupado") val ocupado: Boolean = false
)
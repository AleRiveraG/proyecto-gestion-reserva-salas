package com.example.gestiondereservas.data.remote.dto

import com.example.gestiondereservas.data.room.entidades.DiaSemana
import com.google.gson.annotations.SerializedName
import java.time.LocalTime

data class HorarioResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("salaId") val salaId: Long,
    @SerializedName("dia") val dia: DiaSemana,
    @SerializedName("horaInicio") val horaInicio: LocalTime,
    @SerializedName("horaTermino") val horaTermino: LocalTime,
    @SerializedName("activo") val activo: Boolean
)

data class HorarioDto(
    @SerializedName("salaId") val salaId: Long,
    @SerializedName("dia") val dia: DiaSemana,
    @SerializedName("horaInicio") val horaInicio: LocalTime,
    @SerializedName("horaTermino") val horaTermino: LocalTime,
    @SerializedName("activo") val activo: Boolean
)
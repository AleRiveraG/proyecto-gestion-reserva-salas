package com.example.gestiondereservas.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime

data class AuditoriaResponse(
    @SerializedName("accion") val accion: String,
    @SerializedName("detalle") val detalle: String,
    @SerializedName("fecha") val fecha: LocalDate,
    @SerializedName("hora") val hora: LocalTime,
    @SerializedName("tipo") val tipo: String
)

data class AuditoriaDto(
    @SerializedName("accion") val accion: String,
    @SerializedName("detalle") val detalle: String,
    @SerializedName("fecha") val fecha: LocalDate,
    @SerializedName("hora") val hora: LocalTime,
    @SerializedName("tipo") val tipo: String
)
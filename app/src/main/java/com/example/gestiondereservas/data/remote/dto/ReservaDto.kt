package com.example.gestiondereservas.data.remote.dto

import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.utils.LocalDateAString
import com.example.gestiondereservas.utils.LocalTimeAString
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalTime
import kotlin.Long

data class DocumentoResponse(
    @SerializedName("idDocumento") val idDocumento: Long,
    @SerializedName("nombreArchivo") val nombreArchivo: String,
    @SerializedName("ruta") val ruta: String,
    @SerializedName("fechaSubida") val fechaSubida: LocalDate
)

data class ReservaResponse(
    @SerializedName("idReserva") val idReserva: Long,
    @SerializedName("idUsuario") val idUsuario: Long,
    @SerializedName("nombreActividad") val nombreActividad: String,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("tipoActividad") val tipoActividad: String,
    @SerializedName("tieneAutorizacion") val tieneAutorizacion: Boolean,
    @SerializedName("nombreResponsable") val nombreResponsable: String,
    @SerializedName("telefonoResponsable") val telefonoResponsable: String,
    @SerializedName("correoResponsable") val correoResponsable: String,
    @SerializedName("organizador") val organizador: String,
    @SerializedName("salaId") val salaId: Long,
    @SerializedName("fechaActividad") val fechaActividad: LocalDate,
    @SerializedName("horaInicio") val horaInicio: LocalTime,
    @SerializedName("horaTermino") val horaTermino: LocalTime,
    @SerializedName("fechaMontaje") val fechaMontaje: LocalDate,
    @SerializedName("horaMontaje") val horaMontaje: LocalTime,
    @SerializedName("publico") val publico: MutableSet<String>,
    @SerializedName("requiereFormulario") val requiereFormulario: Boolean,
    @SerializedName("nombresExternos") val nombresExternos: MutableSet<String?>? = null,
    @SerializedName("documento") val documento: DocumentoResponse,

    @SerializedName("servicios") val servicios: MutableMap<String?, MutableSet<String?>?>? = null,

    @SerializedName("complementos") val complementos: String? = null,

    @SerializedName("proveedores") val proveedores: String? = null,

    @SerializedName("estado") val estado: EstadoReserva
)
data class ReservaDTO (
    @SerializedName("idUsuario") val idUsuario: Long,
    @SerializedName("nombreActividad") val nombreActividad: String,
    @SerializedName("descripcion") val descripcion: String? = null,
    @SerializedName("tipoActividad") val tipoActividad: String,
    @SerializedName("tieneAutorizacion") val tieneAutorizacion: Boolean,
    @SerializedName("nombreResponsable") val nombreResponsable: String,
    @SerializedName("telefonoResponsable") val telefonoResponsable: String,
    @SerializedName("correoResponsable") val correoResponsable: String,
    @SerializedName("organizador") val organizador: String,
    @SerializedName("salaId") val salaId: Long,
    @SerializedName("fechaActividad") val fechaActividad: String,
    @SerializedName("horaInicio") val horaInicio: String,
    @SerializedName("horaTermino") val horaTermino: String,
    @SerializedName("fechaMontaje") val fechaMontaje: String,
    @SerializedName("horaMontaje") val horaMontaje: String,
    @SerializedName("publico") val publico: MutableSet<String>,
    @SerializedName("requiereFormulario") val requiereFormulario: Boolean,
    @SerializedName("nombresExternos") val nombresExternos: MutableSet<String?>? = null,

    @SerializedName("servicios") val servicios: MutableMap<String?, MutableSet<String?>?>? = null,

    @SerializedName("complementos") val complementos: String? = null,

    @SerializedName("proveedores") val proveedores: String? = null
)

// Mapeos
/*
fun ReservaDTO.toModel(): ReservaEntity {
    val gson = Gson()
    return ReservaEntity(
        id = 0L,
        idUsuario = idUsuario,
        nombreActividad = nombreActividad,
        descripcion = descripcion,
        tipoActividad = tipoActividad,
        tieneValidacion = tieneAutorizacion,
        nombreResponsable = nombreResponsable,
        telefonoResponsable = telefonoResponsable,
        correoResponsable = correoResponsable,
        organizador = organizador,

        salaId = salaId,
        fechaActividad = LocalDateAString(fechaActividad),
        horaInicio = LocalTimeAString(horaInicio),
        horaTermino = LocalTimeAString(horaTermino),
        fechaMontaje = LocalDateAString(fechaMontaje),
        horaMontaje = LocalTimeAString(horaMontaje),
        publico = publico,
        requiereFormulario = requiereFormulario,
        nombreExternos = nombresExternos,
        documentoPdf = ,

        servicios = servicios,
        complementos = complementos,
        proveedores = proveedores
    )
}

 */

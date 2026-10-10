package com.example.gestiondereservas.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reserva")
data class ReservaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nombreActividad: String,
    val descripcion: String,
    val tipoActividad: String,
    val tieneValidacion: Boolean,
    val nombreResponsable: String,
    val telefonoResponsable: String,
    val correoResponsable: String,
    val organizador: String,

    val salaId: Long,
    val fechaActividad: String,
    val horaInicio: String,
    val horaTermino: String,
    val fechaMontaje: String,
    val horaMontaje: String,
    val publico: String,
    val requiereFormulario: Boolean,
    val nombreExternos: String,

    val servicios: String,

    val estado: EstadoReserva = EstadoReserva.PENDIENTE
)
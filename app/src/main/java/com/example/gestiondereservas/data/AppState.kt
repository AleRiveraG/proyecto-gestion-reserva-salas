package com.example.gestiondereservas.data

import androidx.room.PrimaryKey
import com.example.gestiondereservas.data.room.AppDatabase
import java.time.LocalDate
import java.time.LocalTime

data class Reserva(
    val nombreActividad: String = "",
    val descripcion: String = ""
)

class AppState(private val db: AppDatabase) {

    val reservas = mutableListOf<Reserva>()

}

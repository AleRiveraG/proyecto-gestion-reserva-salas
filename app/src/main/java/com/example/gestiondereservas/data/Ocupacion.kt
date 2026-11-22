package com.example.gestiondereservas.data

import java.time.LocalDate
import java.time.LocalTime

enum class TipoOcupacion { RESERVADO, CLASE, MONTAJE }
//esto es para la ocupacion de la sala reservado, clase o montaje
//esto es lo que viene de reservas por eso no estan los otros como bloqueado y libre
// usa el mismo formato que reserva entity dd/mm tambien para los minutos
data class Ocupacion(
    val salaId: Long,
    val fecha: LocalDate,
    val inicioMin: LocalTime,
    val finMin: LocalTime,
    val tipo: TipoOcupacion,
    val titulo: String,
    val responsable: String = ""
)

//esto es para las ocupaciones de una sala ahora lo estoy haciendo con ocupacion fuente prueba. Cuando se haga con la bd real se
//deberia reemplazar por una que lea las reservas y ocupe el OcupacionMapper desde reserva para cada una


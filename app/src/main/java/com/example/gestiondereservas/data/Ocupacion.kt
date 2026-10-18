package com.example.gestiondereservas.data

enum class TipoOcupacion { RESERVADO, CLASE, MONTAJE }
//esto es para la ocupacion de la sala reservado, clase o montaje
//esto es lo que viene de reservas por eso no estan los otros como bloqueado y libre
// usa el mismo formato que reserva entity dd/mm tambien para los minutos
data class Ocupacion(
    val salaId: Long,
    val fecha: String,
    val inicioMin: Int,
    val finMin: Int,
    val tipo: TipoOcupacion,
    val titulo: String,
    val responsable: String = ""
)

//esto es para las ocupaciones de una sala ahora lo estoy haciendo con ocupacion fuente prueba. Cuando se haga con la bd real se
//deberia reemplazar por una que lea las reservas y ocupe el OcupacionMapper desde reserva para cada una
interface OcupacionFuente {
    suspend fun obtenerPorSala(salaId: Long): List<Ocupacion>
}

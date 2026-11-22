package com.example.gestiondereservas.data

import com.example.gestiondereservas.data.remote.dto.ReservaResponse
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.utils.FormatoHora
import com.example.gestiondereservas.utils.LocalDateAString
import com.example.gestiondereservas.utils.LocalTimeAString
import java.time.LocalTime

//esto convierto los datos de la reserva en las franjas que pinta la grilla
//recibe los mismos de reserva entity
object OcupacionMapper {

    fun desdeReserva(reserva: ReservaResponse): List<Ocupacion> {
        val inicio = reserva.horaInicio
        val fin = reserva.horaTermino
        if (!fin.isAfter(inicio)) return emptyList()

        val tipo = if (reserva.tipoActividad == "Actividad Académica") {
            TipoOcupacion.CLASE
        } else {
            TipoOcupacion.RESERVADO
        }

        val resultado = mutableListOf(
            Ocupacion(reserva.salaId,
                reserva.fechaActividad,
                inicio,
                fin,
                tipo,
                reserva.nombreActividad,
                reserva.nombreResponsable
            )
        )

        val fechaMontaje = reserva.fechaMontaje
        val inicioMontaje = reserva.horaMontaje
        if (fechaMontaje != null && inicioMontaje != null) {
            val finMontaje =
                if (fechaMontaje == reserva.fechaActividad && inicioMontaje.isBefore(inicio)) {
                    inicio
                } else {
                    val minutosRestantes = java.time.Duration.between(inicioMontaje, LocalTime.MAX).toMinutes()
                    if(minutosRestantes >= 60) inicioMontaje.plusMinutes(60) else LocalTime.MAX
                }


            resultado += Ocupacion(
                salaId = reserva.salaId,
                fecha = fechaMontaje,
                inicioMin = inicioMontaje,
                finMin = finMontaje,
                tipo = TipoOcupacion.MONTAJE,
                titulo = "Montaje: ${reserva.nombreActividad}",
                responsable = reserva.nombreResponsable
            )
        }
        return resultado
    }
}

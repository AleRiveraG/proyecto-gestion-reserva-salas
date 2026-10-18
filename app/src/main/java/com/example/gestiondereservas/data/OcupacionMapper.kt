package com.example.gestiondereservas.data

import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.utils.FormatoHora
//esto convierto los datos de la reserva en las franjas que pinta la grilla
//recibe los mismos de reserva entity
object OcupacionMapper {

    fun desdeReserva(reserva: ReservaEntity): List<Ocupacion> {
        val inicio = FormatoHora.aMinutos(reserva.horaInicio) ?: return emptyList()
        val fin = FormatoHora.aMinutos(reserva.horaTermino) ?: return emptyList()
        if (fin <= inicio) return emptyList()

        val tipo = if (reserva.tipoActividad == "Actividad Académica") {
            TipoOcupacion.CLASE
        } else {
            TipoOcupacion.RESERVADO
        }

        val resultado = mutableListOf(
            Ocupacion(reserva.salaId, reserva.fechaActividad, inicio, fin, tipo, reserva.nombreActividad, reserva.nombreResponsable)
        )

        val inicioMontaje = FormatoHora.aMinutos(reserva.horaMontaje)
        if (inicioMontaje != null && reserva.fechaMontaje.isNotBlank()) {
            val finMontaje =
                if (reserva.fechaMontaje == reserva.fechaActividad && inicioMontaje < inicio) inicio
                else inicioMontaje + 60

            resultado += Ocupacion(
                salaId = reserva.salaId,
                fecha = reserva.fechaMontaje,
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

package com.example.gestiondereservas.data

import com.example.gestiondereservas.utils.FormatoHora
//esto convierto los datos de la reserva en las franjas que pinta la grilla
//recibe los mismos de reserva entity
object OcupacionMapper {

    fun desdeReserva(
        salaId: Long,
        titulo: String,
        tipoActividad: String,
        responsable: String,
        fechaActividad: String,
        horaInicio: String,
        horaTermino: String,
        fechaMontaje: String,
        horaMontaje: String
    ): List<Ocupacion> {
        val inicio = FormatoHora.aMinutos(horaInicio) ?: return emptyList()
        val fin = FormatoHora.aMinutos(horaTermino) ?: return emptyList()
        if (fin <= inicio) return emptyList()

        val tipo = if (tipoActividad == "Actividad Académica") {
            TipoOcupacion.CLASE
        } else {
            TipoOcupacion.RESERVADO
        }

        val resultado = mutableListOf(
            Ocupacion(salaId, fechaActividad, inicio, fin, tipo, titulo, responsable)
        )

        val inicioMontaje = FormatoHora.aMinutos(horaMontaje)
        if (inicioMontaje != null && fechaMontaje.isNotBlank()) {
            val finMontaje =
                if (fechaMontaje == fechaActividad && inicioMontaje < inicio) inicio
                else inicioMontaje + 60

            resultado += Ocupacion(
                salaId = salaId,
                fecha = fechaMontaje,
                inicioMin = inicioMontaje,
                finMin = finMontaje,
                tipo = TipoOcupacion.MONTAJE,
                titulo = "Montaje: $titulo",
                responsable = responsable
            )
        }
        return resultado
    }
}

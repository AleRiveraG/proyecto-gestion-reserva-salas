
package com.example.gestiondereservas.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
class UsoSalaViewModel : ViewModel() {

    var estadoActividad by mutableStateOf(EstadoActividad.PENDIENTE)
        private set

    var nombreActividad by mutableStateOf("")
        private set
    var inicioReserva by mutableStateOf(LocalDateTime.now())
        private set

    var finReserva by mutableStateOf(inicioReserva.plusMinutes(60))
        private set

    var segundosRestantes by mutableStateOf(3600L)
        private set

    private var trabajoCronometro: Job? = null

    fun cargarReserva(reserva: ReservaEntity) {

        nombreActividad = reserva.nombreActividad

        val anioActual = LocalDate.now().year
        val fechaCompleta = "${reserva.fechaActividad}/$anioActual"
        val fecha = LocalDate.parse(
            fechaCompleta,
        DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
        val horaInicioFormateada = LocalTime.parse(reserva.horaInicio)
        val horaTerminoFormateada = LocalTime.parse(reserva.horaTermino)
        val inicio = fecha.atTime(horaInicioFormateada)
        val fin = fecha.atTime(horaTerminoFormateada)

        configurarReserva(inicio, fin)
    }

    fun configurarReserva(
        inicio: LocalDateTime,
        fin: LocalDateTime
    ) {
        if (estadoActividad != EstadoActividad.PENDIENTE || !fin.isAfter(inicio)) {
            return
        }

        inicioReserva = inicio
        finReserva = fin
        segundosRestantes = Duration.between(inicio, fin).seconds
    }

    fun iniciarActividad() {
        if (estadoActividad == EstadoActividad.PENDIENTE &&
            LocalDateTime.now().isBefore(finReserva)
        ) {
            estadoActividad = EstadoActividad.EN_CURSO
            actualizarTiempoRestante()

            trabajoCronometro?.cancel()

            trabajoCronometro = viewModelScope.launch {
                while (estadoActividad == EstadoActividad.EN_CURSO) {
                    actualizarTiempoRestante()

                    if (segundosRestantes == 0L) {
                        finalizarActividad()
                        break
                    }
                    delay(1000)
                }
            }
        }
    }

    private fun actualizarTiempoRestante() {
        segundosRestantes = Duration.between(
            LocalDateTime.now(),
            finReserva
        ).seconds.coerceAtLeast(0)
    }

    fun finalizarActividad() {
        if (estadoActividad == EstadoActividad.EN_CURSO) {
            estadoActividad = EstadoActividad.FINALIZADA
            trabajoCronometro?.cancel()
        }
    }
}

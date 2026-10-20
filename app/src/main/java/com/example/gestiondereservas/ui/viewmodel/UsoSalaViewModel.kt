package com.example.gestiondereservas.ui.viewmodel

import androidx.room.withTransaction
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.ReservaDao
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.data.room.UsoSalaDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
import com.example.gestiondereservas.data.room.entidades.UsoSalaEntity
import com.example.gestiondereservas.utils.calcularInicioReserva
import java.time.LocalDate
import java.time.LocalTime
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.gestiondereservas.workers.FinalizacionActividadWorker
import java.util.concurrent.TimeUnit
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

    var reservaId by mutableStateOf<Long?>(null)
        private set

    private var trabajoCronometro: Job? = null

    var salaId by mutableStateOf<Long?>(null)
        private set

    var guardando by mutableStateOf(false)
        private set

    var errorActividad by mutableStateOf<String?>(null)
        private set

        suspend fun cargarEstadoGuardado(dao: UsoSalaDao, id: Long) {
        val uso = withContext(Dispatchers.IO) {
            dao.obtenerPorReserva(id)
        }
        restaurarEstado(uso,dao)
    }

    suspend fun registrarInicio(
        db: AppDatabase,
        context: android.content.Context
    ) {
        val id = reservaId ?: return
        val ahora = LocalDateTime.now()

        if (estadoActividad != EstadoActividad.PENDIENTE) return

        if (ahora.isBefore(inicioReserva)) {
            errorActividad = "Todavía no comienza el horario de la reserva"
            return
        }

        if (!ahora.isBefore(finReserva)) {
            errorActividad = "El horario de la reserva ya terminó"
            return
        }

        guardando = true
        errorActividad = null

        try {
            val registrado = withContext(Dispatchers.IO) {
                db.withTransaction {
                    val reserva = db.reservaDao().obtenerReservaPorId(id)
                    val uso = db.usoSalaDao().obtenerPorReserva(id)

                    if (
                        reserva?.estado != EstadoReserva.APROBADA ||
                        uso?.estado == "AUSENTE" ||
                        uso?.estado == "EN_CURSO" ||
                        uso?.estado == "FINALIZADA"
                    ) {
                        false
                    } else {
                        val momento = LocalDateTime.now()

                        if (
                            momento.isBefore(inicioReserva) ||
                            !momento.isBefore(finReserva)
                        ) {
                            false
                        } else {
                            db.usoSalaDao().guardar(
                                UsoSalaEntity(
                                    reservaId = id,
                                    estado = "EN_CURSO",
                                    fechaHoraInicio = momento.toString()
                                )
                            )
                            true
                        }
                    }
                }
            }

            if (registrado) {
                val espera = Duration.between(
                    LocalDateTime.now(),
                    finReserva
                ).toMillis().coerceAtLeast(0L)

                val solicitud = OneTimeWorkRequestBuilder<FinalizacionActividadWorker>()
                    .setInitialDelay(espera, TimeUnit.MILLISECONDS)
                    .setInputData(
                        workDataOf(
                            "reservaId" to id,
                            "finProgramado" to finReserva.toString()
                        )
                    )
                    .build()

                WorkManager.getInstance(context).enqueueUniqueWork(
                    "finalizacion_$id",
                    ExistingWorkPolicy.REPLACE,
                    solicitud
                )

                estadoActividad = EstadoActividad.EN_CURSO
                iniciarCronometro(db.usoSalaDao())
            } else {
                errorActividad = "No se puede iniciar: la reserva ya no está aprobada o el horario terminó"
            }
        } catch (e: Exception) {
            errorActividad = "No se pudo registrar el inicio"
        } finally {
            guardando = false
        }
    }


    suspend fun registrarFinalizacion(dao: UsoSalaDao) {
        val id = reservaId ?: return

        if (estadoActividad != EstadoActividad.EN_CURSO) return

        guardando = true
        errorActividad = null

        try {
            val filasActualizadas = withContext(Dispatchers.IO) {
                dao.finalizar(id, LocalDateTime.now().toString())
            }

            if (filasActualizadas > 0) {
                finalizarActividad()
            } else {
                errorActividad = "No se encontró una actividad en curso"
            }
        } catch (e: Exception) {
            errorActividad = "No se pudo finalizar la actividad"
        } finally {
            guardando = false
        }
    }

    fun cargarReserva(reserva: ReservaEntity) {
        reservaId = reserva.id
        salaId = reserva.salaId
        nombreActividad = reserva.nombreActividad

        val inicio = calcularInicioReserva(
            reserva.fechaActividad,
            reserva.horaInicio
        )

        val fin = inicio.toLocalDate().atTime(
            LocalTime.parse(reserva.horaTermino)
        )

        trabajoCronometro?.cancel()

        estadoActividad = EstadoActividad.PENDIENTE

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


    fun iniciarActividad(dao: UsoSalaDao) {
        if (estadoActividad != EstadoActividad.PENDIENTE) return

        val ahora = LocalDateTime.now()

        if (ahora.isBefore(inicioReserva) || !ahora.isBefore(finReserva)) {
            return
        }

        estadoActividad = EstadoActividad.EN_CURSO
        iniciarCronometro(dao)
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


    private fun iniciarCronometro(dao: UsoSalaDao) {
        trabajoCronometro?.cancel()

        trabajoCronometro = viewModelScope.launch {
            while (estadoActividad == EstadoActividad.EN_CURSO) {
                actualizarTiempoRestante()

                if (segundosRestantes == 0L) {
                    registrarFinalizacion(dao)
                    break
                }

                delay(1000)
            }
        }
    }



    fun restaurarEstado(uso: UsoSalaEntity?, dao: UsoSalaDao) {
        trabajoCronometro?.cancel()

        estadoActividad = when (uso?.estado) {
            "EN_CURSO" -> EstadoActividad.EN_CURSO
            "FINALIZADA" -> EstadoActividad.FINALIZADA
            else -> EstadoActividad.PENDIENTE
        }

        if (estadoActividad == EstadoActividad.EN_CURSO) {
            actualizarTiempoRestante()
            iniciarCronometro(dao)
        }
    }

}

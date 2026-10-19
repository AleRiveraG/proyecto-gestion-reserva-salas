package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.repository.ReservaRepository
import com.example.gestiondereservas.data.room.entidades.ReservaEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.gestiondereservas.data.room.AppDatabase
import com.example.gestiondereservas.data.room.entidades.EstadoReserva
import com.example.gestiondereservas.workers.AusenciaWorker
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import com.example.gestiondereservas.utils.calcularInicioReserva

class GestionReservasViewModel(
    private val repository: ReservaRepository,
    private val context: Context
) : ViewModel() {

    private val reservasPendiente = MutableStateFlow<List<ReservaEntity>>(emptyList())
    val reservasPendientes: StateFlow<List<ReservaEntity>> = reservasPendiente.asStateFlow()

    private val totalAprobadas = MutableStateFlow(0)
    val aprobadas: StateFlow<Int> = totalAprobadas.asStateFlow()

    private val totalRechazadas = MutableStateFlow(0)
    val rechazadas: StateFlow<Int> = totalRechazadas.asStateFlow()

    private val totalPendientes = MutableStateFlow(0)
    val pendientes: StateFlow<Int> = totalPendientes.asStateFlow()

    init {
        cargarDatos()
    }


    fun cargarDatos() {
        viewModelScope.launch {
            reservasPendiente.value = repository.obtenerReservasPendientes()
            totalAprobadas.value = repository.obtenerAprobadas()
            totalRechazadas.value = repository.obtenerRechazadas()
            totalPendientes.value = repository.obtenerPendiente()
        }
    }

    fun modificarEstado(id: Long, estado: String) {
        viewModelScope.launch {
            repository.modificarEstado(id, estado)

            if (estado == EstadoReserva.APROBADA.name) {
                val reserva = AppDatabase.getInstance(context)
                    .reservaDao()
                    .obtenerReservaPorId(id)

                if (reserva != null && reserva.estado == EstadoReserva.APROBADA) {

                                        val inicio = calcularInicioReserva(
                        reserva.fechaActividad,
                        reserva.horaInicio
                    )

                    val vencimiento = inicio.plusMinutes(15)

                    val espera = Duration.between(
                        LocalDateTime.now(),
                        vencimiento
                    ).toMillis().coerceAtLeast(0L)

                    val solicitud = OneTimeWorkRequestBuilder<AusenciaWorker>()
                        .setInitialDelay(espera, TimeUnit.MILLISECONDS)
                        .setInputData(workDataOf("reservaId" to id))
                        .build()

                    WorkManager.getInstance(context).enqueueUniqueWork(
                        "ausencia_$id",
                        ExistingWorkPolicy.REPLACE,
                        solicitud
                    )
                }
            }

            cargarDatos()
        }
    }

    suspend fun buscarSala(id: Long): SalaEntity? {
        return repository.obtenerSalaPorId(id)
    }



}
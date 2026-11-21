package com.example.gestiondereservas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.Ocupacion
import com.example.gestiondereservas.data.remote.dto.HorarioResponse
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import com.example.gestiondereservas.data.repository.HorarioRepository
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import com.example.gestiondereservas.data.room.entidades.HorarioEntity
import com.example.gestiondereservas.data.room.entidades.SalaEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HorariosUiState(
    val salas: List<SalaResponse> = emptyList(),
    val sala: SalaResponse? = null,
    // bloques de horario de atención de la sala elegida (todos los días)
    val horarios: List<HorarioResponse> = emptyList(),
    // franjas ocupadas de la sala elegida (la pantalla filtra las de la semana visible)
    val ocupaciones: List<Ocupacion> = emptyList(),
    // 0 = esta semana, -1 = la anterior, 1 = la siguiente
    val semana: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class HorariosViewModel(
    private val repositorio: HorarioRepository
) : ViewModel() {

    private val salaElegida = MutableStateFlow<Long?>(null)
    private val semana = MutableStateFlow(0)

    private val salaActiva: Flow<Pair<List<SalaResponse>, SalaResponse?>> =
        combine(
            repositorio.observarSalas(),
            salaElegida
        ) { salas, id ->
            val seleccionada = salas.firstOrNull { it.salaId == id } ?: salas.firstOrNull()
            salas to seleccionada
        }

    private val horariosSala: Flow<List<HorarioResponse>> =
        salaActiva
            .map { (_, sala) -> sala?.salaId }
            .distinctUntilChanged()
            .flatMapLatest { salaId ->
                flow {
                    if(salaId == null) {
                        emit(emptyList())
                    } else {
                        val horarioPorDia = DiaSemana.entries.flatMap { dia ->
                            repositorio.obtenerHorariosPorSalaYDia(salaId, dia)
                        }
                        emit(horarioPorDia)
                    }
                }
            }

    private val ocupacionesSala: Flow<List<Ocupacion>> =
        salaActiva
            .map { (_, sala) -> sala?.salaId }
            .distinctUntilChanged()
            .flatMapLatest { salaId ->
                flow {
                    if(salaId == null) {
                        emit(emptyList())
                    } else {
                        val ocupaciones = try {
                            repositorio.obtenerOcupacionesPorSala(salaId)
                        } catch (_: Exception) {
                            emptyList()
                        }
                        emit(ocupaciones)
                    }

                }
            }

    val uiState: StateFlow<HorariosUiState> =
        combine(
            salaActiva,
            horariosSala,
            ocupacionesSala,
            semana
        ) { (salas, sala), horarios, ocupaciones, semanaActual ->

            HorariosUiState(
                salas = salas,
                sala = sala,
                horarios = horarios,
                ocupaciones = ocupaciones,
                semana = semanaActual
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HorariosUiState()
        )

    fun onSalaChange(salaId: Long) {
        salaElegida.value = salaId
    }

    fun cambiarSemana(delta: Int) {
        semana.value = semana.value + delta
    }

    fun irAHoy() {
        semana.value = 0
    }
}

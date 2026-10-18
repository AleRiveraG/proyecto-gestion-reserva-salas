package com.example.gestiondereservas.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.Ocupacion
import com.example.gestiondereservas.data.repository.HorarioRepository
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
    val salas: List<SalaEntity> = emptyList(),
    val sala: SalaEntity? = null,
    // bloques de horario de atención de la sala elegida (todos los días)
    val horarios: List<HorarioEntity> = emptyList(),
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

    init {
        viewModelScope.launch { repositorio.sembrarSiVacio() }
    }

    private val base: Flow<HorariosUiState> =
        combine(
            repositorio.observarSalas(),
            repositorio.observarTodos(""),
            salaElegida
        ) { salas, todos, id ->
            val sala = salas.firstOrNull { it.id == id } ?: salas.firstOrNull()

            HorariosUiState(
                salas = salas,
                sala = sala,
                horarios = todos
                    .filter { it.sala.id == sala?.id }
                    .map { it.horario }
            )
        }

    private val ocupacionesDeLaSala: Flow<List<Ocupacion>> =
        base
            .map { it.sala?.id }
            .distinctUntilChanged()
            .flatMapLatest { salaId ->
                flow {
                    emit(
                        if (salaId == null) emptyList()
                        else repositorio.obtenerOcupacionesPorSala(salaId)
                    )
                }
            }

    val uiState: StateFlow<HorariosUiState> =
        combine(base, semana, ocupacionesDeLaSala) { estado, semanaActual, lista ->
            estado.copy(semana = semanaActual, ocupaciones = lista)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            HorariosUiState()
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

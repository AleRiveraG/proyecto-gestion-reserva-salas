package com.example.gestiondereservas.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestiondereservas.data.Ocupacion
import com.example.gestiondereservas.data.remote.dto.HorarioResponse
import com.example.gestiondereservas.data.remote.dto.SalaResponse
import com.example.gestiondereservas.data.repository.HorarioRepository
import com.example.gestiondereservas.data.room.entidades.DiaSemana
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HorariosUiState(
    val salas: List<SalaResponse> = emptyList(),
    val sala: SalaResponse? = null,
    // bloques de horario de atención de la sala elegida (todos los días)
    val horarios: List<HorarioResponse> = emptyList(),
    // franjas ocupadas de la sala elegida (la pantalla filtra las de la semana visible)
    val ocupaciones: List<Ocupacion> = emptyList(),
    // 0 = esta semana, -1 = la anterior, 1 = la siguiente
    val semana: Int = 0,
    // true mientras no hay nada que mostrar todavía (primera carga)
    val cargando: Boolean = true
)

data class DatosHorarioSala(
    val horarios: List<HorarioResponse>,
    val ocupaciones: List<Ocupacion>
)

// Caché en memoria: al volver a la pestaña se muestra al tiro lo último cargado
object HorariosCache {
    var salas: List<SalaResponse> = emptyList()
    var salaId: Long? = null
    val datos: MutableMap<Long, DatosHorarioSala> = mutableMapOf()
}

class HorariosViewModel(
    private val repositorio: HorarioRepository
) : ViewModel() {

    private var salaElegida: Long? = HorariosCache.salaId
    private var trabajo: Job? = null

    private val _uiState = MutableStateFlow(estadoInicial())
    val uiState: StateFlow<HorariosUiState> = _uiState.asStateFlow()

    private fun estadoInicial(): HorariosUiState {
        val salas = HorariosCache.salas
        val sala = salas.firstOrNull { it.salaId == salaElegida } ?: salas.firstOrNull()
        val datos = sala?.let { HorariosCache.datos[it.salaId] }
        return HorariosUiState(
            salas = salas,
            sala = sala,
            horarios = datos?.horarios ?: emptyList(),
            ocupaciones = datos?.ocupaciones ?: emptyList(),
            semana = 0,
            cargando = datos == null
        )
    }

    // Trae horarios (7 días a la vez) y ocupaciones de una sala en paralelo
    private suspend fun cargarDatos(salaId: Long): DatosHorarioSala = coroutineScope {
        val horariosDef = DiaSemana.entries.map { dia ->
            async { repositorio.obtenerHorariosPorSalaYDia(salaId, dia) }
        }
        val ocupacionesDef = async {
            try {
                repositorio.obtenerOcupacionesPorSala(salaId)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                emptyList()
            }
        }
        DatosHorarioSala(
            horarios = horariosDef.awaitAll().flatten(),
            ocupaciones = ocupacionesDef.await()
        )
    }

    private fun publicar(
        salas: List<SalaResponse>,
        sala: SalaResponse?,
        datos: DatosHorarioSala?
    ) {
        HorariosCache.salas = salas
        HorariosCache.salaId = sala?.salaId
        if (sala != null && datos != null) HorariosCache.datos[sala.salaId] = datos
        salaElegida = sala?.salaId

        _uiState.update {
            it.copy(
                salas = salas,
                sala = sala,
                horarios = datos?.horarios ?: emptyList(),
                ocupaciones = datos?.ocupaciones ?: emptyList(),
                cargando = false
            )
        }
    }

    fun recargar() {
        // si ya hay una carga en curso no la reinicio
        if (trabajo?.isActive == true) return

        trabajo = viewModelScope.launch {
            try {
                coroutineScope {
                    val idPrevio = salaElegida ?: _uiState.value.sala?.salaId

                    // salas y datos de la sala conocida arrancan al mismo tiempo
                    val salasDef = async { repositorio.salasDisponibles() }
                    val datosPrevioDef = idPrevio?.let { id -> async { cargarDatos(id) } }

                    val salas = salasDef.await()
                    val sala = salas.firstOrNull { it.salaId == idPrevio } ?: salas.firstOrNull()

                    val datos = when {
                        sala == null -> {
                            datosPrevioDef?.cancel()
                            null
                        }
                        sala.salaId == idPrevio && datosPrevioDef != null -> datosPrevioDef.await()
                        else -> {
                            datosPrevioDef?.cancel()
                            cargarDatos(sala.salaId)
                        }
                    }
                    publicar(salas, sala, datos)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun onSalaChange(salaId: Long) {
        val sala = _uiState.value.salas.firstOrNull { it.salaId == salaId } ?: return
        salaElegida = salaId
        HorariosCache.salaId = salaId

        val enCache = HorariosCache.datos[salaId]
        _uiState.update {
            it.copy(
                sala = sala,
                horarios = enCache?.horarios ?: emptyList(),
                ocupaciones = enCache?.ocupaciones ?: emptyList(),
                cargando = enCache == null
            )
        }

        trabajo?.cancel()
        trabajo = viewModelScope.launch {
            try {
                val datos = cargarDatos(salaId)
                publicar(_uiState.value.salas, sala, datos)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun cambiarSemana(delta: Int) {
        _uiState.update { it.copy(semana = it.semana + delta) }
    }

    fun irAHoy() {
        _uiState.update { it.copy(semana = 0) }
    }
}

